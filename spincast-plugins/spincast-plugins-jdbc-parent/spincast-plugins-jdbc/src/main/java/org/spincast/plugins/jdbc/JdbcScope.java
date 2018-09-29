package org.spincast.plugins.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JdbcScope {

    protected final Logger logger = LoggerFactory.getLogger(JdbcScope.class);

    private final static ThreadLocal<Boolean> insideTransactionScopeFlagThreadLocal = new ThreadLocal<Boolean>();
    protected final static ThreadLocal<Map<String, SpincastConnection>> spincastConnectionsThreadLocal =
            new ThreadLocal<Map<String, SpincastConnection>>();

    private final static ThreadLocal<Boolean> insideSpecificConnectionScopeFlagThreadLocal = new ThreadLocal<Boolean>();
    protected final static ThreadLocal<Map<String, Connection>> spincastSpecificConnectionsThreadLocal =
            new ThreadLocal<Map<String, Connection>>();

    private final SpincastConnectionFactory spincastConnectionFactory;

    @Inject
    public JdbcScope(SpincastConnectionFactory spincastConnectionFactory) {
        this.spincastConnectionFactory = spincastConnectionFactory;
    }

    protected SpincastConnectionFactory getSpincastConnectionFactory() {
        return this.spincastConnectionFactory;
    }

    protected String getDataSourceKey(DataSource dataSource) {
        return dataSource.getClass().getName();
    }

    public boolean isInTransactionScope() {
        Boolean result = insideTransactionScopeFlagThreadLocal.get();
        return result != null && result;
    }

    /**
     * Executes the <code>queries</code> with a Connection
     * guaranteed to not be closed during the process.
     * At the end of the process, the Connection will be
     * closed automatically.
     * <p>
     * Each query is going to be commited on the fly. Use
     * {@link #transactional(DataSource, JdbcQueries)} or
     * a {@link #transactional(TransactionalScope)} scope instead
     * if you need transactional support.
     */
    public <T> T autoCommit(DataSource dataSource, JdbcQueries<T> queries) {

        try {

            //==========================================
            // Do we have to use a specific connection?
            // (ie: started with "withConnection(...)").
            //==========================================
            Map<String, Connection> specificConnectionByDataSourceKey = spincastSpecificConnectionsThreadLocal.get();
            if (specificConnectionByDataSourceKey != null) {
                Connection specificConnection = specificConnectionByDataSourceKey.get(getDataSourceKey(dataSource));
                if (specificConnection != null) {
                    this.logger.debug("Specific connection in ThreadLocal, using it.");
                    T result = queries.run(specificConnection);
                    return result;
                }
            }

            Connection connection = dataSource.getConnection();
            if (!(connection instanceof SpincastConnection)) {
                connection = getSpincastConnectionFactory().create(connection);
            }
            connection.setAutoCommit(true);

            try {
                T result = queries.run(connection);
                return result;
            } finally {
                if (!isInTransactionScope()) {
                    ((SpincastConnection)connection).getWrappedConnection().close();
                }
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Starts a scope where only the specified connection
     * will be used (as long as {@link #autoCommit(DataSource, JdbcQueries) autoCommit()},
     * {@link #transactional(TransactionalScope) transactional()}, {@link #transactional(DataSource, JdbcQueries) transactional()},
     * {@link #specificConnection(Connection, ConnectionScope) withSpecificConnection()} or {@link #specificConnection(Connection, JdbcQueries) withSpecificConnection()}
     * are used... Not a connection taken <em>directly</em> from a DataSource.)
     */
    public <T> T specificConnection(Connection connection, DataSource dataSource, ConnectionScope<T> connectionScope) {
        Objects.requireNonNull(connection, "The connection can't be NULL");
        Objects.requireNonNull(connectionScope, "The connectionScope can't be NULL");
        Objects.requireNonNull(dataSource, "The dataSource can't be NULL");

        boolean isFirstSpecificTransactionScope = insideSpecificConnectionScopeFlagThreadLocal.get() == null;
        if (isFirstSpecificTransactionScope) {
            insideSpecificConnectionScopeFlagThreadLocal.set(true);
        }

        try {
            Map<String, Connection> specificConnectionsByDataSourceKey = spincastSpecificConnectionsThreadLocal.get();
            if (specificConnectionsByDataSourceKey == null) {
                specificConnectionsByDataSourceKey = new HashMap<String, Connection>();
                spincastSpecificConnectionsThreadLocal.set(specificConnectionsByDataSourceKey);
            }

            String dataSourceKey = getDataSourceKey(dataSource);
            Connection connectionExisting = specificConnectionsByDataSourceKey.get(dataSourceKey);
            if (connectionExisting == null) {
                specificConnectionsByDataSourceKey.put(dataSourceKey, connection);
            } else {
                connection = connectionExisting;
                this.logger.debug("Specific connection in thread locale, using it.");
            }

            return connectionScope.run(connection);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            if (isFirstSpecificTransactionScope) {
                insideSpecificConnectionScopeFlagThreadLocal.remove();
                spincastSpecificConnectionsThreadLocal.remove();
            }
        }
    }

    /**
     * Starts a scope where only the specified connection.
     * will be used (as long as {@link #autoCommit(DataSource, JdbcQueries) autoCommit()},
     * {@link #transactional(TransactionalScope) transactional()}, {@link #transactional(DataSource, JdbcQueries) transactional()},
     * {@link #specificConnection(Connection, ConnectionScope) withSpecificConnection()} or {@link #specificConnection(Connection, JdbcQueries) withSpecificConnection()}
     * are used... Not a connection taken <em>directly</em> from a DataSource.)
     */
    public <T> T specificConnection(Connection connection, DataSource dataSource, final JdbcQueries<T> queries) {
        Objects.requireNonNull(connection, "The connection can't be NULL");
        Objects.requireNonNull(queries, "The queries can't be NULL");

        return specificConnection(connection, dataSource, new ConnectionScope<T>() {

            @Override
            public T run(Connection connection) throws Exception {
                T result = queries.run(connection);
                return result;
            }
        });
    }

    /**
     * Executes the <code>queries</code> in a transaction
     * by setting the {@link Connection#setAutoCommit()} property
     * to <code>false</code>.
     */
    public <T> T transactional(final DataSource dataSource, final JdbcQueries<T> queries) {

        try {

            //==========================================
            // Do we have to use a specific connection?
            // (ie: started with "withConnection(...)").
            //==========================================
            Map<String, Connection> specificConnectionByDataSourceKey = spincastSpecificConnectionsThreadLocal.get();
            if (specificConnectionByDataSourceKey != null) {
                Connection specificConnection = specificConnectionByDataSourceKey.get(getDataSourceKey(dataSource));
                if (specificConnection != null) {
                    this.logger.debug("Specific connection in ThreadLocal, using it.");
                    T result = queries.run(specificConnection);
                    return result;
                }
            }

            return transactional(new TransactionalScope<T>() {

                @Override
                public T run() throws Exception {

                    //==========================================
                    // The correct connection will be return, a
                    // new one or one saved in the associated ThreadLocal:
                    // "getConnection()" is intercepted using AOP.
                    // @see getConnectionInterceptor() below for more
                    // details. "setAutoCommit(false);" is done there.
                    //==========================================
                    Connection connection = dataSource.getConnection();
                    if (!(connection instanceof SpincastConnection)) {
                        throw new RuntimeException("Only a Datasource which has been wrapped in a " +
                                                   SpincastDataSource.class.getName() +
                                                   " can be part of a Spincast transactionnal scope! This one isn't : " +
                                                   dataSource.getClass().getName());
                    }

                    T result = queries.run(connection);
                    return result;
                }
            });

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Starts a transactional scope.
     */
    public <T> T transactional(TransactionalScope<T> scope) {

        boolean isFirstTransactionalScope = insideTransactionScopeFlagThreadLocal.get() == null;
        if (isFirstTransactionalScope) {
            insideTransactionScopeFlagThreadLocal.set(true);
        }

        //==========================================
        // We push a new "root" SavePoint for every
        // existing connection.
        //==========================================
        Map<String, SpincastConnection> map = spincastConnectionsThreadLocal.get();
        if (map != null) {

            for (Entry<String, SpincastConnection> entry : map.entrySet()) {
                try {
                    SpincastConnection spincastConnection = entry.getValue();
                    spincastConnection.setNewRootSavePoint();
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        }

        try {

            T result = scope.run();

            //==========================================
            // Commits wrapped connections of the transaction
            //==========================================
            if (isFirstTransactionalScope) {
                map = spincastConnectionsThreadLocal.get();
                if (map != null) {
                    for (Entry<String, SpincastConnection> entry : map.entrySet()) {
                        try {
                            SpincastConnection spincastConnection = entry.getValue();
                            spincastConnection.getWrappedConnection().commit();
                        } catch (Exception ex) {
                            throw SpincastStatics.runtimize(ex);
                        }
                    }
                }
            }

            return result;

        } catch (Exception ex) {

            //==========================================
            // Rollbacks wrapped connections
            //==========================================
            map = spincastConnectionsThreadLocal.get();
            if (map != null) {
                for (Entry<String, SpincastConnection> entry : map.entrySet()) {
                    try {
                        SpincastConnection spincastConnection = entry.getValue();
                        spincastConnection.getWrappedConnection().rollback();
                    } catch (Exception ex2) {
                        this.logger.error("Error rollbacking a connection...");
                    }
                }
            }

            throw SpincastStatics.runtimize(ex);

        } finally {

            if (isFirstTransactionalScope) {

                //==========================================
                // Closes wrapped connections
                //==========================================
                map = spincastConnectionsThreadLocal.get();
                if (map != null) {
                    for (Entry<String, SpincastConnection> entry : map.entrySet()) {

                        SpincastConnection spincastConnection = entry.getValue();

                        try {
                            spincastConnection.getWrappedConnection().setAutoCommit(true);
                        } catch (Exception ex) {
                            this.logger.error("Error setAutoCommit(true) on a connection...");
                        }

                        try {
                            spincastConnection.getWrappedConnection().close();
                        } catch (Exception ex) {
                            this.logger.error("Error closing a connection...");
                        }
                    }
                }

                //==========================================
                // Removes all ThreadLocal transactionnal 
                // information.
                //==========================================
                insideTransactionScopeFlagThreadLocal.remove();
                spincastConnectionsThreadLocal.remove();
            }
        }
    }

    /**
     * Gets a Connection from a DataSource, or from the ThreadLocal cache
     * if we're inside a transaction.
     * 
     * This method is called by AOP intercepting 
     * {@link DataSource#getConnection()}.
     */
    public Connection getConnectionInterceptor(MethodInvocation invocation) {

        try {

            if (!(invocation.getThis() instanceof DataSource)) {
                throw new RuntimeException("getConnection() called on an object which is not a DataSource : " +
                                           invocation.getThis());
            }

            if (!"getConnection".equals(invocation.getMethod().getName())) {
                return (Connection)invocation.proceed();
            }

            if (!isInTransactionScope()) {
                return (Connection)invocation.proceed();
            }

            Map<String, SpincastConnection> connectionsForThisThread = spincastConnectionsThreadLocal.get();
            if (connectionsForThisThread == null) {
                connectionsForThisThread = new HashMap<String, SpincastConnection>();
                spincastConnectionsThreadLocal.set(connectionsForThisThread);
            }

            String dataSourceKey = getDataSourceKey((DataSource)invocation.getThis());
            SpincastConnection spincastConnection = connectionsForThisThread.get(dataSourceKey);
            if (spincastConnection == null) {
                Connection originalConnection = (Connection)invocation.proceed();
                originalConnection.setAutoCommit(false);

                if (!(originalConnection instanceof SpincastConnection)) {
                    spincastConnection = getSpincastConnectionFactory().create(originalConnection);
                } else {
                    spincastConnection = (SpincastConnection)originalConnection;
                }
                connectionsForThisThread.put(dataSourceKey, spincastConnection);
            }

            return spincastConnection;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


}

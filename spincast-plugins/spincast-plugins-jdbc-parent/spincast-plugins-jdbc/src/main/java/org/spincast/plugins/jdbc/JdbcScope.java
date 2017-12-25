package org.spincast.plugins.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

            Connection connection = dataSource.getConnection();
            if (!(connection instanceof SpincastConnection)) {
                connection = getSpincastConnectionFactory().create(connection);
            }

            try {
                connection.setAutoCommit(true);
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
     * Executes the <code>queries</code> in a transaction
     * by setting the {@link Connection#setAutoCommit()} property
     * to <code>false</code>.
     */
    public <T> T transactional(final DataSource dataSource, final JdbcQueries<T> queries) {

        try {

            return transactional(new TransactionalScope<T>() {

                @Override
                public T run() throws Exception {
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
                    SpincastConnection connection = entry.getValue();
                    connection.setNewRootSavePoint();
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
                            SpincastConnection connection = entry.getValue();
                            connection.getWrappedConnection().commit();
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
                        SpincastConnection connection = entry.getValue();
                        connection.getWrappedConnection().rollback();
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

                        SpincastConnection connection = entry.getValue();

                        try {
                            connection.getWrappedConnection().setAutoCommit(true);
                        } catch (Exception ex) {
                            this.logger.error("Error setAutoCommit(true) on a connection...");
                        }

                        try {
                            connection.getWrappedConnection().close();
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
     */
    public Connection getConnection(MethodInvocation invocation) {

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
                spincastConnection = getSpincastConnectionFactory().create(originalConnection);
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

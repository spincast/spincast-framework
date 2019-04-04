package org.spincast.plugins.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * A wrapper for JDBC connections.
 */
public class SpincastConnectionDefault implements SpincastConnection {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastConnectionDefault.class);

    private final Connection wrappedConnection;
    private final List<Savepoint> rootSavePoints;
    private final boolean inTransactionalScope;

    @AssistedInject
    public SpincastConnectionDefault(@Assisted Connection wrappedConnection,
                                     JdbcScope jdbcScope) {

        try {
            this.wrappedConnection = wrappedConnection;
            this.inTransactionalScope = jdbcScope.isInTransactionScope();

            this.rootSavePoints = new ArrayList<Savepoint>();
            if (this.inTransactionalScope) {
                this.rootSavePoints.add(wrappedConnection.setSavepoint());
            }

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Connection getWrappedConnection() {
        return this.wrappedConnection;
    }

    protected boolean isInTransactionnalScope() {
        return this.inTransactionalScope;
    }

    @Override
    public void close() throws SQLException {

        //==========================================
        // Unwrap one level of SavePoint...
        //==========================================
        if (this.rootSavePoints.size() > 0) {
            this.rootSavePoints.remove(this.rootSavePoints.size() - 1);
        }

        logger.debug("close() was prevented by the " + SpincastConnectionDefault.class.getSimpleName() + " wrapper");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        if (isInTransactionnalScope()) {
            logger.debug("setAutoCommit() was prevented by the " + SpincastConnectionDefault.class.getSimpleName() +
                              " wrapper");
        } else {
            this.wrappedConnection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void commit() throws SQLException {

        if (isInTransactionnalScope()) {
            logger.debug("commit() was prevented by the " + SpincastConnectionDefault.class.getSimpleName() + " wrapper");
        } else {
            this.wrappedConnection.commit();
        }
    }

    @Override
    public void setNewRootSavePoint() {

        try {
            this.rootSavePoints.add(this.wrappedConnection.setSavepoint());
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected Savepoint getCurrentRootSavepoint() {
        if (this.rootSavePoints.size() == 0) {
            return null;
        }
        return this.rootSavePoints.get(this.rootSavePoints.size() - 1);
    }

    @Override
    public void rollback() throws SQLException {

        if (isInTransactionnalScope()) {

            //==========================================
            // A rollback is allowed, but up to the
            // current "root" Savepoint only.
            //==========================================
            if (getCurrentRootSavepoint() != null) {
                this.wrappedConnection.rollback(getCurrentRootSavepoint());
            }
        } else {
            this.wrappedConnection.rollback();
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        //==========================================
        // Ok, local Savepoint...
        //==========================================
        this.wrappedConnection.rollback(savepoint);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.wrappedConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.wrappedConnection.setSavepoint(name);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.wrappedConnection.releaseSavepoint(savepoint);
    }


    //==========================================
    // Delegates...
    //==========================================

    @Override
    public boolean isClosed() throws SQLException {
        return this.wrappedConnection.isClosed();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.wrappedConnection.getAutoCommit();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.wrappedConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.wrappedConnection.isWrapperFor(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.wrappedConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.wrappedConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.wrappedConnection.nativeSQL(sql);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.wrappedConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.wrappedConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.wrappedConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.wrappedConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.wrappedConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.wrappedConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.wrappedConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.wrappedConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.wrappedConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.wrappedConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.wrappedConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.wrappedConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.wrappedConnection.getHoldability();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.wrappedConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.wrappedConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.wrappedConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.wrappedConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.wrappedConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.wrappedConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.wrappedConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.wrappedConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.wrappedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.wrappedConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.wrappedConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.wrappedConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.wrappedConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.wrappedConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.wrappedConnection.getNetworkTimeout();
    }

}

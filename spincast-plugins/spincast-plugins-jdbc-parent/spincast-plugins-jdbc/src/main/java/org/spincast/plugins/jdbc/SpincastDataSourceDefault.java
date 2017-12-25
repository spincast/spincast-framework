package org.spincast.plugins.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastDataSourceDefault implements SpincastDataSource {

    private final DataSource dataSourceOriginal;

    @AssistedInject
    public SpincastDataSourceDefault(@Assisted DataSource dataSource) {
        this.dataSourceOriginal = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSourceOriginal.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.dataSourceOriginal.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSourceOriginal.getLogWriter();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSourceOriginal.unwrap(iface);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSourceOriginal.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSourceOriginal.isWrapperFor(iface);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSourceOriginal.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSourceOriginal.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSourceOriginal.getParentLogger();
    }

}

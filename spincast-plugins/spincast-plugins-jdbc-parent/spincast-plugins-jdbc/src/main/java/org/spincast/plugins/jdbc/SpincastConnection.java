package org.spincast.plugins.jdbc;

import java.sql.Connection;

public interface SpincastConnection extends Connection {

    public Connection getWrappedConnection();

    public void setNewRootSavePoint();

}

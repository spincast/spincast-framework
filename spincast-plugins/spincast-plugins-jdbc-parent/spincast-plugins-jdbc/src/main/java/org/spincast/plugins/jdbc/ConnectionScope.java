package org.spincast.plugins.jdbc;

import java.sql.Connection;

public interface ConnectionScope<T> {

    public T run(Connection connection) throws Exception;
}

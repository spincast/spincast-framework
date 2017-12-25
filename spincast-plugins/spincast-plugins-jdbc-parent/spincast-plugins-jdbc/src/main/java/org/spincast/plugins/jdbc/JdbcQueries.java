package org.spincast.plugins.jdbc;

import java.sql.Connection;

/**
 * The JDBC queries to run inside a
 * {@link JdbcScope} wrapper.
 * <p>
 * <code>T</code> is the type of the object to
 * return. Return {@link Void} if your query/queries
 * don't return anything.
 */
public interface JdbcQueries<T> {

    public T run(Connection connection) throws Exception;

}

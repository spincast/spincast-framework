package org.spincast.plugins.jdbc.statements;

import java.util.List;

public interface DeleteStatement extends Statement {

    /**
     * Execute the delete query and return the number
     * of affected rows.
     */
    public int delete();

    /**
     * Execute the delete query and return a result set.
     * <p>
     * This can be used with a <code>RETURNING</code> clause.
     */
    public <T> List<T> delete(ResultSetHandler<T> resultSetHandler);
}

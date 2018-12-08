package org.spincast.plugins.jdbc.statements;

import java.util.List;
import java.util.Map;

public interface UpdateStatement extends Statement {

    /**
     * Execute the update and return the number
     * of affected rows.
     */
    public int update();

    /**
     * Execute the update and return a result set.
     * <p>
     * This can be used with a <code>RETURNING</code> clause.
     */
    public <T> List<T> update(ResultSetHandler<T> resultSetHandler);

    /**
     * Adds a <code>CASE</code> statement, using the key and values
     * of the given map.
     * For example :
     * <code>
     * CASE
     *     WHEN mapKey1 = 1 then mapVal1
     *     WHEN mapKey2 = 2 then mapVal2
     * END 
     * </code>
     */
    public void setCase(String paramName, String columnNameToCheck, Map<?, ?> map);

}

package org.spincast.plugins.jdbc.statements;

import java.util.Map;

public interface UpdateStatement extends Statement {

    public QueryResult update();

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

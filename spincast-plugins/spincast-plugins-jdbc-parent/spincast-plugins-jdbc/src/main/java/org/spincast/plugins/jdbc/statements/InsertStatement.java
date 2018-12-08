package org.spincast.plugins.jdbc.statements;

public interface InsertStatement extends Statement {

    /**
     * Runs the insert query, returns the number of affected rows.
     */
    public int insert();

    /**
     * Runs the insert query and return the generated id
     */
    public Long insertGetGeneratedId(String primaryKeyColumnName);

}

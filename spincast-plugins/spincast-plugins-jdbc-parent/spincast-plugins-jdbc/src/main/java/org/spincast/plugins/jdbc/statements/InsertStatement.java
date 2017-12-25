package org.spincast.plugins.jdbc.statements;

public interface InsertStatement extends Statement {

    /**
     * Runs the insert query
     */
    public QueryResult insert();

    /**
     * Runs the insert query and return the generated id
     */
    public InsertResultWithGeneratedKey insertGetGeneratedKeys(String primaryKeyName);

}

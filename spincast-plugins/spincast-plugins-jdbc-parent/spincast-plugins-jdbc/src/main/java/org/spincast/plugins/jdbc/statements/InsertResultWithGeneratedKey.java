package org.spincast.plugins.jdbc.statements;

public interface InsertResultWithGeneratedKey extends QueryResult {

    /**
     * The generated key
     */
    public Long getGeneratedKey();
}

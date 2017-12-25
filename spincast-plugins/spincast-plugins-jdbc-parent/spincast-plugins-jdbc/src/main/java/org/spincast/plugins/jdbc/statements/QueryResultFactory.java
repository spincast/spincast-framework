package org.spincast.plugins.jdbc.statements;

public interface QueryResultFactory {

    QueryResult create(int queryResult);

    InsertResultWithGeneratedKey create(int queryResult, Long generatedKey);

}

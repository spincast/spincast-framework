package org.spincast.plugins.jdbc.statements;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class QueryResultDefault implements QueryResult {

    private final int queryResult;

    @Inject
    public QueryResultDefault(@Assisted int queryResult) {
        this.queryResult = queryResult;
    }

    @Override
    public int getQueryResult() {
        return this.queryResult;
    }

}

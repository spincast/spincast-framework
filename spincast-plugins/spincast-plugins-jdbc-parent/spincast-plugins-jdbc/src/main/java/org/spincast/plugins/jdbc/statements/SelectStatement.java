package org.spincast.plugins.jdbc.statements;

import java.util.List;

import org.spincast.plugins.jdbc.utils.ItemsAndTotalCount;

public interface SelectStatement extends Statement {

    public <T> List<T> selectList(ResultSetHandler<T> resultSetHandler);

    /**
     * Executes the SELECT query but also returns the total of items.
     * It will remove the LIMIT clause if there is one to compute the total.
     * 
     * For the generated "total" query to work there are a couple of rules :
     * 
     * - The original query MUST start with SELECT and contains a FROM. All test between this SELECT keyword and
     * the FROM keyword will be removed and replaced by a COUNT(*).
     * 
     * - If the query contains a LIMIT clause, the last occurence will be remove and everything after it too.
     * 
     * If your query doesn't meet those rules, you'll have to retrieve the total another way...
     */
    public <T> ItemsAndTotalCount<T> selectListAndTotal(ResultSetHandler<T> resultSetHandler);

    public <T> T selectOne(ResultSetHandler<T> resultSetHandler);

}

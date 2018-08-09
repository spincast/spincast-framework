package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.SpincastResultSetDefault;
import org.spincast.plugins.jdbc.utils.ItemsAndTotalCount;
import org.spincast.plugins.jdbc.utils.ItemsAndTotalCountDefault;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SelectStatementDefault extends StatementBase implements SelectStatement {

    protected final static Logger logger = LoggerFactory.getLogger(SelectStatementDefault.class);

    private final JdbcUtils jdbcUtils;

    @AssistedInject
    public SelectStatementDefault(@Assisted Connection connection,
                                  QueryResultFactory queryResultFactory,
                                  JdbcUtils jdbcUtils) {
        super(connection,
              queryResultFactory);
        this.jdbcUtils = jdbcUtils;
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    protected PreparedStatement getStatementWithParamsAdded(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(getParsedQuery());

            addCurrentParamsToStatement(statement);

            return statement;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> T selectOne(ResultSetHandler<T> resultSetHandler) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                ResultSet resultSet = realStatement.executeQuery();
                try {
                    if (resultSetHandler == null) {
                        return null;
                    }

                    if (!resultSet.isBeforeFirst()) {
                        return null;
                    }

                    boolean next = resultSet.next();
                    if (!next) {
                        return null;
                    }

                    T res = resultSetHandler.handle(new SpincastResultSetDefault(resultSet));
                    return res;
                } finally {
                    close(resultSet);
                }
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> List<T> selectList(ResultSetHandler<T> resultSetHandler) {
        ItemsAndTotalCount<T> listAndTotal = selectList(resultSetHandler, false);
        return listAndTotal.getItems();
    }

    @Override
    public <T> ItemsAndTotalCount<T> selectListAndTotal(ResultSetHandler<T> resultSetHandler) {
        return selectList(resultSetHandler, true);
    }

    protected <T> ItemsAndTotalCount<T> selectList(ResultSetHandler<T> resultSetHandler, boolean getTotal) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                ResultSet resultSet = realStatement.executeQuery();
                try {
                    List<T> items = new ArrayList<>();

                    if (resultSetHandler != null && resultSet.isBeforeFirst()) {
                        while (resultSet.next()) {
                            T item = resultSetHandler.handle(new SpincastResultSetDefault(resultSet));
                            items.add(item);
                        }
                    }

                    long total = -1;
                    if (getTotal) {
                        total = getTotalFromSelectQuery(connection);
                        if (total == -1) {
                            logger.info("No generated 'total' query will be launch for this query, we'll return the number of " +
                                        "fetched items as the 'total' : " + getOriginalQuery());
                            total = items.size();
                        }
                    }

                    ItemsAndTotalCount<T> listAndTotal = new ItemsAndTotalCountDefault<>(items, total);
                    return listAndTotal;
                } finally {
                    close(resultSet);
                }
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Returns -1 to indicate the number of items should be taken instead of the total
     * found by a generated query.
     */
    protected long getTotalFromSelectQuery(Connection connection) {
        try {
            String originalQuery = getOriginalQuery();

            originalQuery = StringUtils.replaceChars(originalQuery, "\r\n\t", "   ");
            originalQuery = originalQuery.trim();

            String originalQueryLower = originalQuery.toLowerCase();
            if (!originalQueryLower.startsWith("select ")) {
                throw new RuntimeException("To use this method, your query HAS to start with 'select' : " + originalQuery);
            }

            int posFrom = originalQueryLower.indexOf(" from ");
            if (posFrom < 0) {
                throw new RuntimeException("To use this method, your query has to contain a 'from' part : " + originalQuery);
            }

            int posLast = originalQueryLower.lastIndexOf(" limit ");
            if (posLast < 0) {
                // No limit? No neec to run a 'total' query...
                return -1;
            }

            // remove "oreder by" if there is one
            int posLastOrderBy = originalQueryLower.lastIndexOf(" order by ");
            if (posLastOrderBy > 0 && posLastOrderBy < posLast) {
                posLast = posLastOrderBy;
            }

            String originalQueryBody = originalQuery.substring(posFrom, posLast);
            String totalQuery = "SELECT COUNT(*) AS total " + originalQueryBody;

            logger.debug("Generated 'Total' query : " + totalQuery);

            SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
            stm.sql(totalQuery);
            copyParamsAndStaticTokensTo(stm);

            long total = stm.selectOne(new ResultSetHandler<Long>() {

                @Override
                public Long handle(SpincastResultSet rs) throws Exception {

                    Long total = rs.getLongOrNull("total");
                    if (total == null) {
                        total = 0L;
                    }
                    return total;
                }
            });
            return total;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}

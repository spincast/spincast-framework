package org.spincast.plugins.attemptslimiter.tests.utils;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.attemptslimiter.AttemptCriteria;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcScope;
import org.spincast.plugins.jdbc.JdbcStatementFactory;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;

public class TestAttemptsRepository2 {

    @Inject
    private DataSource dataSource;

    @Inject
    private JdbcScope jdbcScope;

    @Inject
    private JdbcStatementFactory jdbcFactory;

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected JdbcScope getJdbcScope() {
        return this.jdbcScope;
    }

    protected JdbcStatementFactory getJdbcFactory() {
        return this.jdbcFactory;
    }

    public boolean isAttemptTableExists() {
        return getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Boolean>() {

            @Override
            public Boolean run(Connection connection) {

                try {
                    ResultSet rset = connection.getMetaData().getTables(null, null, "attempts", null);
                    return rset.next();
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });
    }

    public void dropAttemptsTable() {
        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcFactory().createUpdateStatement(connection);
                stm.sql("DROP TABLE IF EXISTS attempts");
                stm.update();

                return null;
            }
        });
    }

    public void clearAttemptsTable() {

        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcFactory().createUpdateStatement(connection);
                stm.sql("DELETE FROM attempts");
                stm.update();

                assertEquals(0, getTableCount());

                return null;
            }
        });
    }

    public int getTableCount() {
        return getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Integer>() {

            @Override
            public Integer run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT COUNT(*) as count FROM attempts");

                Integer count = stm.selectOne(new ResultSetHandler<Integer>() {

                    @Override
                    public Integer handle(SpincastResultSet rs) throws Exception {
                        return rs.getIntegerOrNull("count");
                    }
                });
                return count;
            }
        });
    }

    public int getAttemptsCount(String actionName, AttemptCriteria criteria) {
        return getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Integer>() {

            @Override
            public Integer run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT COUNT(*) as count " +
                        "FROM attempts " +
                        "WHERE action_name = :action_name " +
                        "AND criteria_name = :criteria_name " +
                        "AND criteria_value = :criteria_value ");
                stm.setString("action_name", actionName);
                stm.setString("criteria_name", criteria.getName());
                stm.setString("criteria_value", criteria.getValue());

                Integer count = stm.selectOne(new ResultSetHandler<Integer>() {

                    @Override
                    public Integer handle(SpincastResultSet rs) throws Exception {
                        return rs.getIntegerOrNull("count");
                    }
                });
                return count;
            }
        });
    }
}

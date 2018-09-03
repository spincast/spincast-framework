package org.spincast.plugins.attemptslimiter.tests.utils;

import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.spincast.plugins.attemptslimiter.AttemptCriteria;
import org.spincast.plugins.attemptslimiter.SpincastAttemptsLimiterPluginRepository;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcScope;
import org.spincast.plugins.jdbc.JdbcStatementFactory;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.BatchInsertStatement;
import org.spincast.plugins.jdbc.statements.DeleteStatement;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.Statement;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;

/**
 * An implementation of the
 * {@link SpincastAttemptsLimiterPluginRepository} repository
 * required by the Spincast Attempts Limiter plugin.
 * <p>
 * This implementation uses an in memory H2 database.
 */
public class SpincastAttemptsLimiterPluginRepositoryTesting implements SpincastAttemptsLimiterPluginRepository {

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

    /**
     * Creates the "attempts" table.
     */
    public void createAttemptTable() {
        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcFactory().createUpdateStatement(connection);

                stm.sql("CREATE TABLE attempts ( " +
                        "   action_name VARCHAR(255) NOT NULL, " +
                        "   criteria_name VARCHAR(255) NOT NULL, " +
                        "   criteria_value VARCHAR(255) NOT NULL, " +
                        "   creation_date TIMESTAMP WITH TIME ZONE NOT NULL " +
                        ")");
                stm.update();

                stm.clearSql();
                stm.sql("CREATE INDEX attempt_index_full ON attempts(action_name, criteria_name, criteria_value, creation_date)");
                stm.update();

                stm.clearSql();
                stm.sql("CREATE INDEX attempt_index_date ON attempts(action_name, creation_date)");
                stm.update();

                return null;
            }
        });
    }

    @Override
    public Map<String, Integer> getAttemptsNumberPerCriteriaSince(String actionName,
                                                                  Instant sinceDate,
                                                                  AttemptCriteria... criterias) {

        Map<String, Integer> map = getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Map<String, Integer>>() {

            @Override
            public Map<String, Integer> run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);

                stm.sql("SELECT criteria_name, COUNT(*) as nbr " +
                        "FROM attempts " +
                        "WHERE action_name = :action_name ");
                stm.setString("action_name", actionName);

                addAttemptCriteriasClause(stm, criterias);

                stm.sql("AND creation_date > :sinceDate ");
                stm.setInstant("sinceDate", sinceDate);

                stm.sql("GROUP BY criteria_name ");

                Map<String, Integer> map = new HashMap<String, Integer>();

                stm.selectList(new ResultSetHandler<Void>() {

                    @Override
                    public Void handle(SpincastResultSet rs) throws Exception {

                        String criteriaName = rs.getString("criteria_name");
                        Integer nbr = rs.getIntegerOrZero("nbr");

                        map.put(criteriaName, nbr);

                        return null;
                    }
                });

                return map;
            }
        });

        return map;
    }

    @Override
    public void saveNewAttempt(String actionName, AttemptCriteria... criterias) {

        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                BatchInsertStatement stm =
                        getJdbcFactory().createBatchInsertStatement(connection);

                stm.sql("INSERT INTO attempts(action_name, criteria_name, criteria_value, creation_date) " +
                        "VALUES(:action_name, :criteria_name, :criteria_value, :creation_date)");

                for (AttemptCriteria criteria : criterias) {
                    stm.setString("action_name", actionName);
                    stm.setString("criteria_name", criteria.getName());
                    stm.setString("criteria_value", criteria.getValue());
                    stm.setInstant("creation_date", Instant.now());
                    stm.addBatch();
                }

                stm.batchInsert();

                return null;
            }
        });
    }

    @Override
    public void deleteAttemptsOlderThan(String actionName, Instant sinceDate) {
        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                DeleteStatement stm =
                        getJdbcFactory().createDeleteStatement(connection);

                stm.sql("DELETE FROM attempts " +
                        "WHERE action_name = :action_name " +
                        "AND creation_date < :since ");

                stm.setString("action_name", actionName);
                stm.setInstant("since", sinceDate);

                stm.delete();

                return null;
            }
        });
    }

    @Override
    public void deleteAttempts(String actionName, AttemptCriteria... criterias) {
        getJdbcScope().autoCommit(getDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                DeleteStatement stm =
                        getJdbcFactory().createDeleteStatement(connection);

                stm.sql("DELETE FROM attempts " +
                        "WHERE action_name = :action_name ");
                stm.setString("action_name", actionName);

                addAttemptCriteriasClause(stm, criterias);

                stm.delete();

                return null;
            }
        });
    }

    protected void addAttemptCriteriasClause(Statement stm, AttemptCriteria... criterias) {
        if (criterias != null && criterias.length > 0) {
            stm.sql("AND ( ");
            for (int i = 0; i < criterias.length; i++) {

                if (i > 0) {
                    stm.sql(" OR ");
                }

                AttemptCriteria criteria = criterias[i];
                stm.sql("( " +
                        "   criteria_name = :criteria_name_" + i + " " +
                        "   AND " +
                        "   criteria_value = :criteria_value_" + i + " " +
                        ") ");

                stm.setString("criteria_name_" + i, criteria.getName());
                stm.setString("criteria_value_" + i, criteria.getValue());
            }
            stm.sql(") ");
        }
    }
}

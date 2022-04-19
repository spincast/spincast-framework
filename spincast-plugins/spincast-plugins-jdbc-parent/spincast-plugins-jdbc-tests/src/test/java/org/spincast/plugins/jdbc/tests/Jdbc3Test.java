package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.BatchInsertStatement;
import org.spincast.plugins.jdbc.statements.DeleteStatement;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

public class Jdbc3Test extends JdbcPostgresqlTestBase {

    @Test
    public void deleteGetAffectedRows() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate) " +
                        "VALUES('Stromgol', :birthdate)");
                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.insert();

                stm.sql("INSERT INTO test(name, birthdate) " +
                        "VALUES('Stromgol', :birthdate)");
                stm.setInstant("birthdate", now);
                stm.insert();

                DeleteStatement stmDel = getJdbcUtils().statements().createDeleteStatement(connection);

                stmDel.sql("DELETE " +
                           "FROM test " +
                           "WHERE name = :name");
                stmDel.setString("name", "Stromgol");

                int affectedRowsNbr = stmDel.delete();
                assertEquals(2, affectedRowsNbr);

                return null;
            }
        });
    }

    @Test
    public void deleteGetResultSet() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, 'aaa@aaa.ca')");
                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.insert();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, 'bbb@bbb.ca')");
                stm.setInstant("birthdate", now);
                stm.insert();

                DeleteStatement stmDel = getJdbcUtils().statements().createDeleteStatement(connection);

                stmDel.sql("DELETE " +
                           "FROM test " +
                           "WHERE name = 'Stromgol' " +
                           "RETURNING email ");

                List<String> emails = stmDel.delete(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("email");
                    }
                });

                assertEquals(2, emails.size());
                Set<String> set = new HashSet<String>(emails);
                assertTrue(set.contains("aaa@aaa.ca"));
                assertTrue(set.contains("bbb@bbb.ca"));

                return null;
            }
        });
    }

    @Test
    public void updateGetAffectedRows() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate) " +
                        "VALUES('Stromgol', :birthdate)");
                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.insert();

                stm.sql("INSERT INTO test(name, birthdate) " +
                        "VALUES('Stromgol', :birthdate)");
                stm.setInstant("birthdate", now);
                stm.insert();

                UpdateStatement stm2 = getJdbcUtils().statements().createUpdateStatement(connection);

                stm2.sql("UPDATE test " +
                         "SET nbr = 1 " +
                         "WHERE name = 'Stromgol'");

                int affectedRowsNbr = stm2.update();
                assertEquals(2, affectedRowsNbr);

                return null;
            }
        });
    }

    @Test
    public void updateGetResultSet() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, 'aaa@aaa.ca')");
                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.insert();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, 'bbb@bbb.ca')");
                stm.setInstant("birthdate", now);
                stm.insert();

                UpdateStatement stm2 = getJdbcUtils().statements().createUpdateStatement(connection);

                stm2.sql("UPDATE test " +
                         "SET nbr = 1 " +
                         "WHERE name = 'Stromgol' " +
                         "RETURNING email");

                List<String> emails = stm2.update(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("email");
                    }
                });

                assertEquals(2, emails.size());
                Set<String> set = new HashSet<String>(emails);
                assertTrue(set.contains("aaa@aaa.ca"));
                assertTrue(set.contains("bbb@bbb.ca"));

                return null;
            }
        });
    }

    @Test
    public void batchInsertGetAffectedRows() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                BatchInsertStatement stm = getJdbcUtils().statements().createBatchInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, :email)");

                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.setString("email", "aaa@aaa.ca");
                stm.addBatch();

                stm.setInstant("birthdate", now);
                stm.setString("email", "bbb@bbb.ca");
                stm.addBatch();

                int[] affectedRowsNbr = stm.batchInsert();
                assertEquals(2, affectedRowsNbr.length);

                return null;
            }
        });
    }

    @Test
    public void batchInsertGetGeneratedIds() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                BatchInsertStatement stm = getJdbcUtils().statements().createBatchInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate, email) " +
                        "VALUES('Stromgol', :birthdate, :email)");

                Instant now = Instant.now();
                stm.setInstant("birthdate", now);
                stm.setString("email", "aaa@aaa.ca");
                stm.addBatch();

                stm.setInstant("birthdate", now);
                stm.setString("email", "bbb@bbb.ca");
                stm.addBatch();

                List<Long> generatedKeys = stm.batchInsert("id");

                assertEquals(2, generatedKeys.size());

                assertNotNull(generatedKeys.get(0));
                assertNotNull(generatedKeys.get(1));

                return null;
            }
        });
    }
}

package org.spincast.plugins.flywayutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spincast.plugins.flywayutils.SpincastFlywayMigrationContext;
import org.spincast.plugins.flywayutils.tests.migrations8.M_2018_08_13__toto;
import org.spincast.plugins.flywayutils.tests.migrations8b.M_2018_08_15;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MigrationTest extends TestBase {

    @Override
    protected boolean isClearDatabaseBeforeTest() {
        return false;
    }

    @Test
    public void t01_migrationValid() throws Exception {
        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  M_2018_08_13__toto.class.getPackage().getName());
        migrationContext.migrate();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO users(first_name, last_name) " +
                        "VALUES ('Stromgol', 'Laroche') ");
                stm.insert();

                SelectStatement stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM users " +
                         "WHERE first_name = 'Stromgol' ");

                String lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertEquals("Laroche", lastName);
                return null;
            }
        });
    }

    @Test
    public void t02_migrationAddMigrationFile() throws Exception {
        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  M_2018_08_15.class.getPackage().getName());
        migrationContext.migrate();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                SelectStatement stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM users " +
                         "WHERE first_name = 'Stromgol' ");

                String lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertEquals("Alien", lastName);
                return null;
            }
        });
    }

    @Test
    public void t03_customSchema() throws Exception {
        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  "myCustomSchema",
                                                                  M_2018_08_13__toto.class.getPackage().getName());
        migrationContext.migrate();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                SelectStatement stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM myCustomSchema.users " +
                         "WHERE first_name = 'Stromgol' ");

                String lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertNull(lastName);


                stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM users " +
                         "WHERE first_name = 'Stromgol' ");

                lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertEquals("Alien", lastName);


                //==========================================
                // Insert ins chema
                //==========================================
                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO myCustomSchema.users(first_name, last_name) " +
                        "VALUES ('Stromgol', 'Laroche') ");
                stm.insert();

                stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM myCustomSchema.users " +
                         "WHERE first_name = 'Stromgol' ");

                lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertEquals("Laroche", lastName);

                stm2 = getJdbcUtils().statements().createSelectStatement(connection);
                stm2.sql("SELECT last_name FROM users " +
                         "WHERE first_name = 'Stromgol' ");

                lastName = stm2.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {

                        return rs.getString("last_name");
                    }
                });
                assertEquals("Alien", lastName);

                return null;
            }
        });
    }
}

package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.List;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.QueryResult;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;

public class JdbcTest extends JdbcTestBase {

    @Test
    public void insertSimple() throws Exception {

        String name = getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());


                SelectStatement stmSel = getJdbcUtils().statements().createSelectStatement(connection);

                stmSel.sql("SELECT name " +
                           "FROM test " +
                           "WHERE " +
                           " email = :email");

                stmSel.setString("email", "yo@example.com");

                List<String> names = stmSel.selectList(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(names);
                assertEquals(1, names.size());

                return names.get(0);
            }
        });

        assertEquals("Stromgol", name);
    }

    @Test
    public void regularScopeSecondInsertFails() throws Exception {

        try {
            getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) {

                    InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    result = stm.insert();
                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        String name = getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT name " +
                        "FROM test " +
                        "WHERE " +
                        " email = :email");

                stm.setString("email", "yo@example.com");

                String name = stm.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                return name;
            }
        });

        assertEquals("Stromgol", name);
    }

    @Test
    public void transactionScopeValid() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void transactionScopeSecondInsertFails() throws Exception {

        try {
            getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) {

                    InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    result = stm.insert();
                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        assertEquals(0, getTestTableCount());
    }

    @Test
    public void connectionCantBeClosedInScopeAutoCommit() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Try to close the connection!
                connection.close();
                connection.rollback();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void connectionCantBeClosedInScopeTransaction() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Try to close the connection!
                connection.close();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void connectionCantBeCommitedManuallyInScopeTransaction() throws Exception {

        try {
            getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) throws Exception {

                    InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    // Try to commit the connection!
                    connection.commit();

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        assertEquals(0, getTestTableCount());
    }

    @Test
    public void manualRollbackInScopeTransaction() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Rollback!
                connection.rollback();

                // The next queries will works though...

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol3', 'yo3@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void manualRollbackInScopeTransactionThirdInsertFails() throws Exception {

        try {
            getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) throws Exception {

                    InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    // Rollback!
                    connection.rollback();

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol2', 'yo2@example.com')");

                    result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        assertEquals(0, getTestTableCount());
    }

    @Test
    public void rollbackUpToTheCurrentRootSavepointOnly() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Titi', 'titi@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                assertEquals(1, getTestTableCount());

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        assertEquals(1, getTestTableCount());

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        assertEquals(2, getTestTableCount());

                        // Rollback!
                        connection.rollback();

                        assertEquals(1, getTestTableCount());

                        // The next queries will works though...

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        assertEquals(2, getTestTableCount());

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol3', 'yo3@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        assertEquals(3, getTestTableCount());

                        return null;
                    }
                });

                return null;
            }
        });

        assertEquals(3, getTestTableCount());
    }

    @Test
    public void localSavepointsAreValid() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                Savepoint localSavepoint = connection.setSavepoint();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Rollback!
                connection.rollback(localSavepoint);

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol3', 'yo3@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void localSavepointsCalledFromAnotherScope() throws Exception {

        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Titi', 'titi@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        Savepoint localSavepoint = connection.setSavepoint();

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        // Rollback!
                        connection.rollback(localSavepoint);

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol3', 'yo3@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        return null;
                    }
                });

                return null;
            }
        });

        assertEquals(3, getTestTableCount());
    }

}

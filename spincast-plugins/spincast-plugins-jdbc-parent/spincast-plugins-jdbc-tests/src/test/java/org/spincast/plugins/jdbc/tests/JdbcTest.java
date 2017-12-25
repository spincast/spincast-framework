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

        String name = getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());


                SelectStatement stmSel = getJdbcFactory().createSelectStatement(connection);

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
            getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) {

                    InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    result = stm.insert();
                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        String name = getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                stm = getJdbcFactory().createInsertStatement(connection);
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
            getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) {

                    InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Try to close the connection!
                connection.close();
                connection.rollback();

                stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Try to close the connection!
                connection.close();

                stm = getJdbcFactory().createInsertStatement(connection);
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
            getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) throws Exception {

                    InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    // Try to commit the connection!
                    connection.commit();

                    stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Rollback!
                connection.rollback();

                // The next queries will works though...

                stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                stm = getJdbcFactory().createInsertStatement(connection);
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
            getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                @Override
                public Void run(Connection connection) throws Exception {

                    InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol', 'yo@example.com')");

                    QueryResult result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    // Rollback!
                    connection.rollback();

                    stm = getJdbcFactory().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol2', 'yo2@example.com')");

                    result = stm.insert();
                    assertEquals(1, result.getQueryResult());

                    stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Titi', 'titi@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                assertEquals(1, getTestTableCount());

                getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        assertEquals(1, getTestTableCount());

                        InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        assertEquals(2, getTestTableCount());

                        // Rollback!
                        connection.rollback();

                        assertEquals(1, getTestTableCount());

                        // The next queries will works though...

                        stm = getJdbcFactory().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        assertEquals(2, getTestTableCount());

                        stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                Savepoint localSavepoint = connection.setSavepoint();

                stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                result = stm.insert();
                assertEquals(1, result.getQueryResult());

                // Rollback!
                connection.rollback(localSavepoint);

                stm = getJdbcFactory().createInsertStatement(connection);
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

        getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Titi', 'titi@example.com')");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                getJdbcScope().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcFactory().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        Savepoint localSavepoint = connection.setSavepoint();

                        stm = getJdbcFactory().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        // Rollback!
                        connection.rollback(localSavepoint);

                        stm = getJdbcFactory().createInsertStatement(connection);
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

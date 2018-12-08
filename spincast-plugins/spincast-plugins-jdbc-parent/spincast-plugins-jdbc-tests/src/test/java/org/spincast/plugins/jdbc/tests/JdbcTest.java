package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;

public class JdbcTest extends JdbcH2TestBase {

    @Test
    public void insertSimple() throws Exception {

        String name = getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) {

                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol', 'yo@example.com')");

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                    int affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    affectedRowsNbr = stm.insert();
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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                    int affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    affectedRowsNbr = stm.insert();
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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                // Try to close the connection!
                connection.close();
                connection.rollback();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                // Try to close the connection!
                connection.close();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                    int affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

                    // Try to commit the connection!
                    connection.commit();

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                // Rollback!
                connection.rollback();

                // The next queries will works though...

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol3', 'yo3@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                    int affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

                    // Rollback!
                    connection.rollback();

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO test(name, email) " +
                            "VALUES('Stromgol2', 'yo2@example.com')");

                    affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

                    stm = getJdbcUtils().statements().createInsertStatement(connection);
                    stm.sql("INSERT INTO nope(name, email) " +
                            "VALUES('nope', 'nope')");

                    affectedRowsNbr = stm.insert();
                    assertEquals(1, affectedRowsNbr);

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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                assertEquals(1, getTestTableCount());

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        assertEquals(1, getTestTableCount());

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        int affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

                        assertEquals(2, getTestTableCount());

                        // Rollback!
                        connection.rollback();

                        assertEquals(1, getTestTableCount());

                        // The next queries will works though...

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

                        assertEquals(2, getTestTableCount());

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol3', 'yo3@example.com')");

                        affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                Savepoint localSavepoint = connection.setSavepoint();

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol2', 'yo2@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                // Rollback!
                connection.rollback(localSavepoint);

                stm = getJdbcUtils().statements().createInsertStatement(connection);
                stm.sql("INSERT INTO test(name, email) " +
                        "VALUES('Stromgol3', 'yo3@example.com')");

                affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

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

                int affectedRowsNbr = stm.insert();
                assertEquals(1, affectedRowsNbr);

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol', 'yo@example.com')");

                        int affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

                        Savepoint localSavepoint = connection.setSavepoint();

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'yo2@example.com')");

                        affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

                        // Rollback!
                        connection.rollback(localSavepoint);

                        stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol3', 'yo3@example.com')");

                        affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);

                        return null;
                    }
                });

                return null;
            }
        });

        assertEquals(3, getTestTableCount());
    }

    @Test
    public void multipleNestedTransactions() throws Exception {

        String uuid = UUID.randomUUID().toString();


        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {


                getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Stromgol2', 'email2')");

                        int affectedRowsNbr = stm.insert();
                        assertEquals(1, affectedRowsNbr);
                        return null;
                    }
                });

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {
                        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                            @Override
                            public Void run(Connection connection) throws Exception {

                                InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                                stm.sql("INSERT INTO test(name, email) " +
                                        "VALUES('Stromgol', :email)");
                                stm.setString("email", uuid);

                                int affectedRowsNbr = stm.insert();
                                assertEquals(1, affectedRowsNbr);
                                return null;
                            }
                        });
                        return null;
                    }
                });
                return null;
            }
        });

        String email = getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<String>() {

            @Override
            public String run(Connection connection) throws Exception {
                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT email " +
                        "FROM test " +
                        "WHERE name = 'Stromgol' ");

                String email = stm.selectOne(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("email");
                    }
                });
                return email;
            }
        });
        assertEquals(uuid, email);
    }

}

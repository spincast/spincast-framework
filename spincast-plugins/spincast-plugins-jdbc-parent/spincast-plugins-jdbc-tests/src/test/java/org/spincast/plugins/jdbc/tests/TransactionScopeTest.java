package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.TransactionalScope;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.QueryResult;

public class TransactionScopeTest extends JdbcTestBase {

    @Test
    public void testSimple() throws Exception {

        getJdbcUtils().scopes().transactional(new TransactionalScope<Void>() {

            @Override
            public Void run() throws Exception {

                //==========================================
                // First inner Scope
                //==========================================
                getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Titi', 'titi@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        return null;
                    }
                });

                //==========================================
                // Second inner Scope
                //==========================================
                getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) throws Exception {

                        InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                        stm.sql("INSERT INTO test(name, email) " +
                                "VALUES('Titi2', 'titi2@example.com')");

                        QueryResult result = stm.insert();
                        assertEquals(1, result.getQueryResult());

                        return null;
                    }
                });

                return null;
            }
        });

        assertEquals(2, getTestTableCount());
    }

    @Test
    public void testExceptionInSecondScope() throws Exception {

        try {
            getJdbcUtils().scopes().transactional(new TransactionalScope<Void>() {

                @Override
                public Void run() throws Exception {

                    //==========================================
                    // First inner Scope
                    //==========================================
                    getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                        @Override
                        public Void run(Connection connection) throws Exception {

                            InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                            stm.sql("INSERT INTO test(name, email) " +
                                    "VALUES('Titi', 'titi@example.com')");

                            QueryResult result = stm.insert();
                            assertEquals(1, result.getQueryResult());

                            return null;
                        }
                    });

                    //==========================================
                    // Second inner Scope
                    //==========================================
                    getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                        @Override
                        public Void run(Connection connection) throws Exception {

                            InsertStatement stm = getJdbcUtils().statements().createInsertStatement(connection);
                            stm.sql("INSERT INTO nope(name, email) " +
                                    "VALUES('nope', 'nope@example.com')");

                            QueryResult result = stm.insert();
                            assertEquals(1, result.getQueryResult());

                            return null;
                        }
                    });
                    return null;
                }
            });
            fail();
        } catch (Exception ex) {
        }

        //==========================================
        // The first inner scope must not be commited even if
        // "autoCommit" is true, since it is part of
        // a transaction scope parent.
        //==========================================
        assertEquals(0, getTestTableCount());
    }


}

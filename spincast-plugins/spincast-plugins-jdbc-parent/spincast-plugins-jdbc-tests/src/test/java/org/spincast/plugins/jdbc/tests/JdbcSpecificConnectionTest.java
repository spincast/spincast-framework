package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.TransactionalScope;

public class JdbcSpecificConnectionTest extends JdbcTestBase {

    @Test
    public void specificConnection() throws Exception {

        Connection connectionBase = getTestDataSource().getConnection();

        getJdbcUtils().scopes().specificConnection(connectionBase, getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {
                assertEquals(connectionBase, connection);
                getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) {
                        assertEquals(connectionBase, connection);

                        getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                            @Override
                            public Void run(Connection connection) {
                                assertEquals(connectionBase, connection);
                                return null;
                            }
                        });

                        return null;
                    }
                });

                getJdbcUtils().scopes().transactional(getTestDataSource(), new JdbcQueries<Void>() {

                    @Override
                    public Void run(Connection connection) {
                        assertEquals(connectionBase, connection);
                        return null;
                    }
                });

                getJdbcUtils().scopes().transactional(new TransactionalScope<Void>() {

                    @Override
                    public Void run() throws Exception {
                        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

                            @Override
                            public Void run(Connection connection) {
                                assertEquals(connectionBase, connection);
                                return null;
                            }
                        });
                        return null;
                    }
                });

                return null;
            }
        });
    }
}

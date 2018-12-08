package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.BatchInsertStatement;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.utils.ItemsAndTotalCount;

public class JdbcTotalCountTest extends JdbcH2TestBase {

    @Override
    public void beforeTest() {
        // nothing
        //super.beforeTest();
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                BatchInsertStatement stm = getJdbcUtils().statements().createBatchInsertStatement(connection);

                stm.sql("INSERT INTO test(name, nbr) " +
                        "VALUES(:name, :nbr)");

                stm.setString("name", "Stromgol1");
                stm.setInteger("nbr", 1);
                stm.addBatch();

                stm.setString("name", "Stromgol1");
                stm.setInteger("nbr", 2);
                stm.addBatch();

                stm.setString("name", "Stromgol1");
                stm.setInteger("nbr", 3);
                stm.addBatch();

                stm.setString("name", "Stromgol2");
                stm.setInteger("nbr", 1);
                stm.addBatch();

                stm.setString("name", "Stromgol3");
                stm.setInteger("nbr", 1);
                stm.addBatch();

                stm.setString("name", "Stromgol3");
                stm.setInteger("nbr", 4);
                stm.addBatch();

                stm.setString("name", "Nope");
                stm.setInteger("nbr", 1);
                stm.addBatch();

                stm.batchInsert();

                return null;
            }
        });
    }

    @Test
    public void getTotalCountNoLimit() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT name " +
                        "FROM test " +
                        "WHERE name LIKE 'Stromgol%' ");

                ItemsAndTotalCount<String> itemsAndTotalCount = stm.selectListAndTotal(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(itemsAndTotalCount);
                assertEquals(6, itemsAndTotalCount.getTotalCount());
                assertNotNull(itemsAndTotalCount.getItems());
                assertEquals(6, itemsAndTotalCount.getItems().size());

                return null;
            }
        });
    }

    @Test
    public void getTotalCountLimit() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT name " +
                        "FROM test " +
                        "WHERE name LIKE 'Stromgol%' " +
                        "LIMIT 3 ");

                ItemsAndTotalCount<String> itemsAndTotalCount = stm.selectListAndTotal(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(itemsAndTotalCount);
                assertEquals(6, itemsAndTotalCount.getTotalCount());
                assertNotNull(itemsAndTotalCount.getItems());
                assertEquals(3, itemsAndTotalCount.getItems().size());

                return null;
            }
        });
    }

    @Test
    public void getTotalCountLimitAndOffset() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT name " +
                        "FROM test " +
                        "WHERE name LIKE 'Stromgol%' " +
                        "LIMIT 3 " +
                        "OFFSET 5");

                ItemsAndTotalCount<String> itemsAndTotalCount = stm.selectListAndTotal(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(itemsAndTotalCount);
                assertEquals(6, itemsAndTotalCount.getTotalCount());
                assertNotNull(itemsAndTotalCount.getItems());
                assertEquals(1, itemsAndTotalCount.getItems().size());

                return null;
            }
        });
    }

    @Test
    public void getTotalCountDistinct() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT DISTINCT name " +
                        "FROM test " +
                        "WHERE name LIKE 'Stromgol%' " +
                        "LIMIT 2 ");

                ItemsAndTotalCount<String> itemsAndTotalCount = stm.selectListAndTotal(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(itemsAndTotalCount);
                assertEquals(3, itemsAndTotalCount.getTotalCount());
                assertNotNull(itemsAndTotalCount.getItems());
                assertEquals(2, itemsAndTotalCount.getItems().size());

                return null;
            }
        });
    }

    @Test
    public void getTotalCountGroupBy() throws Exception {

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT name " +
                        "FROM test " +
                        "WHERE name LIKE 'Stromgol%' " +
                        "GROUP BY name " +
                        "LIMIT 2 ");

                ItemsAndTotalCount<String> itemsAndTotalCount = stm.selectListAndTotal(new ResultSetHandler<String>() {

                    @Override
                    public String handle(SpincastResultSet rs) throws Exception {
                        return rs.getString("name");
                    }
                });

                assertNotNull(itemsAndTotalCount);
                assertEquals(3, itemsAndTotalCount.getTotalCount());
                assertNotNull(itemsAndTotalCount.getItems());
                assertEquals(2, itemsAndTotalCount.getItems().size());

                return null;
            }
        });
    }


}

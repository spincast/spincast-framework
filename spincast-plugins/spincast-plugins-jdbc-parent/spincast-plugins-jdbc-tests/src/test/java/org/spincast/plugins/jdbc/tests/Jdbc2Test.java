package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.time.Instant;

import org.junit.Test;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.QueryResult;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;

import com.google.common.collect.Sets;

public class Jdbc2Test extends JdbcTestBase {

    @Test
    public void setInString() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT * FROM a WHERE id IN(:test)");
                stm.setInString("test", Sets.newHashSet("a", "b", "c"));

                String sql = stm.getSql(false);
                assertEquals("SELECT * FROM a WHERE id IN('a','b','c')", sql);

                return null;
            }
        });
    }

    @Test
    public void setInInteger() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT * FROM a WHERE id IN(:test)");
                stm.setInInteger("test", Sets.newHashSet(1, 2, 3));

                String sql = stm.getSql(false);
                assertEquals("SELECT * FROM a WHERE id IN(1,2,3)", sql);

                return null;
            }
        });
    }

    @Test
    public void setInLong() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT * FROM a WHERE id IN(:test)");
                stm.setInLong("test", Sets.newHashSet(1L, 2L, 3L));

                String sql = stm.getSql(false);
                assertEquals("SELECT * FROM a WHERE id IN(1,2,3)", sql);

                return null;
            }
        });
    }

    @Test
    public void clearSql() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                SelectStatement stm = getJdbcFactory().createSelectStatement(connection);
                stm.sql("SELECT * FROM a");
                String sql = stm.getSql(false);
                assertEquals("SELECT * FROM a", sql);

                stm.clearSql();
                sql = stm.getSql(false);
                assertEquals("", sql);

                stm.sql("SELECT * FROM b");
                sql = stm.getSql(false);
                assertEquals("SELECT * FROM b", sql);

                return null;
            }
        });
    }

    @Test
    public void orNull() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, nbr2, nbr3, booo2, booo3) " +
                        "VALUES('Stromgol', 123, 0, true, false)");

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                SelectStatement stmSel = getJdbcFactory().createSelectStatement(connection);

                stmSel.sql("SELECT name, email, birthdate, nbr, nbr2, nbr3, booo, booo2, booo3 " +
                           "FROM test " +
                           "WHERE " +
                           " name = :name");
                stmSel.setString("name", "Stromgol");

                stmSel.selectOne(new ResultSetHandler<Void>() {

                    @Override
                    public Void handle(SpincastResultSet rs) throws Exception {

                        Boolean bool = rs.getBooleanOrNull("booo");
                        assertNull(bool);
                        bool = rs.getBooleanOrNull(7);
                        assertNull(bool);

                        Boolean bool2 = rs.getBooleanOrNull("booo2");
                        assertEquals(true, bool2);
                        bool2 = rs.getBooleanOrNull(8);
                        assertEquals(true, bool2);

                        Boolean bool3 = rs.getBooleanOrNull("booo3");
                        assertEquals(false, bool3);
                        bool3 = rs.getBooleanOrNull(9);
                        assertEquals(false, bool3);

                        Integer nbr = rs.getIntOrNull("nbr");
                        assertNull(nbr);

                        Integer nbr2 = rs.getIntOrNull("nbr2");
                        assertEquals((Integer)123, nbr2);

                        Integer nbr3 = rs.getIntOrNull("nbr3");
                        assertEquals((Integer)0, nbr3);

                        Long nbrl = rs.getLongOrNull("nbr");
                        assertNull(nbrl);

                        Long nbr2l = rs.getLongOrNull("nbr2");
                        assertEquals((Long)123L, nbr2l);

                        Long nbr3l = rs.getLongOrNull("nbr3");
                        assertEquals((Long)0L, nbr3l);

                        Byte nbrb = rs.getByteOrNull("nbr");
                        assertNull(nbrb);

                        Byte nbr2b = rs.getByteOrNull("nbr2");
                        assertEquals(new Byte("123"), nbr2b);

                        Byte nbr3b = rs.getByteOrNull("nbr3");
                        assertEquals(new Byte("0"), nbr3b);

                        Short nbrs = rs.getShortOrNull("nbr");
                        assertNull(nbrs);

                        Short nbr2s = rs.getShortOrNull("nbr2");
                        assertEquals(new Short("123"), nbr2s);

                        Short nbr3s = rs.getShortOrNull("nbr3");
                        assertEquals(new Short("0"), nbr3s);

                        Float nbrf = rs.getFloatOrNull("nbr");
                        assertNull(nbrf);

                        Float nbr2f = rs.getFloatOrNull("nbr2");
                        assertEquals(new Float("123"), nbr2f);

                        Float nbr3f = rs.getFloatOrNull("nbr3");
                        assertEquals(new Float("0"), nbr3f);

                        Double nbrd = rs.getDoubleOrNull("nbr");
                        assertNull(nbrd);

                        Double nbr2d = rs.getDoubleOrNull("nbr2");
                        assertEquals(new Double("123"), nbr2d);

                        Double nbr3d = rs.getDoubleOrNull("nbr3");
                        assertEquals(new Double("0"), nbr3d);

                        return null;
                    }
                });

                return null;
            }
        });
    }

    @Test
    public void instant() throws Exception {

        getJdbcScope().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                InsertStatement stm = getJdbcFactory().createInsertStatement(connection);

                stm.sql("INSERT INTO test(name, birthdate) " +
                        "VALUES('Stromgol', :birthdate)");

                Instant now = Instant.now();
                stm.setInstant("birthdate", now);

                QueryResult result = stm.insert();
                assertEquals(1, result.getQueryResult());

                SelectStatement stmSel = getJdbcFactory().createSelectStatement(connection);

                stmSel.sql("SELECT name, birthdate " +
                           "FROM test " +
                           "WHERE " +
                           " name = :name");
                stmSel.setString("name", "Stromgol");

                stmSel.selectOne(new ResultSetHandler<Void>() {

                    @Override
                    public Void handle(SpincastResultSet rs) throws Exception {

                        Instant birthdate = rs.getInstant("birthdate");
                        assertNotNull(birthdate);
                        assertEquals(now, birthdate);

                        return null;
                    }
                });

                return null;
            }
        });
    }

}

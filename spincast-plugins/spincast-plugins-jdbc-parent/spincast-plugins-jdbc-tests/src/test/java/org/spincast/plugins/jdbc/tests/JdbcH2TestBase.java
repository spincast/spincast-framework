package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastJdbcPlugin;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.UpdateStatement;
import org.spincast.testing.core.h2.SpincastTestingH2;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public abstract class JdbcH2TestBase extends NoAppTestingBase {

    @Inject
    protected SpincastTestingH2 spincastTestingH2;

    protected SpincastTestingH2 getH2() {
        return this.spincastTestingH2;
    }

    @Inject
    private DataSource testDataSource;

    @Inject
    private JdbcUtils jdbcUtils;

    protected DataSource getTestDataSource() {
        return this.testDataSource;
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .plugin(new SpincastJdbcPlugin())
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               //==========================================
                               // Enable Spincast H2
                               //==========================================
                               bind(DataSource.class).toProvider(SpincastTestingH2.class).in(Scopes.SINGLETON);
                           }
                       })
                       .init(new String[]{});
    }

    @Override
    public void beforeClass() {

        super.beforeClass();

        getH2().clearDatabase();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("CREATE TABLE test (" +
                        "   id SERIAL PRIMARY KEY, " +
                        "   name VARCHAR(255) NOT NULL, " +
                        "   email VARCHAR(255) UNIQUE," +
                        "   birthdate TIMESTAMP(9) WITH TIME ZONE NOT NULL DEFAULT NOW()," +
                        "   nbr INTEGER," +
                        "   nbr2 INTEGER," +
                        "   nbr3 INTEGER," +
                        "   booo BOOLEAN," +
                        "   booo2 BOOLEAN," +
                        "   booo3 BOOLEAN" +
                        ")");

                stm.update();

                return null;
            }
        });

        boolean tableExists = getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Boolean>() {

            @Override
            public Boolean run(Connection connection) {

                try {
                    ResultSet rset = connection.getMetaData().getTables(null, null, "test", null);
                    return rset.next();
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });
        assertTrue(tableExists);
    }

    @Override
    public void afterClass() {
        super.afterClass();
        getH2().stopH2();
    }

    @Override
    public void beforeTest() {
        super.beforeTest();

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("DELETE FROM test");
                stm.update();

                assertEquals(0, getTestTableCount());

                return null;
            }
        });
    }

    protected int getTestTableCount() {
        return getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Integer>() {

            @Override
            public Integer run(Connection connection) {

                SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
                stm.sql("SELECT COUNT(*) as count FROM test");

                Integer count = stm.selectOne(new ResultSetHandler<Integer>() {

                    @Override
                    public Integer handle(SpincastResultSet rs) throws Exception {
                        return rs.getIntegerOrNull("count");
                    }
                });
                return count;
            }
        });
    }

}

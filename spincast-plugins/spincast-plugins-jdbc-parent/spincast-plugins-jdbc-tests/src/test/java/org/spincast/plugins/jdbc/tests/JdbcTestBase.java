package org.spincast.plugins.jdbc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSource;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;
import org.spincast.plugins.jdbc.SpincastJdbcPlugin;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class JdbcTestBase extends NoAppTestingBase {

    protected Server h2Server = null;

    @Inject
    @TestDataSource
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

                           //==========================================
                           // Binds out test DataSource
                           //==========================================
                           @Override
                           protected void configure() {
                               bind(DataSource.class).annotatedWith(TestDataSource.class)
                                                     .toProvider(TestDataSourceProvider.class)
                                                     .in(Scopes.SINGLETON);
                           }
                       })
                       .init(new String[]{});
    }

    @Override
    public void beforeClass() {

        //==========================================
        // We must start H2 before the context is created
        //==========================================
        try {
            this.h2Server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers")
                                  .start();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        super.beforeClass();

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
        assertFalse(tableExists);

        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("CREATE TABLE test (" +
                        "   id SERIAL PRIMARY KEY, " +
                        "   name VARCHAR(255) NOT NULL, " +
                        "   email VARCHAR(255) UNIQUE," +
                        "   birthdate TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()," +
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

        tableExists = getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Boolean>() {

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

        if (this.h2Server != null) {
            try {
                this.h2Server.stop();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    protected static class TestDataSourceProvider implements Provider<SpincastDataSource> {

        private final SpincastDataSourceFactory spincastDataSourceFactory;

        @Inject
        public TestDataSourceProvider(SpincastDataSourceFactory spincastDataSourceFactory) {
            this.spincastDataSourceFactory = spincastDataSourceFactory;
        }

        @Override
        public SpincastDataSource get() {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:tcp://localhost:9092/mem:" + this.getClass().getSimpleName() +
                              ";MODE=PostgreSQL;DATABASE_TO_UPPER=false");
            config.setUsername("");
            config.setPassword("");
            config.setMaximumPoolSize(10);

            DataSource ds = new HikariDataSource(config);
            return this.spincastDataSourceFactory.create(ds);
        }
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

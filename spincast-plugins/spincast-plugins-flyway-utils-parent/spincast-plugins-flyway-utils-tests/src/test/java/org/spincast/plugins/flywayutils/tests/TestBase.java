package org.spincast.plugins.flywayutils.tests;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.flywayutils.SpincastFlywayFactory;
import org.spincast.plugins.flywayutils.SpincastFlywayMigration;
import org.spincast.plugins.flywayutils.SpincastFlywayUtilsPlugin;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSource;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class TestBase extends NoAppTestingBase {

    protected Server h2Server = null;

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(DataSource.class).toProvider(TestDataSourceProvider.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastFlywayUtilsPlugin());
        return extraPlugins;
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

        clearDatabase();
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        if (isClearDatabaseBeforeTest()) {
            clearDatabase();
        }
    }

    protected boolean isClearDatabaseBeforeTest() {
        return true;
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

    protected void clearDatabase() {
        getJdbcUtils().scopes().autoCommit(getTestDataSource(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("DROP ALL OBJECTS DELETE FILES ");
                stm.update();
                return null;
            }
        });
    }

    @Inject
    private SpincastFlywayFactory spincastFlywayFactory;

    protected SpincastFlywayFactory getSpincastFlywayFactory() {
        return this.spincastFlywayFactory;
    }

    @Inject
    private JdbcUtils jdbcUtils;

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    @Inject
    private DataSource testDataSource;

    protected DataSource getTestDataSource() {
        return this.testDataSource;
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
            String connectionString =
                    "jdbc:h2:tcp://localhost:9092/mem:" + this.getClass().getSimpleName() +
                                      ";MODE=PostgreSQL;DATABASE_TO_UPPER=false";
            config.setJdbcUrl(connectionString);
            config.setUsername("");
            config.setPassword("");
            config.setMaximumPoolSize(10);

            DataSource ds = new HikariDataSource(config);
            return this.spincastDataSourceFactory.create(ds);
        }
    }


    public static class Base implements SpincastFlywayMigration {

        @Override
        public void migrate(Connection connection) throws Exception {
            // ok
        }
    }


}

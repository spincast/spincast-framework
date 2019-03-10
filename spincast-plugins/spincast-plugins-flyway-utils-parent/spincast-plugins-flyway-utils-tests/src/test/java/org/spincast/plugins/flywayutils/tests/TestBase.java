package org.spincast.plugins.flywayutils.tests;

import java.util.List;

import javax.sql.DataSource;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.plugins.flywayutils.SpincastFlywayFactory;
import org.spincast.plugins.flywayutils.SpincastFlywayUtilsPlugin;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.testing.core.h2.SpincastTestingH2;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class TestBase extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                //==========================================
                // Enable Spincast H2
                //==========================================
                bind(DataSource.class).toProvider(SpincastTestingH2.class).in(Scopes.SINGLETON);
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
        super.beforeClass();
        getH2().clearDatabase();
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        if (isClearDatabaseBeforeTest()) {
            getH2().clearDatabase();
        }
    }

    protected boolean isClearDatabaseBeforeTest() {
        return true;
    }

    @Override
    public void afterClass() {
        super.afterClass();
        getH2().stopH2();
    }

    @Inject
    protected SpincastTestingH2 spincastTestingH2;

    protected SpincastTestingH2 getH2() {
        return this.spincastTestingH2;
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

    @Inject
    private SpincastFlywayFactory spincastFlywayFactory;

    protected SpincastFlywayFactory getSpincastFlywayFactory() {
        return this.spincastFlywayFactory;
    }

}

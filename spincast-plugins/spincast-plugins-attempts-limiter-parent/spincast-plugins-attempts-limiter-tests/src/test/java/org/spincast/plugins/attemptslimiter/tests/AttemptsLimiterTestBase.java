package org.spincast.plugins.attemptslimiter.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.attemptslimiter.AttemptCriteria;
import org.spincast.plugins.attemptslimiter.AttemptFactory;
import org.spincast.plugins.attemptslimiter.AttemptRule;
import org.spincast.plugins.attemptslimiter.AttemptsManager;
import org.spincast.plugins.attemptslimiter.SpincastAttemptsLimiterPlugin;
import org.spincast.plugins.attemptslimiter.SpincastAttemptsLimiterPluginRepository;
import org.spincast.plugins.attemptslimiter.tests.utils.SpincastAttemptsLimiterPluginRepositoryTesting;
import org.spincast.plugins.attemptslimiter.tests.utils.TestAttemptsRepository2;
import org.spincast.plugins.jdbc.SpincastJdbcPlugin;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegister;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegistrerDefault;
import org.spincast.testing.core.h2.SpincastTestingH2;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class AttemptsLimiterTestBase extends NoAppStartHttpServerTestingBase {

    protected static boolean isActivateScheduledTasks = false;

    @Inject
    protected SpincastTestingH2 spincastTestingH2;

    protected SpincastTestingH2 getH2() {
        return this.spincastTestingH2;
    }

    @Inject
    protected AttemptsManager attemptsManager;

    protected AttemptsManager getAttemptsManager() {
        return this.attemptsManager;
    }

    @Inject
    protected AttemptFactory attemptFactory;

    protected AttemptFactory getAttemptFactory() {
        return this.attemptFactory;
    }

    @Inject
    protected SpincastAttemptsLimiterPluginRepositoryTesting testAttemptsRepository;

    protected SpincastAttemptsLimiterPluginRepositoryTesting getTestAttemptsRepository() {
        return this.testAttemptsRepository;
    }

    @Inject
    protected TestAttemptsRepository2 testAttemptsRepository2;

    protected TestAttemptsRepository2 getTestAttemptsRepository2() {
        return this.testAttemptsRepository2;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastJdbcPlugin());
        extraPlugins.add(new SpincastAttemptsLimiterPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Enable Spincast H2
                //==========================================
                bind(DataSource.class).toProvider(SpincastTestingH2.class).in(Scopes.SINGLETON);

                bind(SpincastAttemptsLimiterPluginRepository.class).to(SpincastAttemptsLimiterPluginRepositoryTesting.class)
                                                                   .in(Scopes.SINGLETON);

                bind(SpincastScheduledTaskRegister.class).to(TestSpincastScheduledTaskRegistrer.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    public void beforeClass() {

        isActivateScheduledTasks = isActivateScheduledTasks();

        super.beforeClass();

        getH2().clearDatabase();

        assertFalse(getTestAttemptsRepository2().isAttemptTableExists());
        getTestAttemptsRepository().createAttemptTable();
        assertTrue(getTestAttemptsRepository2().isAttemptTableExists());

        //==========================================
        // Registers the attempts rules
        //==========================================
        for (AttemptRule rule : getTestSpincastAttemptRules()) {
            getAttemptsManager().registerAttempRule(rule);
        }
    }

    protected boolean isActivateScheduledTasks() {
        return false;
    }

    @Override
    public void afterClass() {
        super.afterClass();
        getTestAttemptsRepository2().dropAttemptsTable();

        getH2().stopH2();
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        getTestAttemptsRepository2().clearAttemptsTable();
    }

    public static class TestSpincastScheduledTaskRegistrer extends SpincastScheduledTaskRegistrerDefault {

        @Inject
        public TestSpincastScheduledTaskRegistrer(Set<SpincastScheduledTask> scheduledTasks,
                                                  Set<Set<SpincastScheduledTask>> scheduledTasksSets,
                                                  Scheduler scheduler, SpincastConfig spincastConfig) {
            super(scheduledTasks, scheduledTasksSets, scheduler, spincastConfig);
        }

        @Override
        protected boolean registerScheduledTasksInTestingMode() {
            //==========================================
            // We need to activate the scheduled tasks even in testing mode
            //==========================================
            return isActivateScheduledTasks;
        }
    }

    protected int getAttemptsCount(String actionName, AttemptCriteria criteria) {
        return getTestAttemptsRepository2().getAttemptsCount(actionName, criteria);
    }

    protected abstract Set<AttemptRule> getTestSpincastAttemptRules();

}

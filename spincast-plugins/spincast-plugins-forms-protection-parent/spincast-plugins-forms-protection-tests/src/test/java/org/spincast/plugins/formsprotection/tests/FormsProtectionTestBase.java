package org.spincast.plugins.formsprotection.tests;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.plugins.formsprotection.SpincastFormsProtectionPlugin;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.csrf.SpincastFormsCsrfProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegister;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegistrerDefault;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTasksPlugin;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionFilter;
import org.spincast.plugins.session.SpincastSessionManager;
import org.spincast.plugins.session.SpincastSessionPlugin;
import org.spincast.plugins.session.SpincastSessionRepository;
import org.spincast.plugins.session.config.SpincastSessionConfig;
import org.spincast.plugins.session.config.SpincastSessionConfigDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class FormsProtectionTestBase extends NoAppStartHttpServerTestingBase {

    protected static Map<String, SpincastSession> savedSessions = new HashMap<String, SpincastSession>();
    protected static int savedSessionCalledNbr = 0;

    protected static Set<String> submittedFormsIds = new HashSet<String>();

    protected static int[] deleteOldFormsProtectionIdsCalled = new int[]{0};

    @Inject
    protected SpincastSessionFilter spincastSessionFilter;

    protected SpincastSessionFilter getSpincastSessionFilter() {
        return this.spincastSessionFilter;
    }

    @Inject
    protected SpincastSessionManager spincastSessionManager;

    protected SpincastSessionManager getSpincastSessionManager() {
        return this.spincastSessionManager;
    }

    @Inject
    protected SpincastSessionConfig spincastSessionConfig;

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    @Inject
    protected SpincastFormsDoubleSubmitProtectionFilter spincastFormsDoubleSubmitProtectionFilter;

    protected SpincastFormsDoubleSubmitProtectionFilter getSpincastFormsDoubleSubmitProtectionFilter() {
        return this.spincastFormsDoubleSubmitProtectionFilter;
    }

    @Inject
    protected SpincastFormsCsrfProtectionFilter spincastFormsCsrfProtectionFilter;

    protected SpincastFormsCsrfProtectionFilter getSpincastFormsCsrfProtectionFilter() {
        return this.spincastFormsCsrfProtectionFilter;
    }

    @Inject
    protected SpincastFormsProtectionConfig spincastFormsProtectionConfig;

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    @Inject
    protected SpincastFormsDoubleSubmitProtectionRepository spincastFormsDoubleSubmitProtectionRepository;

    protected SpincastFormsDoubleSubmitProtectionRepository getSpincastFormsDoubleSubmitProtectionRepository() {
        return this.spincastFormsDoubleSubmitProtectionRepository;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastFormsProtectionPlugin());
        extraPlugins.add(new SpincastSessionPlugin());
        extraPlugins.add(new SpincastScheduledTasksPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastSessionRepository.class).to(TestSessionRepository.class).in(Scopes.SINGLETON);
                bind(SpincastFormsDoubleSubmitProtectionRepository.class).to(TestFormsDoubleSubmitProtectionRepository.class)
                                                                         .in(Scopes.SINGLETON);
                bind(SpincastScheduledTaskRegister.class).to(TestSpincastScheduledTaskRegistrer.class).in(Scopes.SINGLETON);

                bind(SpincastSessionConfig.class).toInstance(new SpincastSessionConfigDefault() {

                    @Override
                    public boolean isAutoAddSessionFilters() {
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void beforeClass() {
        deleteOldFormsProtectionIdsCalled = new int[]{0};
        super.beforeClass();
        addFilters();
    }

    public static class TestSpincastScheduledTaskRegistrer extends SpincastScheduledTaskRegistrerDefault {

        @Inject
        public TestSpincastScheduledTaskRegistrer(Set<SpincastScheduledTask> scheduledTasks,
                                                  Set<Set<SpincastScheduledTask>> scheduledTaskSets,
                                                  Scheduler scheduler, SpincastConfig spincastConfig) {
            super(scheduledTasks, scheduledTaskSets, scheduler, spincastConfig);
        }

        @Override
        protected boolean registerScheduledTasksInTestingMode() {
            //==========================================
            // We need to activate the scheduled tasks even in testing mode
            //==========================================
            return true;
        }
    }

    /**
     * Adds with "spicastCoreRouteOrPluginRoute()" so they are not removed
     */
    protected void addFilters() {

        if (isAddSessionFilter()) {
            getRouter().ALL().pos(-1000).skipResourcesRequests().spicastCoreRouteOrPluginRoute()
                       .handle(getSpincastSessionFilter()::before);
            getRouter().ALL().pos(100).skipResourcesRequests().spicastCoreRouteOrPluginRoute()
                       .handle(getSpincastSessionFilter()::after);
        }

        if (isAddDoubleSubmitProtectionFilter()) {
            getRouter().ALL().pos(-500).found().skipResourcesRequests().spicastCoreRouteOrPluginRoute()
                       .handle(getSpincastFormsDoubleSubmitProtectionFilter()::handle);
        }

        if (isAddCsrfProtectionFilter()) {
            getRouter().ALL().pos(-450).found().spicastCoreRouteOrPluginRoute().skipResourcesRequests()
                       .handle(getSpincastFormsCsrfProtectionFilter()::handle);
        }
    }

    protected boolean isAddDoubleSubmitProtectionFilter() {
        return false;
    }

    protected boolean isAddCsrfProtectionFilter() {
        return false;
    }

    protected boolean isAddSessionFilter() {
        return false;
    }

    /**
     * Session repository implementation
     */
    public static class TestSessionRepository implements SpincastSessionRepository {

        @Override
        public SpincastSession getSession(String sessionId) {
            SpincastSession session = savedSessions.get(sessionId);
            return session;
        }

        @Override
        public void saveSession(SpincastSession session) {
            savedSessionCalledNbr++;
            savedSessions.put(session.getId(), session);
        }

        @Override
        public void deleteSession(String sessionId) {
            savedSessions.remove(sessionId);
        }

        @Override
        public void deleteOldInactiveSession(int sessionMaxInactiveMinutes) {
            System.out.println("deleteOldInactiveSession ran");
        }
    }

    /**
     * Double Submit repository implementation
     */
    public static class TestFormsDoubleSubmitProtectionRepository implements SpincastFormsDoubleSubmitProtectionRepository {

        @Override
        public boolean isFormAlreadySubmitted(String protectionId) {
            return submittedFormsIds.contains(protectionId);
        }

        @Override
        public void saveSubmittedFormProtectionId(Instant date, String protectionId) {
            submittedFormsIds.add(protectionId);
        }

        @Override
        public void deleteOldFormsProtectionIds(int maxAgeMinutes) {
            deleteOldFormsProtectionIdsCalled[0]++;
            submittedFormsIds.clear();
        }
    }

}

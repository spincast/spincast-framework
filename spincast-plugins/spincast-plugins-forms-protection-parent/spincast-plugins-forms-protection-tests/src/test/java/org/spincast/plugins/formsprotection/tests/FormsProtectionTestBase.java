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
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobRegister;
import org.spincast.plugins.crons.SpincastCronJobRegistrerDefault;
import org.spincast.plugins.crons.SpincastCronsPlugin;
import org.spincast.plugins.formsprotection.SpincastFormsProtectionPlugin;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.csrf.SpincastFormsCsrfProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionFilter;
import org.spincast.plugins.session.SpincastSessionManager;
import org.spincast.plugins.session.SpincastSessionPlugin;
import org.spincast.plugins.session.SpincastSessionRepository;
import org.spincast.plugins.session.config.SpincastSessionConfig;

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
        extraPlugins.add(new SpincastCronsPlugin());
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
                bind(SpincastCronJobRegister.class).to(TestSpincastCronJobRegistrer.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    public void beforeClass() {
        deleteOldFormsProtectionIdsCalled = new int[]{0};
        super.beforeClass();
        addFilters();
    }

    public static class TestSpincastCronJobRegistrer extends SpincastCronJobRegistrerDefault {

        @Inject
        public TestSpincastCronJobRegistrer(Set<SpincastCronJob> cronJobs, Set<Set<SpincastCronJob>> cronJobsSets,
                                            Scheduler scheduler, SpincastConfig spincastConfig) {
            super(cronJobs, cronJobsSets, scheduler, spincastConfig);
        }

        @Override
        protected boolean registerCronJobInTestingMode() {
            //==========================================
            // We need to activate the crons even in testing mode
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

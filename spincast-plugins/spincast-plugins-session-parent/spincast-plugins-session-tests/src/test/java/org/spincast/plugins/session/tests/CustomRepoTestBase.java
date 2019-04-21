package org.spincast.plugins.session.tests;

import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionRepository;

public abstract class CustomRepoTestBase extends SessionTestBase {

    /**
     * Custom Session repository implementation
     */
    public static class CustomSessionRepository implements SpincastSessionRepository {

        @Override
        public SpincastSession getSession(String sessionId) {
            SpincastSession session = savedSession.get(sessionId);
            return session;
        }

        @Override
        public void saveSession(SpincastSession session) {
            savedSession.put(session.getId(), session);
        }

        @Override
        public void deleteSession(String sessionId) {
            savedSession.remove(sessionId);
        }

        @Override
        public void deleteOldInactiveSession(int sessionMaxInactiveMinutes) {
            deleteOldSessionsCalled[0]++;
        }
    }

    @Override
    protected Class<? extends SpincastSessionRepository> getSpincastSessionRepositoryImplClass() {
        return CustomSessionRepository.class;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        //==========================================
        // Add the session filters at specific positions.
        // We add them with ".spicastCoreRouteOrPluginRoute()"
        // so they are not cleared before each test...
        //==========================================
        getRouter().ALL().pos(-1000).spicastCoreRouteOrPluginRoute().skipResourcesRequests()
                   .handle(getSpincastSessionFilter()::before);
        getRouter().ALL().pos(100).spicastCoreRouteOrPluginRoute().skipResourcesRequests()
                   .handle(getSpincastSessionFilter()::after);
    }

}

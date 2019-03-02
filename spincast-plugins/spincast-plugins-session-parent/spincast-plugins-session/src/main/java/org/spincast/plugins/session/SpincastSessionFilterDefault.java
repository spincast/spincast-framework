package org.spincast.plugins.session;

import java.time.Duration;
import java.time.Instant;

import org.spincast.core.exchange.RequestContext;
import org.spincast.plugins.session.config.SpincastSessionConfig;

import com.google.inject.Inject;

public class SpincastSessionFilterDefault implements SpincastSessionFilter {

    private final SpincastSessionManager spincastSessionManager;
    private final SpincastSessionConfig spincastSessionConfig;

    @Inject
    public SpincastSessionFilterDefault(SpincastSessionManager spincastSessionManager,
                                        SpincastSessionConfig spincastSessionConfig) {
        this.spincastSessionManager = spincastSessionManager;
        this.spincastSessionConfig = spincastSessionConfig;
    }

    protected SpincastSessionManager getSpincastSessionManager() {
        return this.spincastSessionManager;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    @Override
    public void before(RequestContext<?> context) {

        //==========================================
        // This filter should have been configured
        // to skip resources requests, but in case :
        //==========================================
        if (context.routing().getRoutingResult().getMainRouteHandlerMatch().getSourceRoute().isStaticResourceRoute()) {
            return;
        }

        SpincastSession session = null;

        String sessionId = getSessionIdFromUserRequest(context);
        if (sessionId != null) {
            session = getSpincastSessionManager().getSavedSession(sessionId);
            if (session != null) {

                //==========================================
                // This is a workaround to make sure a 
                // custom repository implementation that would keep
                // the instances of the sessions as is, without
                // recreated them, wouln't have a session 
                // stay "new" forever.
                //==========================================
                if (session.isNew() || session.isDirty()) {
                    session = getSpincastSessionManager().createSession(session.getId(),
                                                                        session.getCreationDate(),
                                                                        session.getModificationDate(),
                                                                        session.getAttributes());
                    session.setDirty();
                }
            } else {
                //==========================================
                // By default we delete the session on the user
                // since it is not valid anymore.
                // This will be overriden if a new session is
                // created and must be saved!
                //==========================================
                getSpincastSessionManager().deleteSessionIdOnUser();
            }
        }

        //==========================================
        // No session id from the user or no session
        // found with the existing id... We create
        // a new session.
        //
        // A new session will automatically be saved in 
        // the database in the "after" part of the filter.
        //==========================================
        if (session == null) {
            session = getSpincastSessionManager().createNewSession();
        }

        //==========================================
        // Saves the session in the request context.
        //==========================================
        context.variables().set(SpincastSessionManagerDefault.REQUEST_CONTEXT_VARIABLE_SESSION, session);
    }

    @Override
    public void after(RequestContext<?> context) {

        //==========================================
        // This filter should have been configured
        // to skip resources requests, but in case :
        //==========================================
        if (context.routing().getRoutingResult().getMainRouteHandlerMatch().getSourceRoute().isStaticResourceRoute()) {
            return;
        }

        SpincastSession session =
                context.variables().get(SpincastSessionManagerDefault.REQUEST_CONTEXT_VARIABLE_SESSION, SpincastSession.class);

        if (session == null) {
            return;
        }

        //==========================================
        // Invalidated session?
        // We delete it on the client and in the database.
        //==========================================
        if (session.isInvalidated()) {
            getSpincastSessionManager().deleteSessionIdOnUser();
            getSpincastSessionManager().deleteSession(session.getId());
            return;
        }

        //==========================================
        // Save the session?
        //==========================================
        if (session.isDirty() || isUpdateModificationDateEvenIfNotDirty(session)) {
            getSpincastSessionManager().updateModificationDateAndSaveSession(session);

            //==========================================
            // If the session is new, we save its id on 
            // the user.
            //
            // Be careful with an existing session: we don't want
            // to replace a permanent cookie with a browser-session
            // one!
            //==========================================
            if (session.isNew()) {
                getSpincastSessionManager().saveSessionIdOnUser(session.getId(),
                                                                getSpincastSessionConfig().isSessionPermanentByDefault());
            }
        }
    }

    protected boolean isUpdateModificationDateEvenIfNotDirty(SpincastSession session) {
        if (session.isNew()) {
            return false;
        }

        Duration timeFromLastTimeSaved = Duration.between(session.getModificationDate(), Instant.now());
        return timeFromLastTimeSaved.getSeconds() > getSpincastSessionConfig().getUpdateNotDirtySessionPeriodInSeconds();
    }

    protected String getSessionIdFromUserRequest(RequestContext<?> context) {

        //==========================================
        // By default the session id is stored in a cookie.
        //==========================================
        return context.request().getCookieValue(getSpincastSessionConfig().getSessionIdCookieName());
    }


}

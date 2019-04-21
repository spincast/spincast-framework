package org.spincast.plugins.session;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.session.config.SpincastSessionConfig;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

public class SpincastSessionManagerDefault implements SpincastSessionManager {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastSessionManagerDefault.class);

    /**
     * The name of the request context variable used to
     * save the user session.
     */
    public static final String REQUEST_CONTEXT_VARIABLE_SESSION =
            SpincastSessionManagerDefault.class.getName() + "_session";

    private final SpincastSessionFactory spincastSessionFactory;
    private final Provider<RequestContext<?>> requestContextProvider;
    private final JsonManager jsonManager;
    private final SpincastSessionConfig spincastSessionConfig;
    private final SpincastSessionRepository spincastSessionRepository;

    @Inject
    public SpincastSessionManagerDefault(SpincastSessionFactory spincastSessionFactory,
                                         Provider<RequestContext<?>> requestContextProvider,
                                         JsonManager jsonManager,
                                         SpincastSessionConfig spincastSessionConfig,
                                         SpincastSessionRepository spincastSessionRepository) {
        this.spincastSessionFactory = spincastSessionFactory;
        this.requestContextProvider = requestContextProvider;
        this.jsonManager = jsonManager;
        this.spincastSessionConfig = spincastSessionConfig;
        this.spincastSessionRepository = spincastSessionRepository;
    }

    protected SpincastSessionFactory getSpincastSessionFactory() {
        return this.spincastSessionFactory;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected SpincastSessionRepository getSpincastSessionRepository() {
        return this.spincastSessionRepository;
    }

    @Override
    public SpincastSession createNewSession() {
        return getSpincastSessionFactory().createNewSession();
    }

    @Override
    public SpincastSession createSession(String sessionId,
                                         Instant creationDate,
                                         Instant modificationDate,
                                         JsonObject attributes) {
        return getSpincastSessionFactory().createSession(sessionId,
                                                         creationDate,
                                                         modificationDate,
                                                         attributes);
    }

    @Override
    public SpincastSession getCurrentSession() {

        SpincastSession session = null;

        //==========================================
        // This can be called outside of a Request scope, for
        // example by a scheduled task.
        //==========================================
        try {
            RequestContext<?> context = getRequestContextProvider().get();
            session = (SpincastSession)context.variables().get(REQUEST_CONTEXT_VARIABLE_SESSION);
            if (session == null) {
                logger.error("No session found in request contaxt variables. Make sure the filters " +
                             "provided by the Spincast Session plugin have been added properly to your router!");
            }

        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, not in the scope a a request
        }

        return session;
    }

    @Override
    public String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void deleteSessionIdOnUser() {
        try {
            RequestContext<?> context = getRequestContextProvider().get();
            context.response().deleteCookie(getSpincastSessionConfig().getSessionIdCookieName());
        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, not in the scope a a request
        }
    }

    @Override
    public void saveSessionIdOnUser(String sessionId, boolean permanent) {
        try {
            RequestContext<?> context = getRequestContextProvider().get();
            saveSessionIdOnUser(context, sessionId, permanent);
        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, not in the scope a a request
        }
    }

    protected void saveSessionIdOnUser(RequestContext<?> context, String sessionId, boolean permanent) {
        if (permanent) {
            //==========================================
            // Stores it for a long time... The session will be
            // garbaged collected *server side*.
            //==========================================
            context.response().setCookie10yearsSafe(getSpincastSessionConfig().getSessionIdCookieName(), sessionId);
        } else {
            context.response().setCookieSessionSafe(getSpincastSessionConfig().getSessionIdCookieName(), sessionId);
        }
    }

    @Override
    public final void updateModificationDateAndSaveSession(SpincastSession session) {

        SpincastSession updatedSsession = createSession(session.getId(),
                                                        session.getCreationDate(),
                                                        Instant.now(),
                                                        session.getAttributes());
        saveSession(updatedSsession);
    }

    @Override
    public void deleteCurrentSession() {
        SpincastSession currentSession = getCurrentSession();
        if (currentSession != null) {
            currentSession.invalidate();
            deleteSessionIdOnUser();
            deleteSession(currentSession.getId());
        }
    }

    @Override
    public SpincastSession getSavedSession(String sessionId) {
        return getSpincastSessionRepository().getSession(sessionId);
    }

    @Override
    public void saveSession(SpincastSession session) {
        getSpincastSessionRepository().saveSession(session);

    }

    @Override
    public void deleteSession(String sessionId) {
        getSpincastSessionRepository().deleteSession(sessionId);
    }

    @Override
    public void deleteOldInactiveSession(int sessionMaxInactiveMinutes) {
        getSpincastSessionRepository().deleteOldInactiveSession(sessionMaxInactiveMinutes);
    }

}

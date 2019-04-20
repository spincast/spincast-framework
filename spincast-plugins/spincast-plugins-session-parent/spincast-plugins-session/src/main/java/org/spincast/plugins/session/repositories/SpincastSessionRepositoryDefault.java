package org.spincast.plugins.session.repositories;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionFactory;
import org.spincast.plugins.session.SpincastSessionRepository;
import org.spincast.plugins.session.config.SpincastSessionConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

/**
 * The default implementation of {@link SpincastSessionRepository}
 * is to save the sessions <em>in a cookie</em>, on the user.
 * <p>
 * We highly suggest that you bind a custom implementation
 * that saved sessions in a real database instead!
 * <p>
 * A cookie can contain +/- 4096 bytes maximum!
 */
public class SpincastSessionRepositoryDefault implements SpincastSessionRepository {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastSessionRepositoryDefault.class);

    private final Provider<RequestContext<?>> requestContextProvider;
    private final SpincastSessionConfig spincastSessionConfig;
    private final SpincastSessionFactory spincastSessionFactory;
    private final JsonManager jsonManager;

    @Inject
    public SpincastSessionRepositoryDefault(Provider<RequestContext<?>> requestContextProvider,
                                            SpincastSessionConfig spincastSessionConfig,
                                            SpincastSessionFactory spincastSessionFactory,
                                            JsonManager jsonManager) {
        this.requestContextProvider = requestContextProvider;
        this.spincastSessionConfig = spincastSessionConfig;
        this.spincastSessionFactory = spincastSessionFactory;
        this.jsonManager = jsonManager;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected SpincastSessionFactory getSpincastSessionFactory() {
        return this.spincastSessionFactory;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public SpincastSession getSession(String sessionId) {

        String sessionValue = null;

        //==========================================
        // No need to check the session id as there
        // can be only only one session cookie.
        //==========================================
        try {
            RequestContext<?> context = getRequestContextProvider().get();

            sessionValue =
                    context.request().getCookieValue(getSpincastSessionConfig().getDefaultCookieRepositoryCookieName());

        } catch (OutOfScopeException | ProvisionException ex) {
            logger.info("We can't get the session as there is no RequestContext...");
        }

        SpincastSession spincastSession = deserializeSession(sessionValue);

        //==========================================
        // Session too old?
        //==========================================
        if (spincastSession.getModificationDate() != null &&
            spincastSession.getModificationDate()
                           .isBefore(Instant.now().minus(getSpincastSessionConfig().getSessionMaxInactiveMinutes(),
                                                         ChronoUnit.MINUTES))) {
            deleteSession(null);
            spincastSession = createNewSpincastSession();
        }

        return spincastSession;
    }

    @Override
    public void saveSession(SpincastSession session) {

        try {
            RequestContext<?> context = getRequestContextProvider().get();

            String serializedSession = serializeSession(session);
            if (!StringUtils.isBlank(serializedSession)) {
                context.response().setCookie10years(getSpincastSessionConfig().getDefaultCookieRepositoryCookieName(),
                                                    serializedSession);
            } else {
                context.response().deleteCookie(getSpincastSessionConfig().getDefaultCookieRepositoryCookieName());
            }
        } catch (OutOfScopeException | ProvisionException ex) {
            logger.info("We can't save the session as there is no RequestContext...");
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        //==========================================
        // No need to check the session id as there
        // can be only only one session cookie.
        //==========================================
        try {
            RequestContext<?> context = getRequestContextProvider().get();
            context.response().deleteCookie(getSpincastSessionConfig().getDefaultCookieRepositoryCookieName());
        } catch (OutOfScopeException | ProvisionException ex) {
            logger.info("We can't delete the session as there is no RequestContext...");
        }
    }

    @Override
    public void deleteOldInactiveSession(int sessionMaxInactiveMinutes) {
        //==========================================
        // Not required in this repo
        //==========================================
    }

    protected String serializeSession(SpincastSession session) {

        if (session == null) {
            return "";
        }

        JsonObject attributes = session.getAttributes();
        if (attributes == null) {
            return "";
        }

        try {
            //==========================================
            // Add extra atributes to be saved
            //==========================================
            attributes.set(getSessionAttributeNameId(), session.getId().toString());
            attributes.set(getSessionAttributeNameCreationDate(), session.getCreationDate().toString());
            attributes.set(getSessionAttributeNameModificationDate(), session.getModificationDate().toString());

            String val = attributes.toJsonString(false);
            return val;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected SpincastSession deserializeSession(String sessionValue) {

        JsonObject attributes = null;
        if (!StringUtils.isBlank(sessionValue)) {
            try {
                attributes = getJsonManager().fromString(sessionValue);
            } catch (Exception ex) {
                logger.warn("Unable to deserialize session: " + sessionValue);
            }
        }

        if (attributes == null) {
            return createNewSpincastSession();
        }

        String sessionId = attributes.getString(getSessionAttributeNameId());
        if (StringUtils.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }

        Instant now = Instant.now();
        Instant creationDate = attributes.getInstant(getSessionAttributeNameCreationDate());
        if (creationDate == null) {
            creationDate = now;
        }

        Instant modificationDate = attributes.getInstant(getSessionAttributeNameModificationDate());
        if (modificationDate == null) {
            modificationDate = now;
        }

        attributes.remove(getSessionAttributeNameId());
        attributes.remove(getSessionAttributeNameCreationDate());
        attributes.remove(getSessionAttributeNameModificationDate());

        SpincastSession spincastSession = getSpincastSessionFactory().createSession(sessionId,
                                                                                    creationDate,
                                                                                    modificationDate,
                                                                                    attributes);
        return spincastSession;
    }

    protected SpincastSession createNewSpincastSession() {
        Instant now = Instant.now();
        SpincastSession newSession = getSpincastSessionFactory().createSession(UUID.randomUUID().toString(), // wathever, not used
                                                                               now,
                                                                               now,
                                                                               getJsonManager().create());
        return newSession;
    }

    protected String getSessionAttributeNameId() {
        return "_sid";
    }

    protected String getSessionAttributeNameCreationDate() {
        return "_cdate";
    }

    protected String getSessionAttributeNameModificationDate() {
        return "_mdate";
    }

}

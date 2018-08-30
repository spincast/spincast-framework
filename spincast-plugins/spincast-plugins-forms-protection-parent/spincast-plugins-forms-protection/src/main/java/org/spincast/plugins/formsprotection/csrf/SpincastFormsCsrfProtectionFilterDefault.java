package org.spincast.plugins.formsprotection.csrf;

import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.crypto.SpincastCryptoUtils;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.dictionary.SpincastFormsProtectionPluginDictionaryEntries;
import org.spincast.plugins.formsprotection.exceptions.FormInvalidCsrfTokenException;
import org.spincast.plugins.formsprotection.exceptions.FormInvalidOriginException;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionFilter;
import org.spincast.plugins.session.SpincastSessionManager;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

/**
 * CSRF protection filter.
 * 
 * Based on: https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet
 */
public class SpincastFormsCsrfProtectionFilterDefault implements SpincastFormsCsrfProtectionFilter {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFormsCsrfProtectionFilterDefault.class);

    private final SpincastFormsProtectionConfig spincastFormsProtectionConfig;
    private final SpincastCryptoUtils spincastCryptoUtils;
    private final SpincastSessionManager spincastSessionManager;
    private final SpincastConfig spincastConfig;
    private final Dictionary dictionary;

    @Inject
    public SpincastFormsCsrfProtectionFilterDefault(SpincastFormsProtectionConfig spincastFormsProtectionConfig,
                                                    SpincastCryptoUtils spincastCryptoUtils,
                                                    SpincastSessionManager spincastSessionManager,
                                                    SpincastConfig spincastConfig,
                                                    Dictionary dictionary) {
        this.spincastFormsProtectionConfig = spincastFormsProtectionConfig;
        this.spincastCryptoUtils = spincastCryptoUtils;
        this.spincastSessionManager = spincastSessionManager;
        this.spincastConfig = spincastConfig;
        this.dictionary = dictionary;
    }

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    protected SpincastCryptoUtils getSpincastCryptoUtils() {
        return this.spincastCryptoUtils;
    }

    protected SpincastSessionManager getSpincastSessionManager() {
        return this.spincastSessionManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    @Override
    public void handle(RequestContext<?> context) throws FormInvalidOriginException,
                                                  FormInvalidCsrfTokenException {
        //==========================================
        // This filter should have been configured
        // to skip resources requests, but in case :
        //==========================================
        if (context.routing().getRoutingResult().getMainRouteHandlerMatch().getSourceRoute().isResourceRoute()) {
            return;
        }

        try {

            SpincastSession currentSession = getSpincastSessionManager().getCurrentSession();
            if (currentSession == null) {
                throw new RuntimeException("No Session available. Makes sur the " +
                                           SpincastSessionFilter.class.getSimpleName() + " filter is run *before* this " +
                                           SpincastFormsCsrfProtectionFilter.class.getSimpleName() + " filter!");
            }

            //==========================================
            // Gets the CSRF token for the current user...
            // We do NOT create a new token here, if there's
            // none in the session.
            // A new token must be created only when 
            // "getCurrentCsrfToken()" if called from the
            // application... We don't want to add an
            // attribute to the session if it's not required:
            // this makes the session dirty and therefore 
            // saved on each request.
            //==========================================
            SpincastCsrfToken userCsrfToken = getCurrentCsrfToken(false);

            //==========================================
            // Current request processing....
            //==========================================

            //==========================================
            // Safe HTTP methods
            //==========================================
            HttpMethod method = context.request().getHttpMethod();
            if (method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.OPTIONS ||
                method == HttpMethod.CONNECT) {
                return;
            }

            //==========================================
            // Here, the submitted CSRF token must exist
            // and be valid!
            // The session must also contain a matching
            // token...
            //==========================================
            if (userCsrfToken == null) {
                this.logger.debug("No CSRF token found in the user's session...");
                csrfDoesntMatchAction(context,
                                      getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_CSRF_TOKEN_NOT_FOUND_IN_SESSION));
            }

            String csrfTokenName = getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName();

            //==========================================
            // From the FORM data...
            //==========================================
            String csrfTokenSubmitted = context.request().getFormBodyAsJsonObject().getString(csrfTokenName);
            if (csrfTokenSubmitted == null) {

                //==========================================
                // ... or from the querystring.
                //==========================================
                csrfTokenSubmitted = context.request().getQueryStringParamFirst(csrfTokenName);
                if (csrfTokenSubmitted == null) {
                    this.logger.warn(context.request().getHttpMethod() + " without a CSRF \"" + csrfTokenName + "\" token : " +
                                     context.request().getFullUrl());
                    csrfDoesntMatchAction(context,
                                          getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_NO_CSRF_TOKEN_PROVIDED));
                    return;
                }
            }

            if (!csrfTokenSubmitted.equals(userCsrfToken.getId())) {
                this.logger.warn("Request with an invalid CSRF token : " + context.request().getFullUrl() + " => " +
                                 userCsrfToken);
                csrfDoesntMatchAction(context,
                                      getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_INVALID_CSRF_TOKEN));
                return;
            }

            //==========================================
            // Origin or Referrer header must match the host
            //==========================================
            String origin = context.request().getHeaderFirst(HttpHeaders.ORIGIN);
            String referer = context.request().getHeaderFirst(HttpHeaders.REFERER);
            if (origin == null && referer == null) {
                this.logger.warn("Request without origin or referer header : " + context.request().getFullUrl());
                csrfDoesntMatchAction(context,
                                      getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_INVALID_ORGIN));
                return;
            }

            if (origin != null) {
                URI uri = new URI(origin);
                if (!getSpincastConfig().getPublicServerHost().equalsIgnoreCase(uri.getHost())) {
                    this.logger.warn("Request with origin header '" + uri.getHost() + "' that doesn't contain the right host : " +
                                     getSpincastConfig().getPublicServerHost());
                    csrfDoesntMatchAction(context,
                                          getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_INVALID_ORGIN));
                    return;
                }
            } else if (referer != null) {
                URI uri = new URI(referer);
                if (!getSpincastConfig().getPublicServerHost().equalsIgnoreCase(uri.getHost())) {
                    this.logger.warn("Request with referer header '" + uri.getHost() +
                                     "' that doesn't contain the right host : " +
                                     getSpincastConfig().getPublicServerHost());
                    csrfDoesntMatchAction(context,
                                          getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_INVALID_ORGIN));
                    return;
                }
            }

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public SpincastCsrfToken getCurrentCsrfToken() {
        return getCurrentCsrfToken(true);

    }

    public SpincastCsrfToken getCurrentCsrfToken(boolean createItIfNoneExists) {
        SpincastCsrfToken spincastCsrfToken = null;
        SpincastSession currentSession = getSpincastSessionManager().getCurrentSession();

        if (currentSession != null) {
            JsonObject csrfTokenJsonObj = currentSession.getAttributes()
                                                        .getJsonObject(SpincastFormsProtectionConfig.SESSION_VARIABLE_NAME_CSRF_TOKEN);
            if (csrfTokenJsonObj != null) {
                spincastCsrfToken =
                        new SpincastCsrfToken(csrfTokenJsonObj.getString("id"), csrfTokenJsonObj.getInstant("creationDate"));
            }
        }

        //==========================================
        // Create a new token if required.
        // The token will be saved in the user's session...
        //==========================================
        if (spincastCsrfToken == null && createItIfNoneExists) {
            spincastCsrfToken = createCsrfToken();
            currentSession.getAttributes().set(SpincastFormsProtectionConfig.SESSION_VARIABLE_NAME_CSRF_TOKEN,
                                               spincastCsrfToken);
        }

        return spincastCsrfToken;
    }

    protected SpincastCsrfToken createCsrfToken() {

        try {
            String key = UUID.randomUUID().toString();
            key = Base64.getUrlEncoder().encodeToString(key.getBytes("UTF-8"));
            SpincastCsrfToken token = new SpincastCsrfToken(key, Instant.now());
            return token;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * What to do when the CSRF is not there or not valid?
     * 
     * By default, throw a {@link PublicException} with
     * an HTTP status code of {@link HttpStatus#SC_BAD_REQUEST} and
     * a public message.
     */
    protected void csrfDoesntMatchAction(RequestContext<?> context, String message) throws Exception {
        throw new PublicExceptionDefault(message, HttpStatus.SC_BAD_REQUEST);
    }

}

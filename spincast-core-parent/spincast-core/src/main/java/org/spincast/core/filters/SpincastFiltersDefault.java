package org.spincast.core.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exceptions.SkipRemainingHandlersException;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.flash.FlashMessage;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.response.Alert;
import org.spincast.core.response.AlertLevel;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Spincast filters implementations.
 */
public class SpincastFiltersDefault<R extends RequestContext<?>> implements SpincastFilters<R> {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastFiltersDefault.class);

    private final CorsFilter corsFilter;
    private final SpincastConfig spincastConfig;
    private final Server server;
    private final SpincastUtils spincastUtils;

    /**
     * Constructor
     */
    @Inject
    public SpincastFiltersDefault(CorsFilter corsFilter,
                                  SpincastConfig spincastConfig,
                                  Server server,
                                  SpincastUtils spincastUtils) {
        this.corsFilter = corsFilter;
        this.spincastConfig = spincastConfig;
        this.server = server;
        this.spincastUtils = spincastUtils;
    }

    protected CorsFilter getCorsFilter() {
        return this.corsFilter;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Server getServer() {
        return this.server;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    public boolean saveGeneratedResource(R context, String pathForGeneratedResource) {

        try {

            File resourceFile = new File(pathForGeneratedResource);

            //==========================================
            // Check if the main handler has saved the
            // generated resource by itself...
            //==========================================
            if (resourceFile.exists()) {
                logger.info("The resource already exists. We don't save it here.");
                return true;
            }

            if (HttpStatus.SC_OK != context.response().getStatusCode()) {
                logger.info("Nothing will be saved since the response code is not " + HttpStatus.SC_OK);
                return false;
            }

            if (context.response().isHeadersSent()) {
                logger.warn("Headers sent, we can't save a copy of the generated resource! You will have to make sure that " +
                            "you save the generated resource by yourself, otherwise, a new version will be generated for each " +
                            "request!");
                return false;
            }

            byte[] unsentBytes = context.response().getUnsentBytes();

            FileUtils.writeByteArrayToFile(resourceFile, unsentBytes);

            return true;

        } catch (Exception ex) {
            logger.error("Unable to save the generated resource '" + pathForGeneratedResource + "' :\n" +
                         SpincastStatics.getStackTrace(ex));

            // We still let the reponse being sent...
            return false;
        }
    }

    @Override
    public void addSecurityHeaders(R context) {

        //context.response().addHeaderValue("Content-Security-Policy", "default-src: 'self'");
        context.response().addHeaderValue("X-Xss-Protection", "1; mode=block");
        context.response().addHeaderValue("X-Frame-Options", "SAMEORIGIN");
        context.response().addHeaderValue("x-content-type-options", "nosniff");
    }

    @Override
    public void cors(R context) {

        cors(context,
             getCorsDefaultAllowedOrigins(),
             getCorsDefaultExtraHeadersAllowedToBeRead(),
             getCorsDefaultExtraHeadersAllowedToBeSent(),
             getCorsDefaultIsCookiesAllowed(),
             getCorsDefaultAllowedMethods(),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins) {

        cors(context,
             allowedOrigins,
             getCorsDefaultExtraHeadersAllowedToBeRead(),
             getCorsDefaultExtraHeadersAllowedToBeSent(),
             getCorsDefaultIsCookiesAllowed(),
             getCorsDefaultAllowedMethods(),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead) {
        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             getCorsDefaultExtraHeadersAllowedToBeSent(),
             getCorsDefaultIsCookiesAllowed(),
             getCorsDefaultAllowedMethods(),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent) {

        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             getCorsDefaultIsCookiesAllowed(),
             getCorsDefaultAllowedMethods(),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies) {

        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies,
             getCorsDefaultAllowedMethods(),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods) {

        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies,
             allowedMethods,
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods,
                     int maxAgeInSeconds) {

        CorsFilterClient corsFilterClient = createCorsFilterClient(context,
                                                                   allowedOrigins,
                                                                   extraHeadersAllowedToBeRead,
                                                                   extraHeadersAllowedToBeSent,
                                                                   allowCookies,
                                                                   allowedMethods,
                                                                   maxAgeInSeconds);

        CorsFilterResponse corsResult = getCorsFilter().apply(corsFilterClient);

        if (corsResult == CorsFilterResponse.NOT_CORS) {

            //==========================================
            // Not a cors request, or same origin...
            // No need to do anything here.
            //==========================================
            return;

        } else if (corsResult == CorsFilterResponse.HEADERS_ALREADY_SENT) {

            //==========================================
            // Headers already sent? There is nothing we can do...
            //==========================================
            return;

        } else if (corsResult == CorsFilterResponse.INVALID_CORS_REQUEST) {

            //==========================================
            // Invalid request, we return OK without the
            // cors headers.
            //==========================================
            throw new SkipRemainingHandlersException();

        } else if (corsResult == CorsFilterResponse.SIMPLE) {

            //==========================================
            // Simple cors request (not a Preflight).
            // We added the required headers and our job is now done.
            // The routing process can continue...
            //==========================================
            return;

        } else if (corsResult == CorsFilterResponse.PREFLIGHT) {

            //==========================================
            // We always skip all remaining handlers
            // on a Preflight request!
            // For example if the request is for a "dynamic resource",
            // we don't want to run the "saveGeneratedResource" after
            // filter, which would save an empty resource.
            //==========================================
            throw new SkipRemainingHandlersException();

        } else {
            throw new RuntimeException("Unmanaged cors result: " + corsResult);
        }
    }

    /**
     * Creates a client for the cors filter.
     */
    protected CorsFilterClient createCorsFilterClient(final R context,
                                                      final Set<String> allowedOrigins,
                                                      final Set<String> extraHeadersAllowedToBeRead,
                                                      final Set<String> extraHeadersAllowedToBeSent,
                                                      final boolean allowCookies,
                                                      final Set<HttpMethod> allowedMethods,
                                                      final int maxAgeInSeconds) {
        return new CorsFilterClient() {

            @Override
            public void setStatusCode(int code) {
                context.response().setStatusCode(code);
            }

            @Override
            public void resetEverything() {
                context.response().resetEverything();
            }

            @Override
            public boolean requestContainsCookies() {
                Map<String, String> cookies = context.request().getCookiesValues();
                return (cookies != null) && (cookies.size() > 0);
            }

            @Override
            public boolean isHeadersSent() {
                return context.response().isHeadersSent();
            }

            @Override
            public boolean isAllowCookies() {
                return allowCookies;
            }

            @Override
            public int getMaxAgeInSeconds() {
                return maxAgeInSeconds;
            }

            @Override
            public HttpMethod getHttpMethod() {
                return context.request().getHttpMethod();
            }

            @Override
            public String getHeaderFirst(String name) {
                return context.request().getHeaderFirst(name);
            }

            @Override
            public String getFullUrl() {
                return context.request().getFullUrl();
            }

            @Override
            public Set<String> getExtraHeadersAllowedToBeSent() {
                return extraHeadersAllowedToBeSent;
            }

            @Override
            public Set<String> getExtraHeadersAllowedToBeRead() {
                return extraHeadersAllowedToBeRead;
            }

            @Override
            public Set<String> getAllowedOrigins() {
                return allowedOrigins;
            }

            @Override
            public Set<HttpMethod> getAllowedMethods() {
                return allowedMethods;
            }

            @Override
            public void addHeaderValue(String name, String value) {
                context.response().addHeaderValue(name, value);
            }
        };
    }

    /**
     * If &lt;= 0, the "Access-Control-Max-Age" header
     * won't be sent.
     */
    protected int getCorsDefaultMaxAgeInSeconds() {
        return 86400; // 24h
    }

    /**
     * The origins allowed, by default.
     */

    protected Set<String> getCorsDefaultAllowedOrigins() {

        //==========================================
        // All origins allowed.
        //==========================================
        return Sets.newHashSet("*");
    }

    /**
     * The extra headers allowed to be read, by default,
     */
    protected Set<String> getCorsDefaultExtraHeadersAllowedToBeRead() {

        //==========================================
        // No extra header allowed.
        //==========================================
        return null;
    }

    /**
     * The extra headers allowed to be sent, by default,
     */
    protected Set<String> getCorsDefaultExtraHeadersAllowedToBeSent() {

        //==========================================
        // All headers allowed.
        //==========================================
        return Sets.newHashSet("*");
    }

    /**
     * Are cookies allowed by default?
     */
    protected boolean getCorsDefaultIsCookiesAllowed() {

        //==========================================
        // Cookies allowed.
        //==========================================
        return true;
    }

    /**
     * The HTTP methods allowed by default.
     */
    protected Set<HttpMethod> getCorsDefaultAllowedMethods() {

        //==========================================
        // All methods
        //==========================================
        return Sets.newHashSet(HttpMethod.values());
    }

    @Override
    public void cache(R context) {
        cache(context, getCacheSecondsByDefault());
    }

    @Override
    public void cache(R context, int seconds) {
        cache(context, seconds, isCachePrivateByDefault());
    }

    @Override
    public void cache(R context, int seconds, boolean isPrivate) {
        cache(context, seconds, isPrivate, getCacheCdnSecondsByDefault());
    }

    @Override
    public void cache(R context, int seconds, boolean isPrivate, Integer cdnSeconds) {
        context.cacheHeaders().cache(seconds, isPrivate, cdnSeconds);
    }

    protected int getCacheSecondsByDefault() {
        return getSpincastConfig().getDefaultRouteCacheFilterSecondsNbr();
    }

    protected boolean isCachePrivateByDefault() {
        return getSpincastConfig().isDefaultRouteCacheFilterPrivate();
    }

    protected Integer getCacheCdnSecondsByDefault() {
        return getSpincastConfig().getDefaultRouteCacheFilterSecondsNbrCdns();
    }

    @Override
    public void addDefaultGlobalTemplateVariables(final R context) {

        //==========================================
        // Gets the Spincast reserved Map from the
        // templating variables.
        //==========================================
        Map<String, Object> map = context.templating().getSpincastReservedMap();

        //==========================================
        // If the route has been forwarded, we delete
        // the current variables... Otherwise, things like
        // alerts for flash messages may be duplicated.
        //==========================================
        if (context.routing().isForwarded()) {
            map.clear();
        }

        //==========================================
        // The Language abreviation
        //==========================================
        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_LANG_ABREV,
                context.getLocaleToUse().getLanguage());

        //==========================================
        // The current Spincast version
        //==========================================
        String currentVersion = getSpincastUtils().getSpincastCurrentVersion();
        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_SPINCAST_CURRENT_VERSION,
                currentVersion);
        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_SPINCAST_CURRENT_VERSION_IS_SNAPSHOT,
                currentVersion.contains("-SNAPSHOT"));

        //==========================================
        // Cache buster
        //==========================================
        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_CACHE_BUSTER,
                getSpincastUtils().getCacheBusterCode());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_REQUEST_SCOPED_VARIABLES,
                context.variables().getAll());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ROUTE_ID,
                context.routing().getRoutingResult().getMainRouteHandlerMatch()
                       .getSourceRoute().getId());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ROUTE_PATH,
                context.routing().getRoutingResult().getMainRouteHandlerMatch()
                       .getSourceRoute().getPath());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_URL_ROOT,
                getSpincastConfig().getPublicUrlBase());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_FULL_URL,
                context.request().getFullUrl());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_IS_HTTPS,
                context.request().isHttps());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_PATH_PARAMS,
                context.routing().getRoutingResult().getMainRouteHandlerMatch()
                       .getPathParams());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_QUERYSTRING_PARAMS,
                context.request().getQueryStringParams());

        map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_COOKIES,
                context.request().getCookiesValues());

        //==========================================
        // Flash message : we add it to the
        // "alerts" templating variable.
        //==========================================
        if (context.request().isFlashMessageExists()) {
            @SuppressWarnings("unchecked")
            List<Alert> alerts =
                    (List<Alert>)map.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ALERTS);
            if (alerts == null) {
                alerts = new ArrayList<Alert>();
                map.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ALERTS, alerts);
            }

            //==========================================
            // We lazy load the Flash message to make sure
            // that only when the Flash Message is actually needed
            // it is retrieved from the request. Otherwise, some
            // requests for various resources may pass here, retrieve
            // the Flash message and delete it (since it is deleted
            // as soon as it is used).
            //==========================================
            boolean lazyLoadedAlertAlreadyAdded = false;
            for (Alert alert : alerts) {
                if (alert instanceof SpincastFiltersDefault.LazyLoadedFlashMessageAlert) {
                    lazyLoadedAlertAlreadyAdded = true;
                    break;
                }
            }
            if (!lazyLoadedAlertAlreadyAdded) {
                alerts.add(new LazyLoadedFlashMessageAlert(context));
            }
        }
    }

    protected class LazyLoadedFlashMessageAlert implements Alert {

        private FlashMessage flashMessage;
        private boolean flashMessageGot;
        private R context;

        public LazyLoadedFlashMessageAlert(R context) {
            this.context = context;
        }

        protected FlashMessage getFlashMessage() {
            if (!this.flashMessageGot) {
                this.flashMessageGot = true;
                this.flashMessage = this.context.request().getFlashMessage();
            }
            return this.flashMessage;
        }

        @Override
        public String getText() {
            return getFlashMessage() != null ? getFlashMessage().getText() : null;
        }

        @Override
        public AlertLevel getAlertType() {

            if (getFlashMessage() == null) {
                return null;
            }

            FlashMessageLevel flashType = getFlashMessage().getFlashType();
            if (flashType == FlashMessageLevel.SUCCESS) {
                return AlertLevel.SUCCESS;
            } else if (flashType == FlashMessageLevel.WARNING) {
                return AlertLevel.WARNING;
            } else if (flashType == FlashMessageLevel.ERROR) {
                return AlertLevel.ERROR;
            } else if (flashType == FlashMessageLevel.SIMPLE_MESSAGE) {
                return AlertLevel.SIMPLE_MESSAGE;
            } else {
                throw new RuntimeException("Flash type not managed here : " + flashType);
            }
        }
    }

}

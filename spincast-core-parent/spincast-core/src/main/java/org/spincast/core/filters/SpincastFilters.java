package org.spincast.core.filters;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exceptions.SkipRemainingHandlersException;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Spincast filters implementations.
 */
public class SpincastFilters<R extends IRequestContext<?>> implements ISpincastFilters<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFilters.class);

    private final ICorsFilter corsFilter;

    /**
     * Constructor
     */
    @Inject
    public SpincastFilters(ICorsFilter corsFilter) {
        this.corsFilter = corsFilter;
    }

    protected ICorsFilter getCorsFilter() {
        return this.corsFilter;
    }

    @Override
    public void saveGeneratedResource(R context, String pathForGeneratedResource) {

        try {

            File resourceFile = new File(pathForGeneratedResource);

            //==========================================
            // Check if the main handler has saved the 
            // generated resource by itself...
            //==========================================
            if(resourceFile.exists()) {
                this.logger.info("The resource already exists. We don't save it here.");
                return;
            }

            if(HttpStatus.SC_OK != context.response().getStatusCode()) {
                this.logger.info("Nothing will be saved since the response code is not " + HttpStatus.SC_OK);
                return;
            }

            if(context.response().isHeadersSent()) {
                this.logger.warn("Headers sent, we can't save a copy of the generated resource! You will have to make sure that " +
                                 "you save the generated resource by yourself, otherwise, a new version will be generated for each " +
                                 "request!");
                return;
            }

            byte[] unsentBytes = context.response().getUnsentBytes();

            FileUtils.writeByteArrayToFile(resourceFile, unsentBytes);

        } catch(Exception ex) {
            this.logger.error("Unable to save the generated resource '" + pathForGeneratedResource + "' :\n" +
                              SpincastStatics.getStackTrace(ex));

            // we still let the reponse being sent...
        }
    }

    @Override
    public void addSecurityHeaders(R context) {
        //context.response().addHeaderValue("Content-Security-Policy", "default-src 'self'");
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

        ICorsFilterClient corsFilterClient = createCorsFilterClient(context,
                                                                    allowedOrigins,
                                                                    extraHeadersAllowedToBeRead,
                                                                    extraHeadersAllowedToBeSent,
                                                                    allowCookies,
                                                                    allowedMethods,
                                                                    maxAgeInSeconds);

        CorsFilterResponse corsResult = getCorsFilter().apply(corsFilterClient);

        if(corsResult == CorsFilterResponse.NOT_CORS) {

            //==========================================
            // Not a cors request, or same origin...
            // No need to do anything here.
            //==========================================
            return;

        } else if(corsResult == CorsFilterResponse.HEADERS_ALREADY_SENT) {

            //==========================================
            // Headers already sent? There is nothing we can do...
            //==========================================
            return;

        } else if(corsResult == CorsFilterResponse.INVALID_CORS_REQUEST) {

            //==========================================
            // Invalid request, we return OK without the
            // cors headers.
            //==========================================
            throw new SkipRemainingHandlersException();

        } else if(corsResult == CorsFilterResponse.SIMPLE) {

            //==========================================
            // Simple cors request (not a Preflight).
            // We added the required headers and our job is now done.
            // The routing process can continue...
            //==========================================
            return;

        } else if(corsResult == CorsFilterResponse.PREFLIGHT) {

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
    protected ICorsFilterClient createCorsFilterClient(final R context,
                                                       final Set<String> allowedOrigins,
                                                       final Set<String> extraHeadersAllowedToBeRead,
                                                       final Set<String> extraHeadersAllowedToBeSent,
                                                       final boolean allowCookies,
                                                       final Set<HttpMethod> allowedMethods,
                                                       final int maxAgeInSeconds) {
        return new ICorsFilterClient() {

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

                Map<String, ICookie> cookies = context.cookies().getCookies();
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

}

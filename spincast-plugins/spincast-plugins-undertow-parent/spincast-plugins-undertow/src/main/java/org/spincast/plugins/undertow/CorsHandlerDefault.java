package org.spincast.plugins.undertow;

import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.CorsFilter;
import org.spincast.core.filters.CorsFilterClient;
import org.spincast.core.filters.CorsFilterResponse;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.StaticResourceCorsConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class CorsHandlerDefault implements CorsHandler {

    protected static final Logger logger = LoggerFactory.getLogger(CorsHandlerDefault.class);

    private final HttpHandler nextHandler;
    private final StaticResourceCorsConfig corsConfig;
    private final CorsFilter corsFilter;

    @AssistedInject
    public CorsHandlerDefault(@Assisted HttpHandler nextHandler,
                              @Assisted @Nullable StaticResourceCorsConfig corsConfig,
                              CorsFilter corsFilter) {
        this.nextHandler = nextHandler;
        this.corsConfig = corsConfig;
        this.corsFilter = corsFilter;
    }

    protected HttpHandler getNextHandler() {
        return this.nextHandler;
    }

    protected StaticResourceCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

    protected CorsFilter getCorsFilter() {
        return this.corsFilter;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        StaticResourceCorsConfig corsConfig = getCorsConfig();

        do {

            if (corsConfig == null) {
                break;
            }

            //==========================================
            // To speed things up with static resources, we
            // don't even call the cors filter is the "Origin"
            // header is not present.
            //==========================================
            String origin = exchange.getRequestHeaders().getFirst(HttpHeaders.ORIGIN.toLowerCase());
            if (origin == null) {
                break;
            }

            //==========================================
            // Only some methods are acceptable for
            // requests to static resources.
            //==========================================
            HttpString httpString = exchange.getRequestMethod();
            HttpMethod httpMethod = HttpMethod.fromStringValue(httpString.toString());
            if (httpMethod == null || (HttpMethod.OPTIONS != httpMethod &&
                                       HttpMethod.GET != httpMethod &&
                                       HttpMethod.HEAD != httpMethod)) {
                break;
            }

            CorsFilterClient corsFilterClient = createCorsFilterClient(exchange, corsConfig);

            CorsFilterResponse corsResult = getCorsFilter().apply(corsFilterClient);

            if (corsResult == CorsFilterResponse.NOT_CORS) {

                //==========================================
                // Not a cors request, or same origin...
                // No need to do anything here.
                //==========================================
                break;

            } else if (corsResult == CorsFilterResponse.HEADERS_ALREADY_SENT) {

                //==========================================
                // Headers already sent? There is nothing we can do...
                //==========================================
                break;

            } else if (corsResult == CorsFilterResponse.INVALID_CORS_REQUEST) {

                //==========================================
                // Invalid request, we return OK but without any
                // cors headers...
                //==========================================
                return;

            } else if (corsResult == CorsFilterResponse.SIMPLE) {

                //==========================================
                // Simple cors request (not a Preflight).
                // We added the required headers and our job is now done.
                // The routing process can continue...
                //==========================================
                break;

            } else if (corsResult == CorsFilterResponse.PREFLIGHT) {

                //==========================================
                // We always skip all any remaining process
                // on a Preflight request!
                //==========================================
                exchange.endExchange();
                return;

            } else {
                throw new RuntimeException("Unmanaged cors result: " + corsResult);
            }

        } while (false);

        //==========================================
        // Serves the resource!
        //==========================================
        getNextHandler().handleRequest(exchange);
    }

    /**
     * Create the client for the cors filter.
     */
    protected CorsFilterClient createCorsFilterClient(final HttpServerExchange exchange,
                                                      final StaticResourceCorsConfig corsConfig) {

        return new CorsFilterClient() {

            @Override
            public void setStatusCode(int code) {
                exchange.setStatusCode(code);
            }

            @Override
            public void resetEverything() {
                exchange.getResponseHeaders().clear();
            }

            @Override
            public boolean requestContainsCookies() {
                boolean hasCookies = exchange.requestCookies().iterator().hasNext();
                return hasCookies;
            }

            @Override
            public boolean isHeadersSent() {
                return exchange.isResponseStarted();
            }

            @Override
            public HttpMethod getHttpMethod() {

                HttpString requestMethod = exchange.getRequestMethod();
                return HttpMethod.fromStringValue(requestMethod.toString());
            }

            @Override
            public String getHeaderFirst(String name) {
                return exchange.getRequestHeaders().getFirst(name);
            }

            @Override
            public void addHeaderValue(String name, String value) {
                exchange.getResponseHeaders().add(new HttpString(name), value);
            }

            @Override
            public String getFullUrl() {
                String queryString = exchange.getQueryString();
                if (StringUtils.isBlank(queryString)) {
                    queryString = "";
                } else {
                    queryString = "?" + queryString;
                }

                return exchange.getRequestURL() + queryString;
            }

            @Override
            public Set<String> getAllowedOrigins() {
                return corsConfig.getAllowedOrigins();
            }

            @Override
            public Set<String> getExtraHeadersAllowedToBeRead() {
                return corsConfig.getExtraHeadersAllowedToBeRead();
            }

            @Override
            public Set<String> getExtraHeadersAllowedToBeSent() {
                return corsConfig.getExtraHeadersAllowedToBeSent();
            }

            @Override
            public boolean isAllowCookies() {
                return corsConfig.isAllowCookies();
            }

            @Override
            public Set<HttpMethod> getAllowedMethods() {
                return getStaticResourceCorsAllowedMethods();
            }

            @Override
            public int getMaxAgeInSeconds() {
                return corsConfig.getMaxAgeInSeconds();
            }
        };
    }

    /**
     * Those are the valid HTTP methods for requests to
     * static resources served directly by the HTTP server.
     */
    protected Set<HttpMethod> getStaticResourceCorsAllowedMethods() {

        Set<HttpMethod> allowedMethods = Sets.newHashSet(HttpMethod.OPTIONS,
                                                         HttpMethod.GET,
                                                         HttpMethod.HEAD);
        return allowedMethods;
    }
}

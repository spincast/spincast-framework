package org.spincast.core.filters;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;

/**
 * Cors filter implementation.
 */
public class CorsFilter implements ICorsFilter {

    protected final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public CorsFilterResponse apply(ICorsFilterClient corsFilterClient) {

        Objects.requireNonNull(corsFilterClient, "corsFilterClient can't be NULL");

        String origin = corsFilterClient.getHeaderFirst(HttpHeaders.ORIGIN);

        //==========================================
        // No "Origin" header == Not a cors request!
        // No need to do anything here.
        //==========================================
        if(origin == null) {
            return CorsFilterResponse.NOT_CORS;
        }

        //==========================================
        // Same origin request? No need for
        // cors headers.
        //==========================================
        String host = corsFilterClient.getHeaderFirst(HttpHeaders.HOST);
        if(host != null) {
            try {
                String originHost = new URI(origin).getHost();
                if(host.equals(originHost)) {
                    return CorsFilterResponse.NOT_CORS;
                }
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        //==========================================
        // Headers already sent? There is nothing we can do...
        //==========================================
        if(corsFilterClient.isHeadersSent()) {
            //==========================================
            // We can't be sure this is a cors request, even if the
            // "Origin" is present. So we only log an *error* level
            // message if another cors header is present.
            //==========================================
            String message = "Headers already sent: if this is a cors request, it will fail. " +
                             "The request URL is: " + corsFilterClient.getFullUrl();
            if(corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null) {
                this.logger.error(message);
            } else {
                this.logger.info(message);
            }
            return CorsFilterResponse.HEADERS_ALREADY_SENT;
        }

        Set<String> allowedOrigins = corsFilterClient.getAllowedOrigins();
        Set<String> allowedOriginsLowercased = new HashSet<String>();
        if(allowedOrigins == null) {
            allowedOrigins = new HashSet<>();
        }
        for(String allowedOrigin : allowedOrigins) {
            if(allowedOrigin != null) {
                allowedOriginsLowercased.add(allowedOrigin.toLowerCase().trim());
            }
        }

        Set<String> extraHeadersAllowedToBeRead = corsFilterClient.getExtraHeadersAllowedToBeRead();
        Set<String> extraHeadersAllowedToBeReadLowercased = new HashSet<String>();
        if(extraHeadersAllowedToBeRead == null) {
            extraHeadersAllowedToBeRead = new HashSet<>();
        }
        for(String headerAllowedToBeRead : extraHeadersAllowedToBeRead) {
            if(headerAllowedToBeRead != null) {
                extraHeadersAllowedToBeReadLowercased.add(headerAllowedToBeRead.toLowerCase().trim());
            }
        }

        Set<String> extraHeadersAllowedToBeSent = corsFilterClient.getExtraHeadersAllowedToBeSent();
        Set<String> extraHeadersAllowedToBeSentLowercased = new HashSet<String>();
        if(extraHeadersAllowedToBeSent == null) {
            extraHeadersAllowedToBeSent = new HashSet<>();
        }
        for(String extraHeaderAllowedToBeSent : extraHeadersAllowedToBeSent) {
            if(extraHeaderAllowedToBeSent != null) {
                extraHeadersAllowedToBeSentLowercased.add(extraHeaderAllowedToBeSent.toLowerCase().trim());
            }
        }

        Set<HttpMethod> allowedMethods = corsFilterClient.getAllowedMethods();
        if(allowedMethods == null) {
            allowedMethods = new HashSet<HttpMethod>();
        }
        //==========================================
        // OPTIONS should always be allowed.
        //==========================================
        allowedMethods.add(HttpMethod.OPTIONS);

        //==========================================
        // Validation of the origin
        //==========================================
        if(!isCorsOriginValid(corsFilterClient, allowedOriginsLowercased)) {
            corsFilterClient.resetEverything();
            corsFilterClient.setStatusCode(HttpStatus.SC_OK);
            this.logger.info("Invalid origin for a cors request : " + origin);
            return CorsFilterResponse.INVALID_CORS_REQUEST;
        }

        //==========================================
        // Not a Preflight request.
        // We add some required headers and our job is done.
        // The routing process can continue...
        //==========================================
        if(!isPreflightRequest(corsFilterClient)) {
            corsCore(corsFilterClient, allowedOrigins, corsFilterClient.isAllowCookies());
            corsAddExtraHeadersAllowedToBeRead(corsFilterClient, extraHeadersAllowedToBeRead);
            return CorsFilterResponse.SIMPLE;
        }

        //==========================================
        // Preflight request!
        //==========================================
        corsFilterClient.resetEverything();
        corsFilterClient.setStatusCode(HttpStatus.SC_OK);

        //==========================================
        // Validate requested HTTP methods
        //==========================================
        boolean preflightRequestValid = true;
        if(!isCorsRequestMethodHeaderValid(corsFilterClient, allowedMethods)) {
            this.logger.info("Invalid 'Access-Control-Allow-Methods' cors header received : " +
                             corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
            preflightRequestValid = false;
        }

        //==========================================
        // Validate extra headers to be sent.
        //==========================================
        if(preflightRequestValid) {
            if(!isCorsRequestedHeadersToBeSentValid(corsFilterClient, extraHeadersAllowedToBeSentLowercased)) {
                this.logger.info("Invalid 'Access-Control-Request-Headers' cors header received : " +
                                 corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));
                preflightRequestValid = false;
            }
        }

        //==========================================
        // If the preflight request is valid, we add 
        // the required cors headers.
        //==========================================
        if(preflightRequestValid) {
            corsCore(corsFilterClient, allowedOrigins, corsFilterClient.isAllowCookies());
            corsAddAllowMethods(corsFilterClient, allowedMethods);
            corsAddExtraHeadersAllowedToBeSent(corsFilterClient, extraHeadersAllowedToBeSent);
            corsAddMaxAge(corsFilterClient, corsFilterClient.getMaxAgeInSeconds());
        }

        return CorsFilterResponse.PREFLIGHT;
    }

    protected boolean isCorsOriginValid(ICorsFilterClient corsFilterClient, Set<String> allowedOriginsLowercased) {

        if(allowedOriginsLowercased.contains("*")) {
            return true;
        }

        String origin = corsFilterClient.getHeaderFirst(HttpHeaders.ORIGIN).toLowerCase();
        if(allowedOriginsLowercased.contains(origin)) {
            return true;
        }

        return false;
    }

    protected void corsCore(ICorsFilterClient corsFilterClient, Set<String> allowedOrigins, boolean allowCookies) {

        //==========================================
        // "Allow Origin" header : always required
        //==========================================
        corsAddAllowOrigin(corsFilterClient);

        //==========================================
        // Do we "Allow Credentials" (cookies)?
        //==========================================
        if(allowCookies) {
            corsAddAllowCookies(corsFilterClient);
        }
    }

    protected boolean isCorsRequestMethodHeaderValid(ICorsFilterClient corsFilterClient, Set<HttpMethod> allowedMethods) {

        if(allowedMethods == null || allowedMethods.size() == 0) {
            return false;
        }

        String accessControlRequestMethodsStr = corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        if(accessControlRequestMethodsStr == null) {
            return false;
        }

        String[] accessControlRequestMethods = StringUtils.split(accessControlRequestMethodsStr, ",");
        if(accessControlRequestMethods == null || accessControlRequestMethods.length == 0) {
            return false;
        }

        for(String accessControlRequestMethod : accessControlRequestMethods) {
            HttpMethod method = HttpMethod.fromStringValue(accessControlRequestMethod);
            if(method == null) {
                return false;
            }
            if(!allowedMethods.contains(method)) {
                return false;
            }
        }

        return true;
    }

    protected boolean isCorsRequestedHeadersToBeSentValid(ICorsFilterClient corsFilterClient,
                                                          Set<String> extraHeadersAllowedToBeSentLowercased) {

        String requestedHeadersStr = corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        if(requestedHeadersStr == null) {
            return true;
        }

        String[] requestedHeaders = StringUtils.split(requestedHeadersStr, ",");
        if(requestedHeaders == null || requestedHeaders.length == 0) {
            return true;
        }

        if(extraHeadersAllowedToBeSentLowercased.contains("*")) {
            return true;
        }

        if(extraHeadersAllowedToBeSentLowercased == null || extraHeadersAllowedToBeSentLowercased.size() == 0) {
            return false;
        }

        for(String requestedHeader : requestedHeaders) {
            if(!extraHeadersAllowedToBeSentLowercased.contains(requestedHeader.toLowerCase().trim())) {
                return false;
            }
        }

        return true;
    }

    protected boolean isPreflightRequest(ICorsFilterClient corsFilterClient) {
        HttpMethod httpMethod = corsFilterClient.getHttpMethod();
        if(httpMethod == HttpMethod.OPTIONS) {
            String accessControlRequestMethod = corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
            if(accessControlRequestMethod != null) {
                return true;
            }
        }
        return false;
    }

    protected void corsAddExtraHeadersAllowedToBeRead(ICorsFilterClient corsFilterClient,
                                                      Set<String> extraHeadersAllowedToBeRead) {

        String extraHeadersAllowedToBeReadStr = "";
        if(extraHeadersAllowedToBeRead != null && extraHeadersAllowedToBeRead.size() > 0) {
            extraHeadersAllowedToBeReadStr = StringUtils.join(extraHeadersAllowedToBeRead, ",");
        }

        corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, extraHeadersAllowedToBeReadStr);
    }

    protected void corsAddExtraHeadersAllowedToBeSent(ICorsFilterClient corsFilterClient,
                                                      Set<String> extraHeadersAllowedToBeSent) {

        String extraHeadersAllowedToBeSentStr = "";
        if(extraHeadersAllowedToBeSent != null && extraHeadersAllowedToBeSent.size() > 0) {

            //==========================================
            // If we allow all headers to be sent, we simply copy
            // the requested ones.
            //==========================================
            if(extraHeadersAllowedToBeSent.contains("*")) {
                String requestedHeadersStr = corsFilterClient.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
                if(!StringUtils.isBlank(requestedHeadersStr)) {
                    extraHeadersAllowedToBeSentStr = requestedHeadersStr;
                }
            } else {
                extraHeadersAllowedToBeSentStr = StringUtils.join(extraHeadersAllowedToBeSent, ",");
            }
        } else {
            Set<String> defaultExtraHeadersAllowedToBeSent = getDefaultHeadersAllowedToBeSent();
            if(defaultExtraHeadersAllowedToBeSent != null && defaultExtraHeadersAllowedToBeSent.size() > 0) {
                extraHeadersAllowedToBeSentStr = StringUtils.join(defaultExtraHeadersAllowedToBeSent, ",");
            }
        }

        corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, extraHeadersAllowedToBeSentStr);
    }

    protected Set<String> getDefaultHeadersAllowedToBeSent() {
        return null;
    }

    protected void corsAddMaxAge(ICorsFilterClient corsFilterClient, int maxAgeInSeconds) {
        if(maxAgeInSeconds > 0) {
            corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(maxAgeInSeconds));
        }
    }

    protected void corsAddAllowMethods(ICorsFilterClient corsFilterClient, Set<HttpMethod> allowedMethods) {

        if(allowedMethods == null || allowedMethods.size() == 0) {
            return;
        }
        String allowedMethodsStr = StringUtils.join(allowedMethods, ",");

        corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethodsStr);
    }

    protected void corsAddAllowCookies(ICorsFilterClient corsFilterClient) {
        corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    protected void corsAddAllowOrigin(ICorsFilterClient corsFilterClient) {

        //==========================================
        // We never use a wildcard for the "Access-Control-Allow-Origin" header
        // as it's sometimes invalid to do so, and it doesn't provide any
        // benefits anyway.
        //
        // @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS#Requests_with_credentials
        // @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS#Access-Control-Allow-Origin
        // @see http://stackoverflow.com/questions/36992199/cors-how-can-the-server-know-if-jquery-ajaxs-withcredentials-true-was-use
        //==========================================
        String allowedOriginsStr = corsFilterClient.getHeaderFirst(HttpHeaders.ORIGIN);
        corsFilterClient.addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedOriginsStr);
        corsFilterClient.addHeaderValue(HttpHeaders.VARY, HttpHeaders.ORIGIN);
    }

}

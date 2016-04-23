package org.spincast.core.filters;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
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
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

/**
 * Spincast filters implementations.
 */
public class SpincastFilters<R extends IRequestContext<?>> implements ISpincastFilters<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFilters.class);

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
             Sets.newHashSet("*"),
             null,
             Sets.newHashSet("*"),
             true,
             Sets.newHashSet(HttpMethod.values()),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context, Set<String> allowedOrigins) {
        cors(context,
             allowedOrigins,
             null,
             Sets.newHashSet("*"),
             true,
             Sets.newHashSet(HttpMethod.values()),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context, Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead) {
        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             Sets.newHashSet("*"),
             true,
             Sets.newHashSet(HttpMethod.values()),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context, Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent) {
        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             true,
             Sets.newHashSet(HttpMethod.values()),
             getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public void cors(R context, Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent, boolean allowCookies) {
        cors(context,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies,
             Sets.newHashSet(HttpMethod.values()),
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

        String origin = context.request().getHeaderFirst(HttpHeaders.ORIGIN);

        //==========================================
        // No "Origin" header == Not a cors request!
        // No need to do anything here.
        //==========================================
        if(origin == null) {
            return;
        }

        //==========================================
        // Same origin request? No need for
        // cors headers.
        //==========================================
        String host = context.request().getHeaderFirst(HttpHeaders.HOST);
        if(host != null) {
            try {
                String originHost = new URI(origin).getHost();
                if(host.equals(originHost)) {
                    return;
                }
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        //==========================================
        // Headers already sent? There is nothing we can do...
        //==========================================
        if(context.response().isHeadersSent()) {
            //==========================================
            // We can't be sure this is a cors request, even if the
            // "Origin" is present. So we only log an *error* level
            // message if another cors header is present.
            //==========================================
            String message = "Headers already sent: if this is a cors request, it will fail. " +
                             "The request URL is: " + context.request().getFullUrl();
            if(context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null) {
                this.logger.error(message);
            } else {
                this.logger.info(message);
            }
            return;
        }

        Set<String> allowedOriginsLowercased = new HashSet<String>();
        if(allowedOrigins == null) {
            allowedOrigins = new HashSet<>();
        }
        for(String allowedOrigin : allowedOrigins) {
            if(allowedOrigin != null) {
                allowedOriginsLowercased.add(allowedOrigin.toLowerCase().trim());
            }
        }

        Set<String> extraHeadersAllowedToBeReadLowercased = new HashSet<String>();
        if(extraHeadersAllowedToBeRead == null) {
            extraHeadersAllowedToBeRead = new HashSet<>();
        }
        for(String headerAllowedToBeRead : extraHeadersAllowedToBeRead) {
            if(headerAllowedToBeRead != null) {
                extraHeadersAllowedToBeReadLowercased.add(headerAllowedToBeRead.toLowerCase().trim());
            }
        }

        Set<String> extraHeadersAllowedToBeSentLowercased = new HashSet<String>();
        if(extraHeadersAllowedToBeSent == null) {
            extraHeadersAllowedToBeSent = new HashSet<>();
        }
        for(String extraHeaderAllowedToBeSent : extraHeadersAllowedToBeSent) {
            if(extraHeaderAllowedToBeSent != null) {
                extraHeadersAllowedToBeSentLowercased.add(extraHeaderAllowedToBeSent.toLowerCase().trim());
            }
        }

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
        if(!isCorsOriginValid(context, allowedOriginsLowercased)) {
            this.logger.info("Invalid origin for a cors request : " + origin);

            context.response().resetEverything();
            context.response().setStatusCode(HttpStatus.SC_OK);
            throw new SkipRemainingHandlersException();
        }

        //==========================================
        // Not a Preflight request.
        // We add some required headers and our job is done.
        // The routing process can continue...
        //==========================================
        if(!isPreflightRequest(context)) {
            corsCore(context, allowedOrigins, allowCookies);
            corsAddExtraHeadersAllowedToBeRead(context, extraHeadersAllowedToBeRead);
            return;
        }

        //==========================================
        // Preflight request!
        //==========================================
        context.response().resetEverything();
        context.response().setStatusCode(HttpStatus.SC_OK);

        //==========================================
        // Validate requested HTTP methods
        //==========================================
        boolean preflightRequestValid = true;
        if(!isCorsRequestMethodHeaderValid(context, allowedMethods)) {
            this.logger.info("Invalid 'Access-Control-Allow-Methods' cors header received : " +
                             context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
            preflightRequestValid = false;
        }

        //==========================================
        // Validate extra headers to be sent.
        //==========================================
        if(preflightRequestValid) {
            if(!isCorsRequestedHeadersToBeSentValid(context, extraHeadersAllowedToBeSentLowercased)) {
                this.logger.info("Invalid 'Access-Control-Request-Headers' cors header received : " +
                                 context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));
                preflightRequestValid = false;
            }
        }

        //==========================================
        // If the preflight request is valid, we add 
        // the required cors headers.
        //==========================================
        if(preflightRequestValid) {
            corsCore(context, allowedOrigins, allowCookies);
            corsAddAllowMethods(context, allowedMethods);
            corsAddExtraHeadersAllowedToBeSent(context, extraHeadersAllowedToBeSent);
            corsAddMaxAge(context, maxAgeInSeconds);
        }

        //==========================================
        // We always skip all remaining handlers 
        // on a Preflight request!
        // For example if the request is for a "dynamic resource",
        // you don't want to run the "saveGeneratedResource" after
        // filter, which would save an empty resource on the
        // preflight request...
        //==========================================
        throw new SkipRemainingHandlersException();
    }

    /**
     * If <= 0, the "Access-Control-Max-Age" header
     * won't be sent.
     */
    protected int getCorsDefaultMaxAgeInSeconds() {
        return 86400; // 24h
    }

    protected boolean isCorsOriginValid(R context, Set<String> allowedOriginsLowercased) {

        if(allowedOriginsLowercased.contains("*")) {
            return true;
        }

        String origin = context.request().getHeaderFirst(HttpHeaders.ORIGIN).toLowerCase();
        if(allowedOriginsLowercased.contains(origin)) {
            return true;
        }

        return false;
    }

    protected void corsCore(R context, Set<String> allowedOrigins, boolean allowCookies) {

        //==========================================
        // "Allow Origin" header : always required
        //==========================================
        corsAddAlloweOrigin(context, allowedOrigins);

        //==========================================
        // Do we "Allow Credentials" (cookies)?
        //==========================================
        if(allowCookies) {
            corsAddAllowCookies(context);
        }
    }

    protected boolean isCorsRequestMethodHeaderValid(R context, Set<HttpMethod> allowedMethods) {

        if(allowedMethods == null || allowedMethods.size() == 0) {
            return false;
        }

        String accessControlRequestMethodsStr = context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
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

    protected boolean isCorsRequestedHeadersToBeSentValid(R context, Set<String> extraHeadersAllowedToBeSentLowercased) {

        String requestedHeadersStr = context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
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

    protected boolean isPreflightRequest(R context) {
        HttpMethod httpMethod = context.request().getHttpMethod();
        if(httpMethod == HttpMethod.OPTIONS) {
            String accessControlRequestMethod = context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
            if(accessControlRequestMethod != null) {
                return true;
            }
        }
        return false;
    }

    protected void corsAddExtraHeadersAllowedToBeRead(R context, Set<String> extraHeadersAllowedToBeRead) {

        String extraHeadersAllowedToBeReadStr = "";
        if(extraHeadersAllowedToBeRead != null && extraHeadersAllowedToBeRead.size() > 0) {
            extraHeadersAllowedToBeReadStr = StringUtils.join(extraHeadersAllowedToBeRead, ",");
        }

        context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, extraHeadersAllowedToBeReadStr);
    }

    protected void corsAddExtraHeadersAllowedToBeSent(R context, Set<String> extraHeadersAllowedToBeSent) {

        String extraHeadersAllowedToBeSentStr = "";
        if(extraHeadersAllowedToBeSent != null && extraHeadersAllowedToBeSent.size() > 0) {

            //==========================================
            // If we allow all headers to be sent, we simply copy
            // the requested ones.
            //==========================================
            if(extraHeadersAllowedToBeSent.contains("*")) {
                String requestedHeadersStr = context.request().getHeaderFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
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

        context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, extraHeadersAllowedToBeSentStr);
    }

    protected Set<String> getDefaultHeadersAllowedToBeSent() {
        return null;
    }

    protected void corsAddMaxAge(R context, int maxAgeInSeconds) {
        if(maxAgeInSeconds > 0) {
            context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(maxAgeInSeconds));
        }
    }

    protected void corsAddAllowMethods(R context, Set<HttpMethod> allowedMethods) {

        if(allowedMethods == null || allowedMethods.size() == 0) {
            return;
        }
        String allowedMethodsStr = StringUtils.join(allowedMethods, ",");

        context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethodsStr);
    }

    protected void corsAddAllowCookies(R context) {
        context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    protected void corsAddAlloweOrigin(R context, Set<String> allowedOrigins) {

        if(allowedOrigins == null || allowedOrigins.size() == 0) {
            return;
        }

        //==========================================
        // When responding to a credentialed request,  
        // server must specify a domain, and cannot use wild carding.  
        // @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS#Requests_with_credentials
        //==========================================
        Map<String, ICookie> cookies = context.cookies().getCookies();
        boolean hasCookies = cookies != null && cookies.size() > 0;

        String allowedOriginsStr;
        if(!hasCookies && allowedOrigins.contains("*")) {
            allowedOriginsStr = "*";
        } else {
            allowedOriginsStr = context.request().getHeaderFirst(HttpHeaders.ORIGIN);

            // See https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS#Access-Control-Allow-Origin
            context.response().addHeaderValue(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        }

        context.response().addHeaderValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedOriginsStr);
    }

}

package org.spincast.core.controllers;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exceptions.CustomStatusCodeException;
import org.spincast.core.exceptions.ForwardRouteException;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exceptions.ResponseResetableException;
import org.spincast.core.exceptions.SkipRemainingHandlersException;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.server.Server;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SpincastFrontController<R extends RequestContext<R>, W extends WebsocketContext<?>> implements FrontController {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFrontController.class);

    private final Router<R, W> router;
    private final SpincastConfig spincastConfig;
    private final SpincastDictionary spincastDictionary;
    private final Server server;
    private final RequestContextFactory<R> requestCreationFactory;
    private final SpincastRequestScope spincastRequestScope;
    private final Type requestContextType;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;

    /**
     * The constructor.
     */
    @Inject
    public SpincastFrontController(Router<R, W> router,
                                   SpincastConfig spincastConfig,
                                   SpincastDictionary spincastDictionary,
                                   Server server,
                                   RequestContextFactory<R> requestCreationFactory,
                                   SpincastRequestScope spincastRequestScope,
                                   @RequestContextType Type requestContextType,
                                   JsonManager jsonManager,
                                   XmlManager xmlManager) {
        this.router = router;
        this.spincastConfig = spincastConfig;
        this.spincastDictionary = spincastDictionary;
        this.server = server;
        this.requestCreationFactory = requestCreationFactory;
        this.spincastRequestScope = spincastRequestScope;
        this.requestContextType = requestContextType;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastDictionary getSpincastDictionary() {
        return this.spincastDictionary;
    }

    protected Server getServer() {
        return this.server;
    }

    protected RequestContextFactory<R> getRequestContextFactory() {
        return this.requestCreationFactory;
    }

    protected SpincastRequestScope getSpincastRequestScope() {
        return this.spincastRequestScope;
    }

    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    /**
     * Front controller's handle() method, called by the HTTP
     * server.
     */
    @Override
    public void handle(Object exchange) {

        //==========================================
        // We start our custom request Guice scope here!
        //==========================================
        getSpincastRequestScope().enter();
        R requestContext = null;
        RoutingResult<R> routingResult = null;
        try {

            exchange = validateExchange(exchange);

            requestContext = getRequestContextFactory().createRequestContext(exchange);

            //==========================================
            // Add dependencies to the custom request scope
            //==========================================
            addDependenciesInCustomRequestScope(requestContext);

            routingResult = findRouteMatch(requestContext);

            //==========================================
            // Route not found? Use the Not Found route then...
            //
            // This direct Not Found route will have all the "before"
            // and "after" filters run, like a regular route.
            //==========================================
            if(routingResult == null) {
                routingResult = prepareNotFoundRouting(exchange, requestContext);
            }

            try {
                callRouteHandlers(requestContext, routingResult);
            } catch(NotFoundException notFoundException) {

                if(notFoundException.isResetResponse()) {
                    resetResponse(requestContext);
                }

                routingResult = prepareNotFoundRouting(exchange, requestContext);

                // Keeps the custom Not Found message, if any.
                requestContext.variables().add(SpincastConstants.RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE,
                                               notFoundException.getMessage());

                callRouteHandlers(requestContext, routingResult);
            }

        } catch(Throwable ex) {

            //==========================================
            // Custom exception handling, if any.
            //==========================================
            try {
                // No requestContext? We can't run the custom
                // handler.
                if(requestContext == null) {
                    throw ex;
                }

                //==========================================
                // Flag the request context as being in
                // the Exception handling process and add the
                // exception as a variable.
                //==========================================
                requestContext.variables().add(SpincastConstants.RequestScopedVariables.IS_EXCEPTION_HANDLING, true);
                requestContext.variables().add(SpincastConstants.RequestScopedVariables.IS_NOT_FOUND_ROUTE, false);
                requestContext.variables().add(SpincastConstants.RequestScopedVariables.EXCEPTION, ex);

                //==========================================
                // If the headers are not sent yet, we reset everything.
                //==========================================
                if(!(ex instanceof ResponseResetableException) || ((ResponseResetableException)ex).isResetResponse()) {
                    resetResponse(requestContext);
                }

                //==========================================
                // Default exception HTTP status.
                //==========================================
                if(ex instanceof CustomStatusCodeException) {
                    requestContext.response().setStatusCode(((CustomStatusCodeException)ex).getStatusCode());
                } else {
                    requestContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }

                customExceptionHandling(ex, requestContext, routingResult);

            } catch(Throwable ex2) {

                //==========================================
                // Default exception handling.
                //==========================================
                try {
                    defaultExceptionHandling(exchange, ex2);
                } catch(Throwable ex3) {

                    //==========================================
                    // Last resort exception handling!
                    //==========================================
                    lastResortExceptionHandling(ex, ex3);
                }
            }
        } finally {
            try {
                getSpincastRequestScope().exit();
            } catch(Throwable ex) {
                try {
                    this.logger.error("Error while exiting custom Guice scope : " + SpincastStatics.getStackTrace(ex));
                } catch(Throwable ex2) {
                    // too bad
                }
            }
        }
    }

    /**
     * Prepares a direct Not Found routing.
     */
    protected RoutingResult<R> prepareNotFoundRouting(Object exchange, R requestContext) {
        return prepareNotFoundRouting(exchange, requestContext, false);
    }

    /**
     * Prepares a direct Not Found routing.
     */
    protected RoutingResult<R> prepareNotFoundRouting(Object exchange, R requestContext, boolean alreadyTried) {

        //==========================================
        // Get the Not Found handler
        //==========================================
        RoutingResult<R> routingResult = getRouter().route(requestContext, RoutingType.NOT_FOUND);

        //==========================================
        // If no route matches for Not Found, we
        // create a default one!
        //==========================================
        if(routingResult == null) {

            if(alreadyTried) {
                throw new RuntimeException("The method prepareNotFoundRouting was already tried, we called addDefaultNotFoundRoute() " +
                                           "but there's still no Not Found route!! Full url: " +
                                           getServer().getFullUrlOriginal(exchange));
            }

            addDefaultNotFoundRoute();
            return prepareNotFoundRouting(exchange, requestContext, true);
        }

        //==========================================
        // Add a variable in the request context to
        // say this is a Not Found route.
        //==========================================
        requestContext.variables().add(SpincastConstants.RequestScopedVariables.IS_NOT_FOUND_ROUTE, true);

        //==========================================
        // Add a default 404 HTTP status for a Not Found route
        //==========================================
        requestContext.response().setStatusCode(HttpStatus.SC_NOT_FOUND);

        return routingResult;
    }

    /**
     * Add the default Not Found route.
     */
    protected void addDefaultNotFoundRoute() {
        getRouter().ALL(Router.DEFAULT_ROUTE_PATH)
                   .notFound()
                   .save(getDefaultNotFoundHandler());
    }

    /**
     * Create the default Not Found handler
     */
    protected Handler<R> getDefaultNotFoundHandler() {

        Handler<R> handler = new Handler<R>() {

            @Override
            public void handle(R context) {

                String message = getDefaultNotFoundHandlerNotFoundMessage();

                //==========================================
                // Do we have a custom message to display?
                //==========================================
                String customMessage = context.variables()
                                              .getAsString(SpincastConstants.RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);
                if(!StringUtils.isBlank(customMessage)) {
                    message = customMessage;
                }

                context.response().setStatusCode(HttpStatus.SC_NOT_FOUND);

                if(context.request().isJsonShouldBeReturn()) {
                    context.response().sendJson(getNotFoundJsonContent(message));

                } else if(context.request().isXMLShouldBeReturn()) {
                    context.response().sendXml(getNotFoundXmlContent(message));

                } else if(context.request().isHTMLShouldBeReturn()) {
                    String html = getNotFoundHtmlContent(message);
                    context.response().sendHtml(html);

                } else {
                    if(!context.request().isPlainTextShouldBeReturn()) {
                        SpincastFrontController.this.logger.error("Format not managed here!: " +
                                                                  context.request().getContentTypeBestMatch());
                    }
                    context.response().sendPlainText(getNotFoundPlainTextContent(message));
                }
            }
        };
        return handler;
    }

    protected String getNotFoundHtmlContent(String message) {
        return "<pre>" + message + "</pre>";
    }

    protected String getNotFoundJsonContent(String message) {
        JsonObject jsonObj = getJsonManager().create();
        jsonObj.put("error", message);
        return jsonObj.toJsonString();
    }

    protected String getNotFoundXmlContent(String message) {
        JsonObject jsonObj = getJsonManager().create();
        jsonObj.put("error", message);
        return getXmlManager().toXml(jsonObj);
    }

    protected String getNotFoundPlainTextContent(String message) {
        return message;
    }

    /**
     * The message to send for the default Not Found handler.
     */
    protected String getDefaultNotFoundHandlerNotFoundMessage() {
        return getSpincastDictionary().route_notFound_default_message();
    }

    protected void resetResponse(R requestContext) {
        if(requestContext != null && !requestContext.response().isHeadersSent()) {
            requestContext.response().resetEverything();
        }
    }

    /**
     * Custom exception handling.
     */
    protected void customExceptionHandling(Throwable ex, R requestContext,
                                           RoutingResult<R> originalRoutingResult) throws Throwable {

        try {
            //==========================================
            // Do we have a custom exception handler?
            //==========================================
            RoutingResult<R> exceptionRoutingResult = getRouter().route(requestContext, RoutingType.EXCEPTION);
            if(exceptionRoutingResult != null) {

                requestContext.variables().add(SpincastConstants.RequestScopedVariables.ROUTING_RESULT,
                                               exceptionRoutingResult);

                //==========================================
                // Add the original route info to the requestContext.
                //==========================================
                requestContext.variables().add(SpincastConstants.RequestScopedVariables.ORIGINAL_ROUTING_RESULT,
                                               originalRoutingResult);

                //==========================================
                // Call the exception handlers
                //==========================================
                callRouteHandlers(requestContext, exceptionRoutingResult);

                //==========================================
                // Ok, exception managed! We return...
                //==========================================
                return;
            }
        } catch(Throwable ex2) {

            try {
                this.logger.error("An exception occured while using the custom exception handler. The original " +
                                  "exception will be thrown again so it can be managed by the default exception " +
                                  "handler. The exception which occured is : " +
                                  SpincastStatics.getStackTrace(ex2));
            } catch(Throwable ex3) {
                // too bad
            }
        }

        //==========================================
        // Exception not managed here, rethrows it so the
        // default exception handler can manage it.
        //==========================================
        throw ex;
    }

    /**
     * Add dependencies to the custom request scope
     */
    protected void addDependenciesInCustomRequestScope(R requestContext) {

        //==========================================
        // Add the request context object in the cutom request scope
        //==========================================
        addRequestContextInCustomRequestScope(requestContext);
    }

    /**
     * Add the request context objects in the cutom request scope
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addRequestContextInCustomRequestScope(R requestContext) {

        Key key = Key.get(new TypeLiteral<RequestContext<?>>() {});
        getSpincastRequestScope().seed(key, requestContext);

        key = Key.get(getRequestContextType());
        getSpincastRequestScope().seed(key, requestContext);
    }

    /**
     * Call the handlers, in order they are specified.
     */
    protected void callRouteHandlers(R requestContext, RoutingResult<R> routingResult) throws Exception {

        //==========================================
        // Saves the current routing result
        //==========================================
        requestContext.variables().add(SpincastConstants.RequestScopedVariables.ROUTING_RESULT,
                                       routingResult);

        //==========================================
        // Calls the "handle()" method on each handlers.
        //==========================================
        for(RouteHandlerMatch<R> routeHandlerMatch : routingResult.getRouteHandlerMatches()) {

            requestContext.variables().add(SpincastConstants.RequestScopedVariables.ROUTE_HANDLER_MATCH,
                                           routeHandlerMatch);

            Handler<R> handlerMethod = routeHandlerMatch.getHandler();

            //==========================================
            // A route handle may throw a ForwardRouteException to
            // indicate the route must be forwarded.
            //==========================================
            try {
                handlerMethod.handle(requestContext);
            } catch(ForwardRouteException ex) {
                manageForwardRouteException(ex, requestContext, routingResult);
                return;
            } catch(RedirectException ex) {
                manageRedirectException(ex, requestContext, routingResult);
                break;
            } catch(SkipRemainingHandlersException ex) {
                // We run nothing more!
                break;
            }
        }

        //==========================================
        // Ends the response if required.
        //==========================================
        if(!requestContext.response().isClosed()) {
            requestContext.response().end();
        }
    }

    /**
     * Manage a RedirectException exception.
     */
    protected void manageRedirectException(RedirectException ex,
                                           R context,
                                           RoutingResult<R> routingResult) {

        if(context.response().isHeadersSent()) {
            this.logger.error("Headers already sent, we can't sent redirection headers!");
        } else {

            //==========================================
            // We do not reset cookies here
            //==========================================
            context.response().resetEverything(false);

            if(ex.getFlashMessage() != null) {
                context.response().redirect(ex.getNewUrl(),
                                            ex.isRedirectPermanently(),
                                            ex.getFlashMessage());
            } else if(ex.getFlashMessageType() != null && ex.getFlashMessageText() != null) {
                context.response().redirect(ex.getNewUrl(),
                                            ex.isRedirectPermanently(),
                                            ex.getFlashMessageType(),
                                            ex.getFlashMessageText(),
                                            ex.getFlashMessageVariables());
            } else {
                context.response().redirect(ex.getNewUrl(),
                                            ex.isRedirectPermanently());
            }
        }

        context.response().flush(true);
    }

    /**
     * Manage a ForwardRouteException exception.
     */
    protected void manageForwardRouteException(ForwardRouteException ex,
                                               R context,
                                               RoutingResult<R> originalRoutingResult) throws Exception {

        //==========================================
        // Do we have reached the number of time a request
        // can be forwarded?
        //==========================================
        Integer nbrTimeForwarded =
                context.variables().get(SpincastConstants.RequestScopedVariables.ROUTE_FORWARDED_NBR, Integer.class);
        if(nbrTimeForwarded == null) {
            nbrTimeForwarded = 1;
        } else {
            nbrTimeForwarded++;
        }

        if(nbrTimeForwarded > getSpincastConfig().getRouteForwardingMaxNumber()) {
            throw new RuntimeException("The maximum number of request forwarding has been reached : " +
                                       getSpincastConfig().getRouteForwardingMaxNumber() + "." +
                                       "This route won't be called : " + ex.getNewRoute());
        }
        context.variables().add(SpincastConstants.RequestScopedVariables.ROUTE_FORWARDED_NBR, nbrTimeForwarded);

        //==========================================
        // Should we reset the response?
        //==========================================
        if(ex.isResetResponse()) {
            if(!context.response().isHeadersSent()) {
                context.response().resetEverything();
            } else {
                this.logger.warn("The response headers have already been sent, we can't reset the response...");
            }
        }

        //==========================================
        // We create the "forwarded" version of the request
        // context
        //==========================================
        context = createForwardedRequestContext(context, ex.getNewRoute());

        RoutingResult<R> routingResult = getRouter().route(context);
        if(routingResult == null) {
            this.logger.warn("A route forwarding was asked but the requested route doesn't have any match : " + ex.getNewRoute());
            throw new NotFoundException(false);
        }

        //==========================================
        // We also keep the exception message so it can be displayed.
        //==========================================
        context.variables().add(SpincastConstants.RequestScopedVariables.FORWARD_ROUTE_EXCEPTION_MESSAGE,
                                ex.getMessage());

        callRouteHandlers(context, routingResult);
    }

    protected R createForwardedRequestContext(R context,
                                              String fullUrlOrRelativePathAndQueryString) {
        try {
            if(fullUrlOrRelativePathAndQueryString == null) {
                fullUrlOrRelativePathAndQueryString = "/";
            } else {
                fullUrlOrRelativePathAndQueryString = fullUrlOrRelativePathAndQueryString.trim();
                if(fullUrlOrRelativePathAndQueryString.startsWith("//")) {
                    fullUrlOrRelativePathAndQueryString = fullUrlOrRelativePathAndQueryString.substring(1);
                }
            }

            String newFullUrl = fullUrlOrRelativePathAndQueryString;
            URI uri = new URI(fullUrlOrRelativePathAndQueryString);
            if(!uri.isAbsolute()) {
                if(!fullUrlOrRelativePathAndQueryString.startsWith("/")) {
                    fullUrlOrRelativePathAndQueryString = "/" + fullUrlOrRelativePathAndQueryString;
                }

                String oldFullUrlStr = context.request().getFullUrl();
                URL oldFullUrl = new URL(oldFullUrlStr);

                newFullUrl = oldFullUrl.getProtocol() + "://" + oldFullUrl.getHost() + ":" + oldFullUrl.getPort() +
                             fullUrlOrRelativePathAndQueryString;
            }

            context.variables().add(SpincastConstants.RequestScopedVariables.FORWARD_ROUTE_URL,
                                    newFullUrl);

            return context;

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Validate/modify the exchange before the handling.
     */
    protected Object validateExchange(Object exchange) {
        Objects.requireNonNull(exchange, "The exchange object can't be NULL.");
        return exchange;
    }

    /**
     * Find the route handlers to call.
     * 
     */
    protected RoutingResult<R> findRouteMatch(R requestContext) {
        return getRouter().route(requestContext);
    }

    /**
     * Default exception handling.
     */
    protected void defaultExceptionHandling(Object exchange, Throwable ex) throws Throwable {
        if(ex instanceof PublicException) {
            defaultPublicExceptionHandling(exchange, (PublicException)ex);
        } else {
            defaultPrivateExceptionHandling(exchange, ex);
        }
    }

    /**
     * Default exception handling for exceptions with 
     * public information.
     */
    protected void defaultPublicExceptionHandling(Object exchange, PublicException publicException) throws Throwable {

        if(getServer().isResponseHeadersSent(exchange)) {
            this.logger.info("Can't sent proper public error response, headers are already sent :\n" +
                             SpincastStatics.getStackTrace((Throwable)publicException));
            return;
        }

        String errorMessage = ((Throwable)publicException).getMessage();

        //==========================================
        // More info when debug is enabled.
        //==========================================
        if(getSpincastConfig().isDebugEnabled()) {
            errorMessage += "\n\nDebug info :\n\n" + SpincastStatics.getStackTrace(((Throwable)publicException));
        }

        sendErrorUsingBestMatchContentType(exchange, errorMessage, publicException.getStatusCode());
    }

    /**
     * Default exception handling for private exceptions
     */
    protected void defaultPrivateExceptionHandling(Object exchange, Throwable exception) throws Throwable {

        //==========================================
        // We log the exception
        //==========================================
        this.logger.error(SpincastStatics.getStackTrace(exception));

        if(getServer().isResponseHeadersSent(exchange)) {
            this.logger.info("Can't sent proper error response, headers are already sent :\n" +
                             SpincastStatics.getStackTrace(exception));
            return;
        }

        String errorMessage = getSpincastDictionary().exception_default_message();

        //==========================================
        // More info when debug mode is enabled.
        //==========================================
        if(getSpincastConfig().isDebugEnabled()) {
            errorMessage += "\n\nDebug info :\n\n" + SpincastStatics.getStackTrace(exception);
        }

        int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        if(exception instanceof CustomStatusCodeException) {
            statusCode = ((CustomStatusCodeException)exception).getStatusCode();
        }

        sendErrorUsingBestMatchContentType(exchange, errorMessage, statusCode);
    }

    /**
     * Send an error to the client.
     */
    protected void sendErrorUsingBestMatchContentType(Object exchange,
                                                      String errorMessage,
                                                      Integer statusCode) throws Throwable {

        if(statusCode == null) {
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        ContentTypeDefaults type = getResponseContentTypeToUse(exchange);

        if(type == ContentTypeDefaults.JSON) {
            errorMessage = getInternalErrorJsonContent(errorMessage);
            getServer().setResponseHeader(exchange,
                                          HttpHeaders.CONTENT_TYPE,
                                          Arrays.asList(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset()));

        } else if(type == ContentTypeDefaults.XML) {
            errorMessage = getInternalErrorXmlContent(errorMessage);
            getServer().setResponseHeader(exchange,
                                          HttpHeaders.CONTENT_TYPE,
                                          Arrays.asList(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset()));

        } else if(type == ContentTypeDefaults.HTML) {
            errorMessage = getInternalErrorHtmlContent(errorMessage);
            getServer().setResponseHeader(exchange,
                                          HttpHeaders.CONTENT_TYPE,
                                          Arrays.asList(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset()));

        } else if(type == ContentTypeDefaults.TEXT) {
            errorMessage = getInternalErrorTextContent(errorMessage);
            getServer().setResponseHeader(exchange,
                                          HttpHeaders.CONTENT_TYPE,
                                          Arrays.asList(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()));

        } else {
            throw new RuntimeException("Not implemented : " + type);
        }

        byte[] errorMessageBytes = errorMessage.getBytes(getDefaultExceptionHandlingCharset());

        getServer().setResponseStatusCode(exchange, statusCode);
        getServer().setResponseHeader(exchange,
                                      HttpHeaders.CONTENT_LENGTH,
                                      Arrays.asList("" + errorMessageBytes.length));
        getServer().flushBytes(exchange, errorMessageBytes, true);
    }

    /**
     * Charset used by the default exception handling.
     */
    protected String getDefaultExceptionHandlingCharset() {
        return "UTF-8";
    }

    protected String getInternalErrorJsonContent(String errorMessage) {

        JsonObject jsonObject = getJsonManager().create();
        jsonObject.put("error", errorMessage);

        return jsonObject.toJsonString();
    }

    protected String getInternalErrorXmlContent(String errorMessage) {

        JsonObject jsonObject = getJsonManager().create();
        jsonObject.put("error", errorMessage);

        return getXmlManager().toXml(jsonObject);
    }

    protected String getInternalErrorHtmlContent(String errorMessage) {
        return "<pre>" + errorMessage + "</pre>";
    }

    protected String getInternalErrorTextContent(String errorMessage) {
        return errorMessage;
    }

    protected ContentTypeDefaults getResponseContentTypeToUse(Object exchange) {
        try {
            return getServer().getContentTypeBestMatch(exchange);
        } catch(Exception ex) {
            return ContentTypeDefaults.TEXT;
        }
    }

    /**
     * Called when the default exception handler itself throws
     * an exception... Last resort!
     */
    protected void lastResortExceptionHandling(Throwable originalException,
                                               Throwable defaultHandlingException) {

        try {
            this.logger.error("Error while trying to use the default exception " + "handling : " +
                              defaultHandlingException.getMessage() +
                              ".\n" + "The original exception was : " +
                              SpincastStatics.getStackTrace(originalException));
        } catch(Throwable ex3) {
            // too bad
        }

        //==========================================
        // We simply just make sure no internal exception's
        // stack trace is ever displayed publicly by the
        // HTTP server.
        //==========================================
        throw new RuntimeException("An error occured.");
    }

}

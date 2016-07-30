package org.spincast.core.config;

/**
 * Spincast constants.
 */
public abstract class SpincastConstants {

    /**
     * Name of the Cookie to save the user <code>Locale</code>.
     */
    public static final String COOKIE_NAME_LOCALE_TO_USE = "locale";

    /**
     * Request scoped variables that Spincast may set.
     * 
     */
    public static final class RequestScopedVariables {

        /**
         * The current routing result.
         * 
         * The associated value is of type: IRoutingResult&lt;R&gt;.
         */
        public static final String ROUTING_RESULT = RequestScopedVariables.class.getName() + "ROUTING_RESULT";

        /**
         * The current route handler match.
         * 
         * The associated value is of type: IRouteHandlerMatch&lt;R&gt;.
         */
        public static final String ROUTE_HANDLER_MATCH = RequestScopedVariables.class.getName() + "ROUTE_HANDLER_MATCH";

        /**
         * If a "Not Found" route is run, this variable will be set
         * and its value will be <code>true</code>.
         * 
         * The associated value is of type: boolean.
         */
        public static final String IS_NOT_FOUND_ROUTE = RequestScopedVariables.class.getName() + "IS_NOT_FOUND_ROUTE";

        /**
         * If a "Not Found" route is run because of a <code>NotFoundException</code> 
         * has being thrown, the exception message, if any, is <i>public</i> and will be added 
         * using this variable.
         * 
         * The associated value is of type: String.
         */
        public static final String NOT_FOUND_PUBLIC_MESSAGE =
                RequestScopedVariables.class.getName() + "NOT_FOUND_PUBLIC_MESSAGE";

        /**
         * If an "Exception" route is run, this variable will be set to <code>true</code>.
         * 
         * The associated value is of type: boolean.
         */
        public static final String IS_EXCEPTION_HANDLING = RequestScopedVariables.class.getName() + "IS_EXCEPTION_HANDLING";

        /**
         * If an "Exception" route is run, this variable
         * will contain the exception that occured.
         * 
         * The associated value is of type: Throwable.
         */
        public static final String EXCEPTION = RequestScopedVariables.class.getName() + "EXCEPTION";

        /**
         * This is going to be set to the original routing result, if an "Exception"
         * route or a "Not Found" routing process is started. That way, the route handlers
         * may know what the original route was.
         * 
         * The associated value is of type: IRoutingResult&lt;R&gt;.
         */
        public static final String ORIGINAL_ROUTING_RESULT = RequestScopedVariables.class.getName() + "ORIGINAL_ROUTING_RESULT";

        /**
         * When a route is forwarded, this is the new URL.
         * 
         * The associated value is of type: String.
         */
        public static final String FORWARD_ROUTE_URL = RequestScopedVariables.class.getName() + "FORWARD_ROUTE_URL";

        /**
         * If a request is forwaded, this is going to be the forwarding message (taken from the
         * <code>ForwardRouteException</code> exception)
         * 
         * The associated value is of type: String.
         */
        public static final String FORWARD_ROUTE_EXCEPTION_MESSAGE =
                RequestScopedVariables.class.getName() + "FORWARD_ROUTE_EXCEPTION_MESSAGE";

        /**
         * The number of time a request has been forwarded.
         * 
         * The associated value is of type: int.
         */
        public static final String ROUTE_FORWARDED_NBR = RequestScopedVariables.class.getName() + "FORWARDED_NBR";
    }

    /**
     * Do not change that, it is an official value.
     */
    public static final String WEBSOCKET_V13_MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    /**
     * Some HTTP headers that are not defined in Guava's
     * com.google.common.net.HttpHeaders
     */
    public static final class HttpHeadersExtra {

        private HttpHeadersExtra() {
        }

        public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
        public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
        public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
        public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

        public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
        public static final String X_FORWARDED_PORT = "X-Forwarded-Port";

    }

}

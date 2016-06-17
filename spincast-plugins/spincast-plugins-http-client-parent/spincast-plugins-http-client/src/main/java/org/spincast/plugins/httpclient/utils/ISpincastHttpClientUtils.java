package org.spincast.plugins.httpclient.utils;

import org.spincast.shaded.org.apache.http.cookie.Cookie;

public interface ISpincastHttpClientUtils {

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

    }

    /**
     * Generates the expected value of a "Sec-WebSocket-Accept"
     * header in the response for a Websocket upgrade request.
     * 
     * @param secWebSocketKey The value of the "Sec-WebSocket-Key" header
     * sent in the request.
     */
    public String generateExpectedWebsocketV13AcceptHeaderValue(String secWebSocketKey);

    public String apacheCookieToHttpHeaderValue(Cookie cookie);

}

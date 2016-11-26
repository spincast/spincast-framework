package org.spincast.plugins.httpclient.utils;

import org.spincast.shaded.org.apache.http.cookie.Cookie;

public interface SpincastHttpClientUtils {

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

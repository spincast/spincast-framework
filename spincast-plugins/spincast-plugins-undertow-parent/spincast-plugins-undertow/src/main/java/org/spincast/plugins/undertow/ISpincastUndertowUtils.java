package org.spincast.plugins.undertow;

import java.util.Map;

import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;

/**
 * Undertow utilities.
 */
public interface ISpincastUndertowUtils {

    /**
     * Get the custom variables Map associated with the
     * specified exchange object.
     */
    public Map<String, String> getRequestCustomVariables(HttpServerExchange exchange);

    /**
     * Get the custom variables Map associated with the
     * specified exchange object (when wrpped inside a WebSocketHttpExchange).
     */
    public Map<String, String> getRequestCustomVariables(WebSocketHttpExchange exchange);
}

package org.spincast.plugins.undertow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class SpincastUndertowUtils implements ISpincastUndertowUtils {

    @Override
    public Map<String, String> getRequestCustomVariables(HttpServerExchange exchange) {

        Objects.requireNonNull(exchange, "The exchange can't be NULL");

        Map<String, String> variables = exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
        if(variables == null) {
            variables = new HashMap<String, String>();
            exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, variables);
        }
        return variables;
    }

    @Override
    public Map<String, String> getRequestCustomVariables(WebSocketHttpExchange exchange) {
        Objects.requireNonNull(exchange, "The exchange can't be NULL");

        Map<String, String> variables = exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
        if(variables == null) {
            variables = new HashMap<String, String>();
            exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, variables);
        }
        return variables;
    }

}

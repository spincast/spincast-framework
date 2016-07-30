package org.spincast.plugins.undertow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.Inject;

import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class SpincastUndertowUtils implements ISpincastUndertowUtils {

    private final ISpincastUtils spincastUtils;

    @Inject
    public SpincastUndertowUtils(ISpincastUtils spincastUtils) {
        this.spincastUtils = spincastUtils;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

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

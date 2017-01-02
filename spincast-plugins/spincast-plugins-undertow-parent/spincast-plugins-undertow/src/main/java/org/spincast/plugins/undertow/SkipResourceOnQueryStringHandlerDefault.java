package org.spincast.plugins.undertow;

import java.util.Deque;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class SkipResourceOnQueryStringHandlerDefault implements SkipResourceOnQueryStringHandler {

    protected final Logger logger = LoggerFactory.getLogger(SkipResourceOnQueryStringHandlerDefault.class);

    private final HttpHandler runHandler;
    private final HttpHandler skipHandler;

    @AssistedInject
    public SkipResourceOnQueryStringHandlerDefault(@Assisted("runHandler") HttpHandler runHandler,
                                                   @Assisted("skipHandler") HttpHandler skipHandler) {
        this.runHandler = runHandler;
        this.skipHandler = skipHandler;
    }

    protected HttpHandler getRunHandler() {
        return this.runHandler;
    }

    protected HttpHandler getSkipHandler() {
        return this.skipHandler;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        Map<String, Deque<String>> queryParameters = ((HttpServerExchange)exchange).getQueryParameters();
        if(queryParameters != null && queryParameters.size() > 0) {
            getSkipHandler().handleRequest(exchange);
        } else {
            getRunHandler().handleRequest(exchange);
        }
    }
}

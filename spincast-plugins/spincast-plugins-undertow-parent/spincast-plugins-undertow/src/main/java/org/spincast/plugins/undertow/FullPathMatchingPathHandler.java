package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;

/**
 * Custom PathHandler that always match on the full path, not the
 * remaining path, if previous PathHandler matched.
 * <p>
 * Warning: Those FullPathMatchingPathHandlers shouldn't be use in association
 * with regular PathHandlers since they override the "relative path" 
 * of the exchange, that is used by regular PathHandlers. You should only use
 * FullPathMatchingPathHandlers or only regular PathHandlers.
 * </p>
 * 
 * @see http://lists.jboss.org/pipermail/undertow-dev/2016-July/001649.html
 */
public class FullPathMatchingPathHandler extends PathHandler {

    public FullPathMatchingPathHandler(HttpHandler defaultHandler) {
        super(defaultHandler);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        // The workaround :
        exchange.setRelativePath(exchange.getRequestPath());

        super.handleRequest(exchange);
    }
}

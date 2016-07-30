package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Handler to remove cache busters from the request path.
 */
public interface ICacheBusterRemovalHandler extends HttpHandler {

    /**
     * Get the original URL of the request potentially containing cache
     * busters. The URL is not decoded.
     */
    public String getOrigninalRequestUrlWithPotentialCacheBusters(HttpServerExchange exchange);
}

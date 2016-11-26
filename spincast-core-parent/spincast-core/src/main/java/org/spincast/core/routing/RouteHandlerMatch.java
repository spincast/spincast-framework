package org.spincast.core.routing;

import java.util.Map;

import org.spincast.core.exchange.RequestContext;

/**
 * Represents a match found by the router.
 */
public interface RouteHandlerMatch<R extends RequestContext<?>> {

    /**
     * The route associated with this match.
     */
    public Route<R> getSourceRoute();

    /**
     * The route handler.
     */
    public Handler<R> getHandler();

    /**
     * The values parsed from the URL, given
     * the dynamic parameters of the route's path, if any.
     */
    public Map<String, String> getPathParams();

    /**
     * The position the handler should be run at.
     */
    public int getPosition();

}

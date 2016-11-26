package org.spincast.core.routing;

import java.util.List;

import org.spincast.core.exchange.RequestContext;

/**
 * The result of the router, when asked to find matches for 
 * a request.
 */
public interface RoutingResult<R extends RequestContext<?>> {

    /**
     * The handlers matching the route (a main handler + filters, if any), 
     * in order they have to be called.
     */
    public List<RouteHandlerMatch<R>> getRouteHandlerMatches();

    /**
     * The main route handler and its information, from the routing result.
     */
    public RouteHandlerMatch<R> getMainRouteHandlerMatch();

}

package org.spincast.core.routing;

import java.util.Map;

import org.spincast.core.exchange.IRequestContext;

/**
 * Represents a match found by the router.
 */
public interface IRouteHandlerMatch<R extends IRequestContext<?>> {

    /**
     * The route associated with this match.
     */
    public IRoute<R> getSourceRoute();

    /**
     * The route handler.
     */
    public IHandler<R> getHandler();

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

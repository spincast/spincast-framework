package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;

/**
 * Add-on to get information about
 * the current routing process.
 */
public interface IRoutingRequestContextAddon<R extends IRequestContext<?>> {

    /**
     * Are we currently on a "Not Found" routing type?
     */
    public boolean isNotFoundRoute();

    /**
     * Are we currently on an "Exception" routing type?
     */
    public boolean isExceptionRoute();

    /**
     * Is the current route forwarded?
     */
    public boolean isForwarded();

    /**
     * The current route handler being run (may be a filter) and its
     * associated information.
     */
    public IRouteHandlerMatch<R> getCurrentRouteHandlerMatch();

    /**
     * The routing result for the current request, 
     * as returned by the router.
     */
    public IRoutingResult<R> getRoutingResult();

    /**
     * The current route handler position. 
     * If &lt; 0 : is a "before" handler.
     * If == 0 : is the main handler.
     * If &gt; 0 : is an "after" handler.
     */
    public int getPosition();

}

package org.spincast.core.routing;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;

/**
 * Represents a regular route and all its informations.
 */
public interface Route<R extends RequestContext<?>> {

    /**
     * The route id.
     */
    public String getId();

    /**
     * The route path.
     */
    public String getPath();

    /**
     * The <code>HTTP methods</code> this route applies to.
     */
    public Set<HttpMethod> getHttpMethods();

    /**
     * The <code>Content-Types</code> this route
     * accepts (names all lowercased).
     */
    public Set<String> getAcceptedContentTypes();

    /**
     * The <code>routing types</code> the route should be considered for.
     */
    public Set<RoutingType> getRoutingTypes();

    /**
     * The main handler.
     */
    public Handler<R> getMainHandler();

    /**
     * The "before" filters, if any.
     */
    public List<Handler<R>> getBeforeFilters();

    /**
     * The "after" filters, if any.
     */
    public List<Handler<R>> getAfterFilters();

    /**
     * The position at which this route should be run during a
     * routing process.
     */
    public int getPosition();

    /**
     * The ids of the filters that should be skipped for this
     * route.
     */
    public Set<String> getFilterIdsToSkip();

}

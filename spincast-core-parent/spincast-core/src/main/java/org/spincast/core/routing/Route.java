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
     * The route's classes.
     */
    public Set<String> getClasses();

    /**
     * Is this a Websocket route?
     */
    public boolean isWebsocketRoute();

    /**
     * Is this a route for a resource? It is
     * if it was was started using
     * {@link Router#dir(String)} or {@link Router#file(String)}.
     * <p>
     * On a resource route, we may want to skip some filters,
     * for example.
     */
    public boolean isStaticResourceRoute();

    /**
     * Returns the {@link StaticResource } to serve or
     * <code>null</code> if this is not a
     * route for a static resource (ie if
     * {@link #isStaticResourceRoute()}) doesn't return
     * <code>true</code>.
     */
    public StaticResource<R> getStaticResource();

    /**
     * Is this a route added by Spincast itself
     * or by a plugin? Otherwise, the route is
     * considered as an application route.
     */
    public boolean isSpicastCoreRouteOrPluginRoute();

    /**
     * The route path.
     */
    public String getPath();

    /**
     * Should a request for a resource be skipped?
     * <p>
     * Only used if the current route is a filter.
     */
    public boolean isSkipResourcesRequests();

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

    /**
     * The optional specs for the route.
     */
    public Object getSpecs();

    /**
     * Optional specs parameters.
     */
    public List<Object> getSpecsParameters();

    /**
     * Is this route ignored when specs are
     * generated?
     */
    public boolean isSpecsIgnore();

}

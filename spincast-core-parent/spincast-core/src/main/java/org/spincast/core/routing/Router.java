package org.spincast.core.routing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketRoute;
import org.spincast.core.websocket.WebsocketRouteBuilder;

/**
 * The router.
 */
public interface Router<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * The default path used when Spincast creates routes by
     * itself.
     */
    public static final String DEFAULT_ROUTE_PATH = "/*{path}";

    /**
     * Starts the creation of a <code>GET</code> route,
     * for all path. Same as <code>GET("/*{path}")</code>.
     */
    public RouteBuilder<R> GET();

    /**
     * Starts the creation of a <code>GET</code> route.
     */
    public RouteBuilder<R> GET(String path);

    /**
     * Starts the creation of a <code>POST</code> route,
     * for all path. Same as <code>POST("/*{path}")</code>.
     */
    public RouteBuilder<R> POST();

    /**
     * Starts the creation of a <code>POST</code> route.
     */
    public RouteBuilder<R> POST(String path);

    /**
     * Starts the creation of a <code>PUT</code> route,
     * for all path. Same as <code>PUT("/*{path}")</code>.
     */
    public RouteBuilder<R> PUT();

    /**
     * Starts the creation of a <code>PUT</code> route.
     */
    public RouteBuilder<R> PUT(String path);

    /**
     * Starts the creation of a <code>DELETE</code> route,
     * for all path. Same as <code>DELETE("/*{path}")</code>.
     */
    public RouteBuilder<R> DELETE();

    /**
     * Starts the creation of a <code>DELETE</code> route.
     */
    public RouteBuilder<R> DELETE(String path);

    /**
     * Starts the creation of a <code>OPTIONS</code> route,
     * for all path. Same as <code>OPTIONS("/*{path}")</code>.
     */
    public RouteBuilder<R> OPTIONS();

    /**
     * Starts the creation of a <code>OPTIONS</code> route.
     */
    public RouteBuilder<R> OPTIONS(String path);

    /**
     * Starts the creation of a <code>TRACE</code> route,
     * for all path. Same as <code>TRACE("/*{path}")</code>.
     */
    public RouteBuilder<R> TRACE();

    /**
     * Starts the creation of a <code>TRACE</code> route,
     * at the specified position.
     */
    public RouteBuilder<R> TRACE(String path);

    /**
     * Starts the creation of a <code>HEAD</code> route,
     * for all path. Same as <code>HEAD("/*{path}")</code>.
     */
    public RouteBuilder<R> HEAD();

    /**
     * Starts the creation of a <code>HEAD</code> route.
     */
    public RouteBuilder<R> HEAD(String path);

    /**
     * Starts the creation of a <code>PATCH</code> route,
     * for all path. Same as <code>PATCH("/*{path}")</code>.
     */
    public RouteBuilder<R> PATCH();

    /**
     * Starts the creation of a <code>PATCH</code> route.
     */
    public RouteBuilder<R> PATCH(String path);

    /**
     * Starts the creation of a route matching any HTTP method,
     * and on any path. Same as <code>ALL("/*{path}")</code>.
     */
    public RouteBuilder<R> ALL();

    /**
     * Starts the creation of a route matching any HTTP method.
     */
    public RouteBuilder<R> ALL(String path);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods and on any path. Same as 
     * <code>SOME("/*{path}", httpMethods)</code>.
     */
    public RouteBuilder<R> SOME(Set<HttpMethod> httpMethods);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods.
     */
    public RouteBuilder<R> SOME(String path, Set<HttpMethod> httpMethods);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods and on any path. Same as 
     * <code>SOME("/*{path}", httpMethods)</code>.
     */
    public RouteBuilder<R> SOME(HttpMethod... httpMethods);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods.
     */
    public RouteBuilder<R> SOME(String path, HttpMethod... httpMethods);

    /**
     * Creates a route considered during an "Exception" routing process.
     * <p>
     * Synonym of : 
     * <p>
     * <code>ALL("/*{path}").exception().save(handler)</code>
     */
    public void exception(Handler<R> handler);

    /**
     * Creates a route considered during an "Exception" routing process.
     * <p>
     * Synonym of : 
     * <p>
     * <code>ALL(path).exception().save(handler)</code>
     */
    public void exception(String path, Handler<R> handler);

    /**
     * Creates a route considered during an "Not Found" routing process.
     * <p>
     * Synonym of : 
     * <p>
     * <code>ALL("/*{path}").notFound().save(handler)</code>
     */
    public void notFound(Handler<R> handler);

    /**
     * Creates a route considered during an "Not Found" routing process.
     * <p>
     * Synonym of : 
     * <p>
     * <code>ALL(path).notFound().save(handler)</code>
     */
    public void notFound(String path, Handler<R> handler);

    /**
     * Start the creation of a <code>static resource</code> file.
     * <p>
     * Only a <code>GET</code> or a <code>HEAD</code> request will be able
     * to access this resource.
     * </p>
     * <p>
     * No "before" and "after" filters will be applied to those, since the request
     * won't even reach the framework.
     * </p>
     * @param url The url which will trigger the output of this
     *            static resource.
     */
    public StaticResourceBuilder<R> file(String url);

    /**
     * Start the creation of a <code>static resource</code> directory.
     * <p>
     * Only a <code>GET</code> or a <code>HEAD</code> request will be able
     * to access the resources below this directory.
     * </p>
     * <p>
     * No "before" and "after" filters will be applied to those, since the request
     * won't even reach the framework.
     * </p>
     * @param url The url which will trigger the output of this
     *            static resource.
     */
    public StaticResourceBuilder<R> dir(String url);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on all 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R) 
     *                                          SpincastFilters#cors(R context)    
     */
    public void cors();

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)  
     */
    public void cors(Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeReadt)   
     */
    public void cors(Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent)  
     */
    public void cors(Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies) 
     */
    public void cors(Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods) 
     */
    public void cors(Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean, Set, int) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods,
     *                                          int maxAgeInSeconds) 
     */
    public void cors(Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods,
                     int maxAgeInSeconds);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on all 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R) 
     *                                          SpincastFilters#cors(R context)    
     */
    public void cors(String path);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)  
     */
    public void cors(String path,
                     Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead)  
     */
    public void cors(String path,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent)  
     */
    public void cors(String path,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies) 
     */
    public void cors(String path,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods) 
     */
    public void cors(String path,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean, Set, int) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods,
     *                                          int maxAgeInSeconds)  
     */
    public void cors(String path,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods,
                     int maxAgeInSeconds);

    /**
     * Adds a <code>static resource</code> route, directly.
     */
    public void addStaticResource(StaticResource<R> staticResource);

    /**
     * Find the route to use to handle the current request. The result
     * contains all handlers to use.
     * 
     * @return the routing result or <code>null</code> if no route matches.
     */
    public RoutingResult<R> route(R requestContext);

    /**
     * Find the route to use to handle the current request, given the
     * specified routing type. The result contains all handlers to use.
     * 
     * @return the routing result or <code>null</code> if no route matches.
     */
    public RoutingResult<R> route(R requestContext, RoutingType routingType);

    /**
     * Adds a route, directly.
     */
    public void addRoute(Route<R> route);

    /**
     * Removes all routes, except the one Spincast
     * addds automatically (their ids start with "spincast_").
     */
    public void removeAllRoutes();

    /**
     * Removes all routes.
     * 
     * @param removeSpincastRoutesToo Should the routes added by 
     * Spincast be removed too?
     */
    public void removeAllRoutes(boolean removeSpincastRoutesToo);

    /**
     * Removes a route using its <code>routeId</code>.
     */
    public void removeRoute(String routeId);

    /**
     * Gets a route using its <code>routeId</code>.
     */
    public Route<R> getRoute(String routeId);

    /**
     * Gets the global "before" filters.
     */
    public List<Route<R>> getGlobalBeforeFiltersRoutes();

    /**
     * Gets the main routes.
     */
    public List<Route<R>> getMainRoutes();

    /**
     * Gets the global "after" filters.
     */
    public List<Route<R>> getGlobalAfterFiltersRoutes();

    /**
     * Adds an alias for a path pattern. For example,
     * the path of a route may be <code>"/${param1:&lt;XXX&gt;}"</code> : here "XXX" is the alias for the
     * regular expression pattern to use.
     */
    public void addRouteParamPatternAlias(String alias, String pattern);

    /**
     * The path patterns' aliases.
     * Themap is mutable.
     */
    public Map<String, String> getRouteParamPatternAliases();

    /**
     * Creates HTTP authentication protection (realm) for the
     * specified path prefix.
     */
    public void httpAuth(String pathPrefix, String realmName);

    /**
     * Starts the creation of a <code>Websocket route</code>.
     */
    public WebsocketRouteBuilder<R, W> websocket(String path);

    /**
     * Adds a Websocket route, directly.
     */
    public void addWebsocketRoute(WebsocketRoute<R, W> websocketRoute);

    /**
     * Starts the creation of a redirection rule.
     * 
     * @param oldPath The old path that needs to be redirected.
     */
    public RedirectRuleBuilder redirect(String oldPath);

}

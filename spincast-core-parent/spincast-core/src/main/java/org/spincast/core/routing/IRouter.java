package org.spincast.core.routing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketRoute;
import org.spincast.core.websocket.IWebsocketRouteBuilder;

/**
 * The router.
 */
public interface IRouter<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * The default path used when Spincast creates routes by
     * itself.
     */
    public static final String DEFAULT_ROUTE_PATH = "/*{path}";

    /**
     * Starts the creation of a <code>GET</code> route.
     */
    public IRouteBuilder<R> GET(String path);

    /**
     * Starts the creation of a <code>POST</code> route.
     */
    public IRouteBuilder<R> POST(String path);

    /**
     * Starts the creation of a <code>PUT</code> route.
     */
    public IRouteBuilder<R> PUT(String path);

    /**
     * Starts the creation of a <code>DELETE</code> route.
     */
    public IRouteBuilder<R> DELETE(String path);

    /**
     * Starts the creation of a <code>OPTIONS</code> route.
     */
    public IRouteBuilder<R> OPTIONS(String path);

    /**
     * Starts the creation of a <code>TRACE</code> route.
     */
    public IRouteBuilder<R> TRACE(String path);

    /**
     * Starts the creation of a <code>HEAD</code> route.
     */
    public IRouteBuilder<R> HEAD(String path);

    /**
     * Starts the creation of a <code>PATCH</code> route.
     */
    public IRouteBuilder<R> PATCH(String path);

    /**
     * Starts the creation of a route matching any HTTP method.
     */
    public IRouteBuilder<R> ALL(String path);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods.
     */
    public IRouteBuilder<R> SOME(String path, Set<HttpMethod> httpMethods);

    /**
     * Starts the creation of a route matching the specified
     * HTTP methods.
     */
    public IRouteBuilder<R> SOME(String path, HttpMethod... httpMethods);

    /**
     * Creates a "before" filter.
     * 
     * Synonym of : 
     * 
     * <code>ALL("/*{path}").pos(-10)</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> before();

    /**
     * Creates a "before" filter.
     * 
     * Synonym of : 
     * 
     * <code>ALL(path).pos(-10)</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> before(String path);

    /**
     * Creates an "after" filter.
     * 
     * Synonym of : 
     * 
     * <code>ALL("/*{path}").pos(10)</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> after();

    /**
     * Creates an "after" filter.
     * 
     * Synonym of : 
     * 
     * <code>ALL(path).pos(10))</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> after(String path);

    /**
     * Creates a "before" and an "after" filters.
     * 
     * Synonym of : 
     * 
     * <code>ALL("/*{path}").pos(-10)</code>
     * and
     * <code>ALL("/*{path}").pos(10)</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> beforeAndAfter();

    /**
     * Creates a "before" and an "after" filters.
     * 
     * Synonym of : 
     * 
     * <code>ALL(path).pos(-10)</code>
     * and
     * <code>ALL(path).pos(10)</code>
     * and with the Routing types as returned by
     * ISpincastRouterConfig#getFilterDefaultRoutingTypes()
     */
    public IRouteBuilder<R> beforeAndAfter(String path);

    /**
     * Creates a route considered during an "Exception" routing process.
     * 
     * Synonym of : 
     * 
     * <code>ALL("/*{path}").exception().save(handler)</code>
     */
    public void exception(IHandler<R> handler);

    /**
     * Creates a route considered during an "Exception" routing process.
     * 
     * Synonym of : 
     * 
     * <code>ALL(path).exception().save(handler)</code>
     */
    public void exception(String path, IHandler<R> handler);

    /**
     * Creates a route considered during an "Not Found" routing process.
     * 
     * Synonym of : 
     * 
     * <code>ALL("/*{path}").notFound().save(handler)</code>
     */
    public void notFound(IHandler<R> handler);

    /**
     * Creates a route considered during an "Not Found" routing process.
     * 
     * Synonym of : 
     * 
     * <code>ALL(path).notFound().save(handler)</code>
     */
    public void notFound(String path, IHandler<R> handler);

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
    public IStaticResourceBuilder<R> file(String url);

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
    public IStaticResourceBuilder<R> dir(String url);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on all 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R) 
     *                                          ISpincastFilters#cors(R context)    
     */
    public void cors();

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)  
     */
    public void cors(Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * matching requests (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set, int) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R) 
     *                                          ISpincastFilters#cors(R context)    
     */
    public void cors(String path);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)  
     */
    public void cors(String path,
                     Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors) on 
     * requests matching the specified path (except the static resources, 
     * for whom cors has to be activated directly!)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set) 
     *                                          ISpincastFilters#cors(R context, 
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
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set, int) 
     *                                          ISpincastFilters#cors(R context, 
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
    public void addStaticResource(IStaticResource<R> staticResource);

    /**
     * Find the route to use to handle the current request. The result
     * contains all handlers to use.
     * 
     * @return the routing result or <code>null</code> if no route matches.
     */
    public IRoutingResult<R> route(R requestContext);

    /**
     * Find the route to use to handle the current request, given the
     * specified routing type. The result contains all handlers to use.
     * 
     * @return the routing result or <code>null</code> if no route matches.
     */
    public IRoutingResult<R> route(R requestContext, RoutingType routingType);

    /**
     * Adds a route, directly.
     */
    public void addRoute(IRoute<R> route);

    /**
     * Removes all routes.
     */
    public void removeAllRoutes();

    /**
     * Removes a route using its <code>routeId</code>.
     */
    public void removeRoute(String routeId);

    /**
     * Gets a route using its <code>routeId</code>.
     */
    public IRoute<R> getRoute(String routeId);

    /**
     * Gets the global "before" filters.
     */
    public List<IRoute<R>> getGlobalBeforeFiltersRoutes();

    /**
     * Gets the main routes.
     */
    public List<IRoute<R>> getMainRoutes();

    /**
     * Gets the global "after" filters.
     */
    public List<IRoute<R>> getGlobalAfterFiltersRoutes();

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
    public IWebsocketRouteBuilder<R, W> websocket(String path);

    /**
     * Adds a Websocket route, directly.
     */
    public void addWebsocketRoute(IWebsocketRoute<R, W> websocketRoute);

    /**
     * Starts the creation of a redirection rule.
     * 
     * @param oldPath The old path that needs to be redirected.
     */
    public IRedirectRuleBuilder redirect(String oldPath);

}

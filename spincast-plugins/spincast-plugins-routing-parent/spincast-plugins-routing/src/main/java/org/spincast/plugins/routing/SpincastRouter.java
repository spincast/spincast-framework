package org.spincast.plugins.routing;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.RedirectRuleBuilder;
import org.spincast.core.routing.RedirectRuleBuilderFactory;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RouteBuilder;
import org.spincast.core.routing.RouteBuilderFactory;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceBuilder;
import org.spincast.core.routing.StaticResourceBuilderFactory;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.routing.StaticResourceFactory;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketRoute;
import org.spincast.core.websocket.WebsocketRouteBuilder;
import org.spincast.core.websocket.WebsocketRouteBuilderFactory;
import org.spincast.core.websocket.WebsocketRouteHandlerFactory;
import org.spincast.plugins.routing.utils.ReplaceDynamicParamsResult;
import org.spincast.plugins.routing.utils.SpincastRoutingUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

/**
 * Spincast router 
 */
public class SpincastRouter<R extends RequestContext<?>, W extends WebsocketContext<?>> implements Router<R, W> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastRouter.class);

    private final RouteHandlerMatchFactory<R> routeHandlerMatchFactory;
    private final RouteBuilderFactory<R, W> routeBuilderFactory;
    private final RedirectRuleBuilderFactory<R, W> redirectRuleBuilderFactory;
    private final StaticResourceBuilderFactory<R, W> staticResourceBuilderFactory;
    private final StaticResourceFactory<R> staticResourceFactory;
    private final SpincastRouterConfig spincastRouterConfig;
    private final RouteFactory<R> routeFactory;
    private final SpincastConfig spincastConfig;
    private final Dictionary dictionary;
    private final SpincastFilters<R> spincastFilters;
    private final WebsocketRouteBuilderFactory<R, W> websocketRouteBuilderFactory;
    private final WebsocketRouteHandlerFactory<R, W> websocketRouteHandlerFactory;
    private final SpincastRoutingUtils spincastRoutingUtils;

    private TreeMap<Integer, List<Route<R>>> globalBeforeFiltersPerPosition;
    private TreeMap<Integer, List<Route<R>>> globalAfterFiltersPerPosition;

    private List<Route<R>> globalBeforeFilters;
    private List<Route<R>> globalAfterFilters;
    private List<Route<R>> mainRoutes;

    private final Server server;

    private final Map<String, String> routeParamPatternAliases = new HashMap<String, String>();

    private final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

    @Inject
    public SpincastRouter(SpincastRouterDeps<R, W> spincastRouterDeps) {
        this.spincastRouterConfig = spincastRouterDeps.getSpincastRouterConfig();
        this.routeFactory = spincastRouterDeps.getRouteFactory();
        this.spincastConfig = spincastRouterDeps.getSpincastConfig();
        this.dictionary = spincastRouterDeps.getDictionary();
        this.server = spincastRouterDeps.getServer();
        this.spincastFilters = spincastRouterDeps.getSpincastFilters();
        this.routeBuilderFactory = spincastRouterDeps.getRouteBuilderFactory();
        this.redirectRuleBuilderFactory = spincastRouterDeps.getRedirectRuleBuilderFactory();
        this.staticResourceBuilderFactory = spincastRouterDeps.getStaticResourceBuilderFactory();
        this.routeHandlerMatchFactory = spincastRouterDeps.getRouteHandlerMatchFactory();
        this.staticResourceFactory = spincastRouterDeps.getStaticResourceFactory();
        this.websocketRouteBuilderFactory = spincastRouterDeps.getWebsocketRouteBuilderFactory();
        this.websocketRouteHandlerFactory = spincastRouterDeps.getWebsocketRouteHandlerFactory();
        this.spincastRoutingUtils = spincastRouterDeps.getSpincastRoutingUtils();
    }

    @Inject
    protected void init() {
        validation();
        addDefaultFilters();
    }

    protected void validation() {
        int corsFilterPosition = getSpincastRouterConfig().getCorsFilterPosition();
        if (corsFilterPosition >= 0) {
            throw new RuntimeException("The position of the Cors filter must be less than 0. " +
                                       "Currently : " + corsFilterPosition);
        }
    }

    protected void addDefaultFilters() {

        //==========================================
        // Default variables for the templating engine.
        //==========================================
        if (getSpincastConfig().isAddDefaultTemplateVariablesFilter()) {
            ALL(DEFAULT_ROUTE_PATH).id("spincast_default_template_variables")
                                   .spicastCoreRouteOrPluginRoute() // Spincast route!
                                   .pos(getSpincastConfig().getDefaultTemplateVariablesFilterPosition())
                                   .found().notFound().exception()
                                   .handle(new Handler<R>() {

                                       @Override
                                       public void handle(R context) {
                                           getSpincastFilters().addDefaultGlobalTemplateVariables(context);
                                       }
                                   });
        }
    }

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected RouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    protected Server getServer() {
        return this.server;
    }

    protected SpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    protected RouteBuilderFactory<R, W> getRouteBuilderFactory() {
        return this.routeBuilderFactory;
    }

    protected RedirectRuleBuilderFactory<R, W> getRedirectRuleBuilderFactory() {
        return this.redirectRuleBuilderFactory;
    }

    protected WebsocketRouteBuilderFactory<R, W> getWebsocketRouteBuilderFactory() {
        return this.websocketRouteBuilderFactory;
    }

    protected WebsocketRouteHandlerFactory<R, W> getWebsocketRouteHandlerFactory() {
        return this.websocketRouteHandlerFactory;
    }

    protected StaticResourceBuilderFactory<R, W> getStaticResourceBuilderFactory() {
        return this.staticResourceBuilderFactory;
    }

    protected RouteHandlerMatchFactory<R> getRouteHandlerMatchFactory() {
        return this.routeHandlerMatchFactory;
    }

    protected StaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    protected SpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    protected Pattern getPattern(String patternStr) {
        Pattern pattern = this.patternCache.get(patternStr);
        if (pattern == null) {
            pattern = Pattern.compile(patternStr);
            this.patternCache.put(patternStr, pattern);
        }
        return pattern;
    }

    @Override
    public Map<String, String> getRouteParamPatternAliases() {
        return this.routeParamPatternAliases;
    }

    @Override
    public Route<R> getRoute(String routeId) {

        for (Route<R> route : getGlobalBeforeFiltersRoutes()) {
            if ((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }
        for (Route<R> route : getMainRoutes()) {
            if ((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }
        for (Route<R> route : getGlobalAfterFiltersRoutes()) {
            if ((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }

        return null;
    }

    protected Map<Integer, List<Route<R>>> getGlobalBeforeFiltersPerPosition() {
        if (this.globalBeforeFiltersPerPosition == null) {
            this.globalBeforeFiltersPerPosition = new TreeMap<Integer, List<Route<R>>>();
        }
        return this.globalBeforeFiltersPerPosition;
    }

    @Override
    public List<Route<R>> getGlobalBeforeFiltersRoutes() {

        if (this.globalBeforeFilters == null) {
            this.globalBeforeFilters = new ArrayList<Route<R>>();

            Collection<List<Route<R>>> routesLists = getGlobalBeforeFiltersPerPosition().values();
            if (routesLists != null) {
                for (List<Route<R>> routeList : routesLists) {
                    this.globalBeforeFilters.addAll(routeList);
                }
            }
        }

        return this.globalBeforeFilters;
    }

    protected Map<Integer, List<Route<R>>> getGlobalAfterFiltersPerPosition() {
        if (this.globalAfterFiltersPerPosition == null) {
            this.globalAfterFiltersPerPosition = new TreeMap<Integer, List<Route<R>>>();
        }
        return this.globalAfterFiltersPerPosition;
    }

    @Override
    public List<Route<R>> getGlobalAfterFiltersRoutes() {
        if (this.globalAfterFilters == null) {
            this.globalAfterFilters = new ArrayList<Route<R>>();

            Collection<List<Route<R>>> routesLists = getGlobalAfterFiltersPerPosition().values();
            if (routesLists != null) {
                for (List<Route<R>> routeList : routesLists) {
                    this.globalAfterFilters.addAll(routeList);
                }
            }
        }

        return this.globalAfterFilters;
    }

    @Override
    public List<Route<R>> getMainRoutes() {

        if (this.mainRoutes == null) {
            this.mainRoutes = new ArrayList<>();
        }
        return this.mainRoutes;
    }

    @Override
    public void addRoute(Route<R> route) {

        if (route == null ||
            route.getMainHandler() == null ||
            route.getHttpMethods() == null) {
            return;
        }

        validateId(route.getId());

        validatePath(route.getPath());

        int position = route.getPosition();
        if (position < 0) {
            this.globalBeforeFilters = null; // reset cache
            List<Route<R>> routes = getGlobalBeforeFiltersPerPosition().get(position);
            if (routes == null) {
                routes = new ArrayList<Route<R>>();
                getGlobalBeforeFiltersPerPosition().put(position, routes);
            }
            routes.add(route);

        } else if (position == 0) {
            // Keep main routes in order they are added.
            getMainRoutes().add(route);
        } else {
            this.globalAfterFilters = null; // reset cache
            List<Route<R>> routes = getGlobalAfterFiltersPerPosition().get(position);
            if (routes == null) {
                routes = new ArrayList<Route<R>>();
                getGlobalAfterFiltersPerPosition().put(position, routes);
            }
            routes.add(route);
        }
    }

    protected void validateId(String id) {
        if (id == null) {
            return; //ok
        }

        Route<R> sameIdRoute = null;
        for (Route<R> route : getGlobalBeforeFiltersRoutes()) {
            if (id.equals(route.getId())) {
                sameIdRoute = route;
                break;
            }
        }
        if (sameIdRoute == null) {
            for (Route<R> route : getGlobalAfterFiltersRoutes()) {
                if (id.equals(route.getId())) {
                    sameIdRoute = route;
                    break;
                }
            }
        }
        if (sameIdRoute == null) {
            for (Route<R> route : getMainRoutes()) {
                if (id.equals(route.getId())) {
                    sameIdRoute = route;
                    break;
                }
            }
        }

        if (sameIdRoute != null) {
            throw new RuntimeException("A route already use the id '" + id + "' : " + sameIdRoute + ". Ids " +
                                       "must be uniques!");
        }
    }

    /**
     * Validate the path of a route.
     * Throws an exception if not valide.
     */
    protected void validatePath(String path) {
        if (path == null) {
            return;
        }

        Set<String> paramNames = new HashSet<String>();
        String[] pathTokens = path.split("/");
        boolean splatFound = false;
        for (String pathToken : pathTokens) {

            if (StringUtils.isBlank(pathToken)) {
                continue;
            }

            if (pathToken.startsWith("${") || pathToken.startsWith("*{")) {

                if (!pathToken.endsWith("}")) {
                    throw new RuntimeException("A parameter in the path of a route must end with '}'. Incorrect parameter : " +
                                               pathToken);
                }

                if (pathToken.startsWith("*{")) {
                    if (splatFound) {
                        throw new RuntimeException("The path of a route can only contain one " +
                                                   "splat parameter (the one starting with a '*{'). The path is : " +
                                                   path);
                    }

                    if (pathToken.contains(":")) {
                        throw new RuntimeException("A splat parameter can't contain a pattern (so no ':' allowed) : " +
                                                   pathToken);
                    }

                    splatFound = true;
                } else {

                    String token = pathToken.substring(2, pathToken.length() - 1);

                    int posColon = token.indexOf(":");
                    if (posColon > -1) {

                        token = token.substring(posColon + 1);

                        //==========================================
                        // Pattern aliases
                        //==========================================
                        if (token.startsWith("<")) {

                            if (!token.endsWith(">")) {
                                throw new RuntimeException("A parameter with an pattern alias must have a closing '>' : " +
                                                           pathToken);
                            }
                            token = token.substring(1, token.length() - 1);
                            String pattern = getPatternFromAlias(token);
                            if (pattern == null) {
                                throw new RuntimeException("Pattern not found using alias : " + token);
                            }
                        }
                    }
                }

                String paramName = pathToken.substring(2, pathToken.length() - 1);
                if (!StringUtils.isBlank(paramName) && paramNames.contains(paramName)) {
                    throw new RuntimeException("Two parameters with the same name, '" + paramName + "', in route with path : " +
                                               path);
                }
                paramNames.add(paramName);
            }
        }
    }

    @Override
    public void removeAllRoutes() {
        removeAllRoutes(false);
    }

    @Override
    public void removeAllRoutes(boolean removeSpincastAndPluginsRoutesToo) {

        this.globalBeforeFilters = null; // reset cache
        this.globalAfterFilters = null; // reset cache

        if (removeSpincastAndPluginsRoutesToo) {

            getGlobalBeforeFiltersPerPosition().clear();
            getMainRoutes().clear();
            getGlobalAfterFiltersPerPosition().clear();

        } else {

            Collection<List<Route<R>>> routeLists = getGlobalBeforeFiltersPerPosition().values();
            for (List<Route<R>> routes : routeLists) {
                for (int i = routes.size() - 1; i >= 0; i--) {
                    Route<R> route = routes.get(i);
                    if (!route.isSpicastCoreRouteOrPluginRoute()) {
                        routes.remove(i);
                    }
                }
            }

            routeLists = getGlobalAfterFiltersPerPosition().values();
            for (List<Route<R>> routes : routeLists) {
                for (int i = routes.size() - 1; i >= 0; i--) {
                    Route<R> route = routes.get(i);
                    if (!route.isSpicastCoreRouteOrPluginRoute()) {
                        routes.remove(i);
                    }
                }
            }

            List<Route<R>> routes = getMainRoutes();
            for (int i = routes.size() - 1; i >= 0; i--) {
                Route<R> route = routes.get(i);
                if (!route.isSpicastCoreRouteOrPluginRoute()) {
                    routes.remove(i);
                }
            }
        }
    }

    protected boolean startsWithAnyOf(String id, Set<String> prefixes) {

        if (id == null || prefixes == null || prefixes.size() == 0) {
            return false;
        }
        for (String prefix : prefixes) {
            if (id.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeRoute(String routeId) {

        if (routeId == null) {
            return;
        }

        Collection<List<Route<R>>> routeLists = getGlobalBeforeFiltersPerPosition().values();
        for (List<Route<R>> routes : routeLists) {
            for (int i = routes.size() - 1; i >= 0; i--) {
                Route<R> route = routes.get(i);
                if (route != null && routeId.equals(route.getId())) {
                    routes.remove(i);
                }
            }
        }
        this.globalBeforeFilters = null; // reset cache

        routeLists = getGlobalAfterFiltersPerPosition().values();
        for (List<Route<R>> routes : routeLists) {
            for (int i = routes.size() - 1; i >= 0; i--) {
                Route<R> route = routes.get(i);
                if (route != null && routeId.equals(route.getId())) {
                    routes.remove(i);
                }
            }
        }
        this.globalAfterFilters = null; // reset cache

        List<Route<R>> routes = getMainRoutes();
        for (int i = routes.size() - 1; i >= 0; i--) {
            Route<R> route = routes.get(i);
            if (route != null && routeId.equals(route.getId())) {
                routes.remove(i);
            }
        }
    }

    @Override
    public RoutingResult<R> route(R requestContext) {
        return route(requestContext, requestContext.request().getFullUrl(), RoutingType.FOUND);
    }

    @Override
    public RoutingResult<R> route(R requestContext,
                                  RoutingType routingType) {
        return route(requestContext, requestContext.request().getFullUrl(), routingType);
    }

    public RoutingResult<R> route(R requestContext,
                                  String fullUrl,
                                  RoutingType routingType) {
        try {

            URL url = new URL(fullUrl);
            HttpMethod httpMethod = requestContext.request().getHttpMethod();
            List<String> acceptedContentTypes = requestContext.request().getHeader(HttpHeaders.ACCEPT);
            if (acceptedContentTypes == null) {
                acceptedContentTypes = new ArrayList<String>();
            }

            List<RouteHandlerMatch<R>> routeHandlerMatches = new ArrayList<RouteHandlerMatch<R>>();

            //==========================================
            // First check if there is a main handler for this request.
            // We only keep the first match here!
            //==========================================
            List<RouteHandlerMatch<R>> mainRouteHandlerMatches = null;
            Route<R> matchingRoute = null;
            for (Route<R> route : getMainRoutes()) {

                List<RouteHandlerMatch<R>> routeHandlerMatch = createRegularHandlerMatches(routingType,
                                                                                           route,
                                                                                           httpMethod,
                                                                                           acceptedContentTypes,
                                                                                           url,
                                                                                           0);
                if (routeHandlerMatch != null && routeHandlerMatch.size() > 0) {
                    mainRouteHandlerMatches = routeHandlerMatch;
                    matchingRoute = route;
                    break;
                }
            }

            //==========================================
            // No main matches? Then no "before" or "after"
            // filters either!
            //==========================================
            if (matchingRoute != null) {

                //==========================================
                // First, the global "before" filters.
                //==========================================
                for (Route<R> route : getGlobalBeforeFiltersRoutes()) {

                    //==========================================
                    // Should this before filter be skipped?
                    //==========================================
                    if (route.getId() != null && matchingRoute.getFilterIdsToSkip().contains(route.getId())) {
                        continue;
                    }

                    if (!isRoutingTypeMatch(routingType, route)) {
                        continue;
                    }

                    if (isMustSkipResourceRequest(matchingRoute, route)) {
                        continue;
                    }

                    List<RouteHandlerMatch<R>> beforeRouteHandlerMatches = createRegularHandlerMatches(routingType,
                                                                                                       route,
                                                                                                       httpMethod,
                                                                                                       acceptedContentTypes,
                                                                                                       url,
                                                                                                       -1);
                    if (beforeRouteHandlerMatches != null) {
                        routeHandlerMatches.addAll(beforeRouteHandlerMatches);
                    }
                }

                //==========================================
                // The main handler match.
                //==========================================
                routeHandlerMatches.addAll(mainRouteHandlerMatches);

                //==========================================
                // Finally, the global "after" filters.
                //==========================================
                for (Route<R> route : getGlobalAfterFiltersRoutes()) {

                    //==========================================
                    // Should this after filter be skipped?
                    //==========================================
                    if (route.getId() != null && matchingRoute.getFilterIdsToSkip().contains(route.getId())) {
                        continue;
                    }

                    if (!isRoutingTypeMatch(routingType, route)) {
                        continue;
                    }

                    if (isMustSkipResourceRequest(matchingRoute, route)) {
                        continue;
                    }

                    List<RouteHandlerMatch<R>> afterRouteHandlerMatches =
                            createRegularHandlerMatches(routingType,
                                                        route,
                                                        httpMethod,
                                                        acceptedContentTypes,
                                                        url,
                                                        1);
                    if (afterRouteHandlerMatches != null) {
                        routeHandlerMatches.addAll(afterRouteHandlerMatches);
                    }
                }
            }

            if (routeHandlerMatches.size() == 0) {
                return null;
            }

            RoutingResult<R> routingResult = createRoutingResult(routeHandlerMatches);
            return routingResult;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isRoutingTypeMatch(RoutingType routingType, Route<R> route) {

        Objects.requireNonNull(routingType, "routingType can't be NULL");
        Objects.requireNonNull(route, "route can't be NULL");

        return route.getRoutingTypes() != null && route.getRoutingTypes().contains(routingType);
    }

    protected boolean isMustSkipResourceRequest(Route<R> mainRoute, Route<R> filterRoute) {
        if (mainRoute.isResourceRoute() && filterRoute.isSkipResourcesRequests()) {
            return true;
        }
        return false;
    }

    protected RoutingResult<R> createRoutingResult(List<RouteHandlerMatch<R>> routeHandlerMatches) {
        RoutingResult<R> routingResult = new RoutingResultDefault<R>(routeHandlerMatches);
        return routingResult;
    }

    /**
     * Get the matches (filters and main handle) if the route matches the URL and
     * HTTP method, or returns NULL otherwise.
     */
    protected List<RouteHandlerMatch<R>> createRegularHandlerMatches(RoutingType routingType,
                                                                     Route<R> route,
                                                                     HttpMethod httpMethod,
                                                                     List<String> acceptedContentTypes,
                                                                     URL url,
                                                                     int position) {

        if (!isRoutingTypeMatch(routingType, route)) {
            return null;
        }

        //==========================================
        // Validate the HTTP method.
        //==========================================
        if (!isRouteMatchHttpMethod(route, httpMethod)) {
            return null;
        }

        //==========================================
        // Validate the Accept content-types.
        //==========================================
        if (!isRouteMatchAcceptedContentType(route, acceptedContentTypes)) {
            return null;
        }

        //==========================================
        // Validate the route path.
        //==========================================
        String routePath = route.getPath();
        Map<String, String> matchingParams = validatePath(routePath, url);
        if (matchingParams == null) {
            return null;
        }

        //==========================================
        // Match!
        //==========================================
        List<RouteHandlerMatch<R>> matches = new ArrayList<RouteHandlerMatch<R>>();
        RouteHandlerMatch<R> routeHandlerMatch = getRouteHandlerMatchFactory().create(route,
                                                                                      route.getMainHandler(),
                                                                                      matchingParams,
                                                                                      position);
        matches.add(routeHandlerMatch);

        //==========================================
        // If the main handler has inline "before" filters, 
        // we add them with the same configurations as it.
        //==========================================
        List<Handler<R>> beforeFilters = route.getBeforeFilters();
        if (beforeFilters != null) {
            for (Handler<R> beforeFilter : beforeFilters) {
                if (beforeFilter != null) {
                    RouteHandlerMatch<R> beforeMethodRouteHandlerMatch =
                            createHandlerMatchForBeforeOrAfterFilter(routeHandlerMatch,
                                                                     beforeFilter,
                                                                     -1);
                    matches.add(0, beforeMethodRouteHandlerMatch);
                }
            }
        }

        //==========================================
        // If the main handler has inline "after" filters, 
        // we add them with the same configurations as it.
        //==========================================
        List<Handler<R>> afterFilters = route.getAfterFilters();
        if (afterFilters != null) {

            for (Handler<R> afterFilter : afterFilters) {
                if (afterFilter != null) {
                    RouteHandlerMatch<R> afterMethodRouteHandlerMatch =
                            createHandlerMatchForBeforeOrAfterFilter(routeHandlerMatch,
                                                                     afterFilter,
                                                                     1);
                    matches.add(afterMethodRouteHandlerMatch);
                }
            }
        }

        return matches;
    }

    protected boolean isRouteMatchAcceptedContentType(Route<R> route,
                                                      List<String> requestContentTypes) {

        if (requestContentTypes == null || requestContentTypes.size() == 0) {
            return true;
        }

        Set<String> routeContentTypes = route.getAcceptedContentTypes();
        if (routeContentTypes == null || routeContentTypes.size() == 0) {
            return true;
        }

        for (String contentType : requestContentTypes) {
            if (contentType != null && routeContentTypes.contains(contentType.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates an handler match with no matching params.
     */
    protected RouteHandlerMatch<R> createNoMatchingParamsHandlerMatch(Route<R> route,
                                                                      String id,
                                                                      Handler<R> handler,
                                                                      int position) {
        return getRouteHandlerMatchFactory().create(route,
                                                    handler,
                                                    null,
                                                    position);
    }

    /**
     * Creates a new match for a "before" or "after" handler specific
     * to a route. THis measn keeping the same informations as the
     * main handler.
     */
    protected RouteHandlerMatch<R> createHandlerMatchForBeforeOrAfterFilter(RouteHandlerMatch<R> mainRouteHandlerMatch,
                                                                            Handler<R> beforeOrAfterMethod,
                                                                            int position) {

        RouteHandlerMatch<R> routeHandlerMatch = getRouteHandlerMatchFactory().create(mainRouteHandlerMatch.getSourceRoute(),
                                                                                      beforeOrAfterMethod,
                                                                                      mainRouteHandlerMatch.getPathParams(),
                                                                                      position);
        return routeHandlerMatch;
    }

    /**
     * Validate if a route matches the given HTTP method.
     */
    protected boolean isRouteMatchHttpMethod(Route<R> route, HttpMethod httpMethod) {
        return route.getHttpMethods().contains(httpMethod);
    }

    /**
     * Validate if url matches the path of the route and if so, returns the
     * parsed parameters, if any.
     * 
     * Returns NULL if there is no match.
     */
    protected Map<String, String> validatePath(String routePath, URL url) {

        String urlPath = url.getPath();
        String urlPathSlashesStriped = StringUtils.strip(urlPath, "/ ");

        String routePathSlashesStriped = StringUtils.strip(routePath, "/ ");

        boolean routesAreCaseSensitive = getSpincastConfig().isRoutesCaseSensitive();

        //==========================================
        // URL and route path are the same : match!
        //==========================================
        if (routesAreCaseSensitive) {
            if (urlPathSlashesStriped.equals(routePathSlashesStriped)) {
                return new HashMap<String, String>();
            }
        } else {
            if (urlPathSlashesStriped.equalsIgnoreCase(routePathSlashesStriped)) {
                return new HashMap<String, String>();
            }
        }

        //==========================================
        // If the route path doesn't contain any "${" or "*{" there no
        // need to validate furthermore...
        //==========================================
        int pos = routePathSlashesStriped.indexOf("*{");
        boolean hasSplat = true;
        if (pos < 0) {
            hasSplat = false;
            pos = routePathSlashesStriped.indexOf("${");
            if (pos < 0) {
                return null;
            }
        }

        String[] routePathTokens = routePathSlashesStriped.split("/");
        if (routePathTokens.length == 1 && routePathTokens[0].equals("")) {
            routePathTokens = new String[0];
        }

        String[] urlPathTokens = urlPathSlashesStriped.split("/");
        if (urlPathTokens.length == 1 && urlPathTokens[0].equals("")) {
            urlPathTokens = new String[0];
        }

        //==========================================
        // The Url should have at least as many tokens
        // than the route path but can also have more in case
        // a splat "*{" token is used in the route path!
        //==========================================
        if (!hasSplat && urlPathTokens.length > routePathTokens.length) {
            return null;
        }

        Map<String, String> params = new HashMap<String, String>();

        int routePathTokenPos = 0;
        int urlTokenPos = 0;
        for (; routePathTokenPos < routePathTokens.length; routePathTokenPos++) {

            String routePathToken = routePathTokens[routePathTokenPos];

            String urlPathToken = "";
            if (!(routePathToken.startsWith("*{") && urlTokenPos >= urlPathTokens.length)) {

                if (urlTokenPos + 1 > urlPathTokens.length) {
                    return null;
                }

                urlPathToken = urlPathTokens[urlTokenPos];
            }

            //==========================================
            // The position of the url's tokens may increase
            // faster than the one of the route path because 
            // of splat tokens!
            //==========================================
            urlTokenPos++;

            //==========================================
            // For a token that doesn't start with "${" or "*{", the
            // same value must be in url and in the route path.
            //==========================================
            if (!routePathToken.startsWith("${") && !routePathToken.startsWith("*{")) {
                if (routesAreCaseSensitive) {
                    if (!urlPathToken.equals(routePathToken)) {
                        return null;
                    }
                } else {
                    if (!urlPathToken.equalsIgnoreCase(routePathToken)) {
                        return null;
                    }
                }
            } else {

                String paramName = routePathToken.substring(2, routePathToken.length() - 1);
                String paramValue;
                try {
                    paramValue = URLDecoder.decode(urlPathToken, "UTF-8");
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }

                //==========================================
                // If there a pattern?
                //==========================================
                String pattern = null;
                if (routePathToken.startsWith("${")) {

                    int posComma = paramName.indexOf(":");
                    if (posComma > -1) {
                        pattern = paramName.substring(posComma + 1);
                        paramName = paramName.substring(0, posComma);

                        if (StringUtils.isBlank(pattern)) {
                            pattern = null;
                        } else {

                            //==========================================
                            // Is it a pattern alias?
                            //==========================================
                            if (pattern.startsWith("<") && pattern.endsWith(">")) {
                                pattern = getPatternFromAlias(pattern.substring(1, pattern.length() - 1));
                            }
                        }
                    }

                    if (pattern != null && !getPattern(pattern).matcher(urlPathToken).matches()) {
                        this.logger.debug("Url token '" + urlPathToken + "' doesn't match pattern '" + pattern + "'.");
                        return null;
                    }

                }
                //==========================================
                // Splat param : the value can contain multiple
                // tokens from the url.
                //==========================================
                else if (routePathToken.startsWith("*{")) {

                    int nbrTokensInSplat = urlPathTokens.length - routePathTokens.length + 1;
                    if (nbrTokensInSplat > 1) {

                        StringBuilder builder = new StringBuilder(paramValue);
                        for (int i = 1; i < nbrTokensInSplat; i++) {
                            builder.append("/").append(urlPathTokens[urlTokenPos]);
                            urlTokenPos++;
                        }
                        paramValue = builder.toString();
                    }
                }

                //==========================================
                // We do not collect the parameters without names.
                //==========================================
                if (!StringUtils.isBlank(paramName)) {
                    params.put(paramName, paramValue);
                }
            }
        }

        return params;
    }

    /**
     * Get a path pattern from its alias.
     * @return the pattern or NULL if not found.
     */
    protected String getPatternFromAlias(String alias) {

        if (alias == null) {
            return null;
        }

        for (Entry<String, String> entry : getRouteParamPatternAliases().entrySet()) {
            if (alias.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public void addRouteParamPatternAlias(String alias, String pattern) {

        if (StringUtils.isBlank(alias)) {
            throw new RuntimeException("The alias can't be empty.");
        }
        if (StringUtils.isBlank(pattern)) {
            throw new RuntimeException("The pattern can't be empty.");
        }

        getRouteParamPatternAliases().put(alias, pattern);
    }

    @Override
    public RouteBuilder<R> GET() {
        return GET(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> GET(String path) {

        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.GET();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> POST() {
        return POST(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> POST(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.POST();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> PUT() {
        return PUT(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> PUT(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.PUT();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> DELETE() {
        return DELETE(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> DELETE(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.DELETE();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> OPTIONS() {
        return OPTIONS(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> OPTIONS(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.OPTIONS();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> TRACE() {
        return TRACE(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> TRACE(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.TRACE();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> HEAD() {
        return HEAD(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> HEAD(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.HEAD();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> PATCH() {
        return PATCH(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> PATCH(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.PATCH();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> ALL() {
        return ALL(DEFAULT_ROUTE_PATH);
    }

    @Override
    public RouteBuilder<R> ALL(String path) {
        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.ALL();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public RouteBuilder<R> methods(HttpMethod... httpMethods) {
        return methods(DEFAULT_ROUTE_PATH, httpMethods);
    }

    @Override
    public RouteBuilder<R> methods(String path, HttpMethod... httpMethods) {

        if (httpMethods.length == 0) {
            throw new RuntimeException("Using methods(...), you have to specify at least one HTTP method.");
        }

        return methods(path, Sets.newHashSet(httpMethods));
    }

    @Override
    public RouteBuilder<R> methods(Set<HttpMethod> httpMethods) {
        return methods(DEFAULT_ROUTE_PATH, httpMethods);
    }

    @Override
    public RouteBuilder<R> methods(String path, Set<HttpMethod> httpMethods) {

        if (httpMethods == null || httpMethods.size() == 0) {
            throw new RuntimeException("Using methods(...), you have to specify at least one HTTP method.");
        }

        RouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.methods(httpMethods);
        builder = builder.path(path);

        return builder;
    }

    @Override
    public void exception(Handler<R> handler) {
        exception(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void exception(String path, Handler<R> handler) {
        ALL(path).exception().handle(handler);
    }

    @Override
    public void notFound(Handler<R> handler) {
        notFound(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void notFound(String path, Handler<R> handler) {
        ALL(path).notFound().handle(handler);
    }

    @Override
    public void cors() {
        cors(DEFAULT_ROUTE_PATH);
    }

    @Override
    public void cors(Set<String> allowedOrigins) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins);
    }

    @Override
    public void cors(Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins,
             extraHeadersAllowedToBeRead);
    }

    @Override
    public void cors(Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent);
    }

    @Override
    public void cors(Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead, Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies);
    }

    @Override
    public void cors(Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead, Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies, Set<HttpMethod> allowedMethods) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies,
             allowedMethods);
    }

    @Override
    public void cors(Set<String> allowedOrigins, Set<String> extraHeadersAllowedToBeRead, Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies, Set<HttpMethod> allowedMethods, int maxAgeInSeconds) {
        cors(DEFAULT_ROUTE_PATH,
             allowedOrigins,
             extraHeadersAllowedToBeRead,
             extraHeadersAllowedToBeSent,
             allowCookies,
             allowedMethods,
             maxAgeInSeconds);
    }

    @Override
    public void cors(String path) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins,
                     final Set<String> extraHeadersAllowedToBeRead) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins,
                                                   extraHeadersAllowedToBeRead);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins,
                     final Set<String> extraHeadersAllowedToBeRead,
                     final Set<String> extraHeadersAllowedToBeSent) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins,
                                                   extraHeadersAllowedToBeRead,
                                                   extraHeadersAllowedToBeSent);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins,
                     final Set<String> extraHeadersAllowedToBeRead,
                     final Set<String> extraHeadersAllowedToBeSent,
                     final boolean allowCookies) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins,
                                                   extraHeadersAllowedToBeRead,
                                                   extraHeadersAllowedToBeSent,
                                                   allowCookies);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins,
                     final Set<String> extraHeadersAllowedToBeRead,
                     final Set<String> extraHeadersAllowedToBeSent,
                     final boolean allowCookies,
                     final Set<HttpMethod> allowedMethods) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins,
                                                   extraHeadersAllowedToBeRead,
                                                   extraHeadersAllowedToBeSent,
                                                   allowCookies,
                                                   allowedMethods);
                     }
                 });
    }

    @Override
    public void cors(String path,
                     final Set<String> allowedOrigins,
                     final Set<String> extraHeadersAllowedToBeRead,
                     final Set<String> extraHeadersAllowedToBeSent,
                     final boolean allowCookies,
                     final Set<HttpMethod> allowedMethods,
                     final int maxAgeInSeconds) {

        ALL(path).pos(getSpincastRouterConfig().getCorsFilterPosition())
                 .found().notFound().handle(new Handler<R>() {

                     @Override
                     public void handle(R context) {
                         getSpincastFilters().cors(context,
                                                   allowedOrigins,
                                                   extraHeadersAllowedToBeRead,
                                                   extraHeadersAllowedToBeSent,
                                                   allowCookies,
                                                   allowedMethods,
                                                   maxAgeInSeconds);
                     }
                 });
    }

    @Override
    public StaticResourceBuilder<R> file(String url) {
        StaticResourceBuilder<R> builder = getStaticResourceBuilderFactory().create(this, false);
        builder = builder.url(url);

        return builder;
    }

    @Override
    public StaticResourceBuilder<R> dir(String url) {
        StaticResourceBuilder<R> builder = getStaticResourceBuilderFactory().create(this, true);
        builder = builder.url(url);

        return builder;
    }

    @Override
    public void addStaticResource(final StaticResource<R> staticResource) {

        if (staticResource.getUrlPath() == null) {
            throw new RuntimeException("The URL to the resource must be specified!");
        }

        if (staticResource.getResourcePath() == null) {
            throw new RuntimeException("A classpath or a file system path must be specified!");
        }

        if (staticResource.isDirResource() &&
            getSpincastRoutingUtils().isPathContainDynamicParams(staticResource.getResourcePath())) {
            throw new RuntimeException("You can't use a dynamic (or splat) parameters in the path of the " +
                                       "target resource when using 'dir()'. The resulting path must be fixed, since " +
                                       "the server will try to find the resource in it. It cannot depend on the " +
                                       "current request since the server doesn't know about dynamic parameters and how to " +
                                       "replace them.");
        }

        if (staticResource.isClasspath() && staticResource.getGenerator() != null) {
            throw new RuntimeException("A resource generator can only be specified when a file system " +
                                       "path is used, not a classpath path.");
        }

        boolean dynParamFound = false;
        boolean splatParamFound = false;

        StringBuilder urlWithoutEndingSplatParamBuilder = new StringBuilder("");
        String[] tokens = staticResource.getUrlPath().split("/");
        for (String token : tokens) {

            token = token.trim();
            if (StringUtils.isBlank(token)) {
                continue;
            }

            boolean isDynParam = false;
            boolean isSplatParam = false;

            if (token.startsWith("${")) {
                isDynParam = true;
            } else if (token.startsWith("*{")) {
                isSplatParam = true;
            }

            if (!isSplatParam) {
                urlWithoutEndingSplatParamBuilder.append("/").append(token);
            }

            if (staticResource.isFileResource()) {

                if (isSplatParam) {
                    throw new RuntimeException("A file resource path can't contain a splat parameter. " +
                                               "Use 'dir()' instead!");
                }

                if (!staticResource.isCanBeGenerated() && isDynParam) {
                    throw new RuntimeException("A file resource path can't contain dynamic parameters if no generator " +
                                               "is used. Use 'dir()', add a generator or use " +
                                               "a regular route instead.");
                }
            } else {

                if (isDynParam) {
                    throw new RuntimeException("A dir static resource route can't contains any standard dynamic parameter. It can only contain " +
                                               "a splat parameter, at the very end of the path. Invalid token : " + token);
                } else if (splatParamFound) { // Another splat param already found...
                    throw new RuntimeException("A dir resource path can contain one splat parameter, and only at the very end of the path! " +
                                               "For example, this is invalid as a path : '/one/*{param1}/two', but this is valid : '/one/two/*{param1}'.");
                }
            }

            if (isDynParam) {
                dynParamFound = true;
            } else if (isSplatParam) {
                splatParamFound = true;
            }
        }

        String urlWithoutSplatParam = urlWithoutEndingSplatParamBuilder.toString();
        if (StringUtils.isBlank(urlWithoutSplatParam)) {
            urlWithoutSplatParam = "/";
        }

        //==========================================
        // We directly register the static resource on the server, but only if:
        // - It doesn't contain any dynamic or splat params.
        // - It is a dir resource and contains a splat param. In that
        //   case, we strip the splat part before adding the static resource.
        //==========================================
        if (staticResource.isDirResource() && splatParamFound) {

            StaticResource<R> staticResourceNoDynParams =
                    getStaticResourceFactory().create(staticResource.isSpicastOrPluginAddedResource(),
                                                      staticResource.getStaticResourceType(),
                                                      urlWithoutSplatParam,
                                                      staticResource.getResourcePath(),
                                                      staticResource.getGenerator(),
                                                      staticResource.getCorsConfig(),
                                                      staticResource.getCacheConfig(),
                                                      staticResource.isIgnoreQueryString());
            getServer().addStaticResourceToServe(staticResourceNoDynParams);
        } else if (!splatParamFound && !dynParamFound) {
            getServer().addStaticResourceToServe(staticResource);
        }

        //==========================================
        // If the resource is dynamic, we add its generator 
        // as a route for the same path! 
        // We also add an "after" filter which will try to
        // automatically save the generated resource. The headers
        // shouln't have been sent for that to work!
        //==========================================
        final boolean mustBeRegisteredOnServer = staticResource.isFileResource() && (splatParamFound || dynParamFound);
        if (staticResource.getGenerator() != null) {

            Route<R> route = null;
            Handler<R> saveResourceFilter = null;

            //==========================================
            // We add a filter to save the generated resource on disk.
            //
            // In debug mode, we may not want to create the resource on disk : the
            // generator will always be called so new modifications will
            // be picked up.
            //==========================================
            if (isCreateStaticResourceOnDisk()) {

                final String urlWithoutSplatParamFinal = urlWithoutSplatParam;
                saveResourceFilter = new Handler<R>() {

                    @Override
                    public void handle(R context) {

                        if (HttpStatus.SC_OK != context.response().getStatusCode()) {
                            SpincastRouter.this.logger.info("Nothing will be saved since the response code is not " +
                                                            HttpStatus.SC_OK);
                            return;
                        }

                        if (!staticResource.isIgnoreQueryString() &&
                            context.request().getQueryStringParams() != null &&
                            context.request().getQueryStringParams().size() > 0) {
                            SpincastRouter.this.logger.info("Nothing will be saved since the queryString contains parameters and " +
                                                            "'isIgnoreQueryString' is false  : " +
                                                            context.request().getQueryString(false));
                            return;
                        }

                        if (context.response().isHeadersSent()) {
                            SpincastRouter.this.logger.warn("Headers sent, we can't save a copy of the generated resource! You will have to make sure that " +
                                                            "you save the generated resource by yourself, otherwise, a new version will be generated for each " +
                                                            "request!");
                            return;
                        }

                        if (staticResource.isDirResource()) {

                            String urlPathPrefix = StringUtils.stripStart(urlWithoutSplatParamFinal, "/");

                            String requestPath = context.request().getRequestPath();
                            requestPath = StringUtils.stripStart(requestPath, "/");

                            if (!requestPath.startsWith(urlPathPrefix)) {
                                throw new RuntimeException("The requestPath '" + requestPath +
                                                           "' should starts with the urlPathPrefix '" + urlPathPrefix +
                                                           "' here!");
                            }
                            requestPath = requestPath.substring(urlPathPrefix.length());

                            // Make sure the path of the resource to generate is safe!
                            String resourceToGeneratePath;
                            try {
                                resourceToGeneratePath =
                                        new File(staticResource.getResourcePath() + "/" + requestPath).getCanonicalFile()
                                                                                                      .getAbsolutePath();
                                String resourcesRoot =
                                        new File(staticResource.getResourcePath()).getCanonicalFile().getAbsolutePath();

                                if (!resourceToGeneratePath.startsWith(resourcesRoot)) {
                                    throw new RuntimeException("The requestPath '" + resourceToGeneratePath +
                                                               "' should be inside the root resources folder : " + resourcesRoot);
                                }

                                //==========================================
                                // Save the resource in the dynamic dir.
                                //==========================================
                                if (!StringUtils.isBlank(requestPath)) {
                                    getSpincastFilters().saveGeneratedResource(context, resourceToGeneratePath);
                                }
                            } catch (Exception ex) {
                                throw SpincastStatics.runtimize(ex);
                            }
                        } else {

                            //==========================================
                            // We build the target file path to generate using
                            // dynamic params on the route URL, if any
                            //==========================================
                            String targetPath = staticResource.getResourcePath();
                            ReplaceDynamicParamsResult result =
                                    getSpincastRoutingUtils().replaceDynamicParamsInPath(targetPath,
                                                                                         context.request().getPathParams());
                            if (result.isPlaceholdersRemaining()) {
                                throw new RuntimeException("Not supposed : there are some remaining placeholders in the " +
                                                           "target path for the generated static resource file : " +
                                                           result.getPath());
                            }

                            targetPath = result.getPath();

                            @SuppressWarnings("unused")
                            boolean resourceSaved = getSpincastFilters().saveGeneratedResource(context, targetPath);

                            //==========================================
                            // Do we have to register this new static resource
                            // on the server? it may not already be.
                            //==========================================
                            if (mustBeRegisteredOnServer) {

                                StaticResource<R> newStaticResource =
                                        getStaticResourceFactory().create(staticResource.isSpicastOrPluginAddedResource(),
                                                                          staticResource.getStaticResourceType(),
                                                                          context.request().getRequestPath(),
                                                                          targetPath,
                                                                          staticResource.getGenerator(),
                                                                          staticResource.getCorsConfig(),
                                                                          staticResource.getCacheConfig(),
                                                                          staticResource.isIgnoreQueryString());
                                getServer().addStaticResourceToServe(newStaticResource);
                            }
                        }

                        //==========================================
                        // Some Caching headers to send?
                        //==========================================
                        StaticResourceCacheConfig cacheConfig = staticResource.getCacheConfig();
                        if (cacheConfig != null) {
                            getSpincastFilters().cache(context,
                                                       cacheConfig.getCacheSeconds(),
                                                       cacheConfig.isCachePrivate(),
                                                       cacheConfig.getCacheSecondsCdn());
                        }

                    }
                };
            }

            route = getRouteFactory().createRoute(null,
                                                  true, // is a resource route!
                                                  staticResource.isSpicastOrPluginAddedResource(),
                                                  Sets.newHashSet(HttpMethod.GET),
                                                  staticResource.getUrlPath(),
                                                  Sets.newHashSet(RoutingType.FOUND),
                                                  null,
                                                  staticResource.getGenerator(),
                                                  saveResourceFilter != null ? Arrays.asList(saveResourceFilter) : null,
                                                  0,
                                                  null,
                                                  null,
                                                  false);

            addRoute(route);
        }
    }

    protected boolean isCreateStaticResourceOnDisk() {
        return getSpincastConfig().isWriteToDiskDynamicStaticResource();
    }

    @Override
    public void httpAuth(String pathPrefix, String realmName) {

        if (StringUtils.isBlank(realmName)) {
            throw new RuntimeException("The realm name can't be empty");
        }
        if (StringUtils.isBlank(pathPrefix)) {
            pathPrefix = "/";
        } else if (!pathPrefix.startsWith("/")) {
            pathPrefix = "/" + pathPrefix;
        }

        String[] tokens = pathPrefix.split("/");
        for (String token : tokens) {
            token = token.trim();
            if (token.startsWith("${") || token.startsWith("*{")) {
                throw new RuntimeException("The path prefix for an HTTP authenticated section can't contain " +
                                           "any dynamic parameters: " + token);
            }
        }

        getServer().createHttpAuthenticationRealm(pathPrefix, realmName);
    }

    @Override
    public WebsocketRouteBuilder<R, W> websocket(String path) {

        WebsocketRouteBuilder<R, W> builder = getWebsocketRouteBuilderFactory().create(this);
        builder = builder.path(path);

        return builder;
    }

    @Override
    public void addWebsocketRoute(WebsocketRoute<R, W> websocketRoute) {

        //==========================================
        // We create an HTTP route from the Websocket route
        // informations: this allows the inital request to
        // be routed exactly as a standard route and to have "before"
        // filters applied.
        //==========================================
        Route<R> httpRoute = createHttpRouteFromWebsocketRoute(websocketRoute);
        addRoute(httpRoute);
    }

    protected Route<R> createHttpRouteFromWebsocketRoute(final WebsocketRoute<R, W> websocketRoute) {

        //==========================================
        // We create the "main" route handler for this
        // route: its job will be to convert the HTTP request 
        // to a Websocket connection.
        //==========================================
        final Handler<R> routeHandler = getWebsocketRouteHandlerFactory().createWebsocketRouteHandler(websocketRoute);

        Route<R> httpRoute = new Route<R>() {

            @Override
            public String getId() {
                return websocketRoute.getId();
            }

            @Override
            public boolean isResourceRoute() {
                return false;
            }

            @Override
            public boolean isSpicastCoreRouteOrPluginRoute() {
                return websocketRoute.isSpicastCoreRouteOrPluginRoute();
            }

            @Override
            public String getPath() {
                return websocketRoute.getPath();
            }

            @Override
            public boolean isSkipResourcesRequests() {
                return false;
            }

            @Override
            public Set<HttpMethod> getHttpMethods() {

                //==========================================
                // Websocket connection request only valid using a 
                // GET method.
                //==========================================
                return Sets.newHashSet(HttpMethod.GET);
            }

            @Override
            public Set<String> getAcceptedContentTypes() {

                //==========================================
                // Not interesting for a Websocket connection.
                //==========================================
                return null;
            }

            @Override
            public Set<RoutingType> getRoutingTypes() {
                return Sets.newHashSet(RoutingType.FOUND);
            }

            @Override
            public Handler<R> getMainHandler() {

                //==========================================
                // The Websocket route hander we just created...
                //==========================================
                return routeHandler;
            }

            @Override
            public List<Handler<R>> getBeforeFilters() {
                return websocketRoute.getBeforeFilters();
            }

            @Override
            public List<Handler<R>> getAfterFilters() {
                //==========================================
                // No "after" filter for a Websocket route:
                // if the Websocket connection is established, 
                // the HTTP request is not anymore.
                //==========================================
                return null;
            }

            @Override
            public int getPosition() {

                //==========================================
                // Websocket routes can't be used as filters.
                //==========================================
                return 0;
            }

            @Override
            public Set<String> getFilterIdsToSkip() {
                return websocketRoute.getFilterIdsToSkip();
            }

        };

        return httpRoute;
    }

    @Override
    public RedirectRuleBuilder redirect(String oldPath) {
        RedirectRuleBuilder builder = getRedirectRuleBuilderFactory().create(this, oldPath);
        return builder;
    }


}

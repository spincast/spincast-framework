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
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.IRouteBuilder;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingResult;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceBuilder;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

/**
 * Spincast router 
 */
public class SpincastRouter<R extends IRequestContext<?>> implements IRouter<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastRouter.class);

    private final IRouteHandlerMatchFactory<R> routeHandlerMatchFactory;
    private final IRouteBuilderFactory<R> routeBuilderFactory;
    private final IStaticResourceBuilderFactory<R> staticResourceBuilderFactory;
    private final IStaticResourceFactory<R> staticResourceFactory;
    private final ISpincastRouterConfig spincastRouterConfig;
    private final IRouteFactory<R> routeFactory;
    private final ISpincastConfig spincastConfig;
    private final ISpincastDictionary spincastDictionary;
    private final ISpincastFilters<R> spincastFilters;

    private TreeMap<Integer, List<IRoute<R>>> globalBeforeFiltersPerPosition;
    private TreeMap<Integer, List<IRoute<R>>> globalAfterFiltersPerPosition;

    private List<IRoute<R>> globalBeforeFilters;
    private List<IRoute<R>> globalAfterFilters;
    private List<IRoute<R>> mainRoutes;

    private final IServer server;

    private final Map<String, String> routeParamPatternAliases = new HashMap<String, String>();

    private final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

    @Inject
    public SpincastRouter(ISpincastRouterConfig spincastRouterConfig,
                          IRouteFactory<R> routeFactory,
                          ISpincastConfig spincastConfig,
                          ISpincastDictionary spincastDictionary,
                          IServer server,
                          ISpincastFilters<R> spincastFilters,
                          IRouteBuilderFactory<R> routeBuilderFactory,
                          IStaticResourceBuilderFactory<R> staticResourceBuilderFactory,
                          IRouteHandlerMatchFactory<R> routeHandlerMatchFactory,
                          IStaticResourceFactory<R> staticResourceFactory) {
        this.spincastRouterConfig = spincastRouterConfig;
        this.routeFactory = routeFactory;
        this.spincastConfig = spincastConfig;
        this.spincastDictionary = spincastDictionary;
        this.server = server;
        this.spincastFilters = spincastFilters;
        this.routeBuilderFactory = routeBuilderFactory;
        this.staticResourceBuilderFactory = staticResourceBuilderFactory;
        this.routeHandlerMatchFactory = routeHandlerMatchFactory;
        this.staticResourceFactory = staticResourceFactory;
    }

    @Inject
    protected void init() {
        validation();
    }

    protected void validation() {
        int corsFilterPosition = getSpincastRouterConfig().getCorsFilterPosition();
        if(corsFilterPosition >= 0) {
            throw new RuntimeException("The position of the Cors filter must be less than 0. " +
                                       "Currently : " + corsFilterPosition);
        }
    }

    protected ISpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected IRouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected ISpincastDictionary getSpincastDictionary() {
        return this.spincastDictionary;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected ISpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    protected IRouteBuilderFactory<R> getRouteBuilderFactory() {
        return this.routeBuilderFactory;
    }

    protected IStaticResourceBuilderFactory<R> getStaticResourceBuilderFactory() {
        return this.staticResourceBuilderFactory;
    }

    protected IRouteHandlerMatchFactory<R> getRouteHandlerMatchFactory() {
        return this.routeHandlerMatchFactory;
    }

    protected IStaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    protected Pattern getPattern(String patternStr) {
        Pattern pattern = this.patternCache.get(patternStr);
        if(pattern == null) {
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
    public IRoute<R> getRoute(String routeId) {

        for(IRoute<R> route : getGlobalBeforeFiltersRoutes()) {
            if((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }
        for(IRoute<R> route : getMainRoutes()) {
            if((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }
        for(IRoute<R> route : getGlobalAfterFiltersRoutes()) {
            if((routeId == null && route.getId() == null) || (routeId != null && routeId.equals(route.getId()))) {
                return route;
            }
        }

        return null;
    }

    protected Map<Integer, List<IRoute<R>>> getGlobalBeforeFiltersPerPosition() {
        if(this.globalBeforeFiltersPerPosition == null) {
            this.globalBeforeFiltersPerPosition = new TreeMap<Integer, List<IRoute<R>>>();
        }
        return this.globalBeforeFiltersPerPosition;
    }

    @Override
    public List<IRoute<R>> getGlobalBeforeFiltersRoutes() {

        if(this.globalBeforeFilters == null) {
            this.globalBeforeFilters = new ArrayList<IRoute<R>>();

            Collection<List<IRoute<R>>> routesLists = getGlobalBeforeFiltersPerPosition().values();
            if(routesLists != null) {
                for(List<IRoute<R>> routeList : routesLists) {
                    this.globalBeforeFilters.addAll(routeList);
                }
            }
        }

        return this.globalBeforeFilters;
    }

    protected Map<Integer, List<IRoute<R>>> getGlobalAfterFiltersPerPosition() {
        if(this.globalAfterFiltersPerPosition == null) {
            this.globalAfterFiltersPerPosition = new TreeMap<Integer, List<IRoute<R>>>();
        }
        return this.globalAfterFiltersPerPosition;
    }

    @Override
    public List<IRoute<R>> getGlobalAfterFiltersRoutes() {
        if(this.globalAfterFilters == null) {
            this.globalAfterFilters = new ArrayList<IRoute<R>>();

            Collection<List<IRoute<R>>> routesLists = getGlobalAfterFiltersPerPosition().values();
            if(routesLists != null) {
                for(List<IRoute<R>> routeList : routesLists) {
                    this.globalAfterFilters.addAll(routeList);
                }
            }
        }

        return this.globalAfterFilters;
    }

    @Override
    public List<IRoute<R>> getMainRoutes() {

        if(this.mainRoutes == null) {
            this.mainRoutes = new ArrayList<>();
        }
        return this.mainRoutes;
    }

    @Override
    public void addRoute(IRoute<R> route) {

        if(route == null ||
           route.getMainHandler() == null ||
           route.getHttpMethods() == null) {
            return;
        }

        validateId(route.getId());

        validatePath(route.getPath());

        List<Integer> positions = route.getPositions();
        for(int position : positions) {
            if(position < 0) {
                this.globalBeforeFilters = null; // reset cache
                List<IRoute<R>> routes = getGlobalBeforeFiltersPerPosition().get(position);
                if(routes == null) {
                    routes = new ArrayList<IRoute<R>>();
                    getGlobalBeforeFiltersPerPosition().put(position, routes);
                }
                routes.add(route);

            } else if(position == 0) {
                // Keep main routes in order they are added.
                getMainRoutes().add(route);
            } else {
                this.globalAfterFilters = null; // reset cache
                List<IRoute<R>> routes = getGlobalAfterFiltersPerPosition().get(position);
                if(routes == null) {
                    routes = new ArrayList<IRoute<R>>();
                    getGlobalAfterFiltersPerPosition().put(position, routes);
                }
                routes.add(route);
            }
        }
    }

    protected void validateId(String id) {
        if(id == null) {
            return; //ok
        }

        IRoute<R> sameIdRoute = null;
        for(IRoute<R> route : getGlobalBeforeFiltersRoutes()) {
            if(id.equals(route.getId())) {
                sameIdRoute = route;
                break;
            }
        }
        if(sameIdRoute == null) {
            for(IRoute<R> route : getGlobalAfterFiltersRoutes()) {
                if(id.equals(route.getId())) {
                    sameIdRoute = route;
                    break;
                }
            }
        }
        if(sameIdRoute == null) {
            for(IRoute<R> route : getMainRoutes()) {
                if(id.equals(route.getId())) {
                    sameIdRoute = route;
                    break;
                }
            }
        }

        if(sameIdRoute != null) {
            throw new RuntimeException("A route already use the id '" + id + "' : " + sameIdRoute + ". Ids " +
                                       "must be uniques!");
        }
    }

    /**
     * Validate the path of a route.
     * Throws an exception if not valide.
     */
    protected void validatePath(String path) {
        if(path == null) {
            return;
        }

        Set<String> paramNames = new HashSet<String>();
        String[] pathTokens = path.split("/");
        boolean splatFound = false;
        for(String pathToken : pathTokens) {

            if(StringUtils.isBlank(pathToken)) {
                continue;
            }

            if(pathToken.startsWith("${") || pathToken.startsWith("*{")) {

                if(!pathToken.endsWith("}")) {
                    throw new RuntimeException("A parameter in the path of a route must end with '}'. Incorrect parameter : " +
                                               pathToken);
                }

                if(pathToken.startsWith("*{")) {
                    if(splatFound) {
                        throw new RuntimeException("The path of a route can only contain one " +
                                                   "splat parameter (the one starting with a '*{'). The path is : " +
                                                   path);
                    }

                    if(pathToken.contains(":")) {
                        throw new RuntimeException("A splat parameter can't contain a pattern (so no ':' allowed) : " +
                                                   pathToken);
                    }

                    splatFound = true;
                } else {

                    String token = pathToken.substring(2, pathToken.length() - 1);

                    int posColon = token.indexOf(":");
                    if(posColon > -1) {

                        token = token.substring(posColon + 1);

                        //==========================================
                        // Pattern aliases
                        //==========================================
                        if(token.startsWith("<")) {

                            if(!token.endsWith(">")) {
                                throw new RuntimeException("A parameter with an pattern alias must have a closing '>' : " +
                                                           pathToken);
                            }
                            token = token.substring(1, token.length() - 1);
                            String pattern = getPatternFromAlias(token);
                            if(pattern == null) {
                                throw new RuntimeException("Pattern not found using alias : " + token);
                            }
                        }
                    }
                }

                String paramName = pathToken.substring(2, pathToken.length() - 1);
                if(!StringUtils.isBlank(paramName) && paramNames.contains(paramName)) {
                    throw new RuntimeException("Two parameters with the same name, '" + paramName + "', in route with path : " +
                                               path);
                }
                paramNames.add(paramName);
            }
        }
    }

    @Override
    public void removeAllRoutes() {
        getGlobalBeforeFiltersPerPosition().clear();
        this.globalBeforeFilters = null; // reset cache
        getMainRoutes().clear();
        getGlobalAfterFiltersPerPosition().clear();
        this.globalAfterFilters = null; // reset cache
    }

    @Override
    public void removeRoute(String routeId) {

        if(routeId == null) {
            return;
        }

        Collection<List<IRoute<R>>> routeLists = getGlobalBeforeFiltersPerPosition().values();
        for(List<IRoute<R>> routes : routeLists) {
            for(int i = routes.size() - 1; i >= 0; i--) {
                IRoute<R> route = routes.get(i);
                if(route != null && routeId.equals(route.getId())) {
                    routes.remove(i);
                }
            }
        }
        this.globalBeforeFilters = null; // reset cache

        routeLists = getGlobalAfterFiltersPerPosition().values();
        for(List<IRoute<R>> routes : routeLists) {
            for(int i = routes.size() - 1; i >= 0; i--) {
                IRoute<R> route = routes.get(i);
                if(route != null && routeId.equals(route.getId())) {
                    routes.remove(i);
                }
            }
        }
        this.globalAfterFilters = null; // reset cache

        List<IRoute<R>> routes = getMainRoutes();
        for(int i = routes.size() - 1; i >= 0; i--) {
            IRoute<R> route = routes.get(i);
            if(route != null && routeId.equals(route.getId())) {
                routes.remove(i);
            }
        }
    }

    @Override
    public IRoutingResult<R> route(R requestContext) {
        return route(requestContext, requestContext.request().getFullUrl(), RoutingType.NORMAL);
    }

    @Override
    public IRoutingResult<R> route(R requestContext,
                                   RoutingType routingType) {
        return route(requestContext, requestContext.request().getFullUrl(), routingType);
    }

    public IRoutingResult<R> route(R requestContext,
                                   String fullUrl,
                                   RoutingType routingType) {
        try {

            URL url = new URL(fullUrl);
            HttpMethod httpMethod = requestContext.request().getHttpMethod();
            List<String> acceptedContentTypes = requestContext.request().getHeader(HttpHeaders.ACCEPT);
            if(acceptedContentTypes == null) {
                acceptedContentTypes = new ArrayList<String>();
            }

            List<IRouteHandlerMatch<R>> routeHandlerMatches = new ArrayList<IRouteHandlerMatch<R>>();

            //==========================================
            // First check if there is a main handler for this request.
            // We only keep the first match here!
            //==========================================
            List<IRouteHandlerMatch<R>> mainRouteHandlerMatches = null;
            for(IRoute<R> route : getMainRoutes()) {

                List<IRouteHandlerMatch<R>> routeHandlerMatch = createRegularHandlerMatches(routingType,
                                                                                            route,
                                                                                            httpMethod,
                                                                                            acceptedContentTypes,
                                                                                            url,
                                                                                            0);
                if(routeHandlerMatch != null && routeHandlerMatch.size() > 0) {
                    mainRouteHandlerMatches = routeHandlerMatch;
                    break;
                }
            }

            //==========================================
            // No main matches? Then no "before" or "after"
            // filters either!
            //==========================================
            if(mainRouteHandlerMatches != null) {

                //==========================================
                // First, the global "before" filters.
                //==========================================
                for(IRoute<R> route : getGlobalBeforeFiltersRoutes()) {

                    if(!isRoutingTypeMatch(routingType, route)) {
                        continue;
                    }

                    List<IRouteHandlerMatch<R>> beforeRouteHandlerMatches = createRegularHandlerMatches(routingType,
                                                                                                        route,
                                                                                                        httpMethod,
                                                                                                        acceptedContentTypes,
                                                                                                        url,
                                                                                                        -1);
                    if(beforeRouteHandlerMatches != null) {
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
                for(IRoute<R> route : getGlobalAfterFiltersRoutes()) {

                    if(!isRoutingTypeMatch(routingType, route)) {
                        continue;
                    }

                    List<IRouteHandlerMatch<R>> afterRouteHandlerMatches =
                            createRegularHandlerMatches(routingType,
                                                        route,
                                                        httpMethod,
                                                        acceptedContentTypes,
                                                        url,
                                                        1);
                    if(afterRouteHandlerMatches != null) {
                        routeHandlerMatches.addAll(afterRouteHandlerMatches);
                    }
                }
            }

            if(routeHandlerMatches.size() == 0) {
                return null;
            }

            IRoutingResult<R> routingResult = createRoutingResult(routeHandlerMatches);
            return routingResult;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isRoutingTypeMatch(RoutingType routingType, IRoute<R> route) {

        Objects.requireNonNull(routingType, "routingType can't be NULL");
        Objects.requireNonNull(route, "route can't be NULL");

        return route.getRoutingTypes() != null && route.getRoutingTypes().contains(routingType);
    }

    protected IRoutingResult<R> createRoutingResult(List<IRouteHandlerMatch<R>> routeHandlerMatches) {
        IRoutingResult<R> routingResult = new RoutingResult<R>(routeHandlerMatches);
        return routingResult;
    }

    /**
     * Get the matches (filters and main handle) if the route matches the URL and
     * HTTP method, or returns NULL otherwise.
     */
    protected List<IRouteHandlerMatch<R>> createRegularHandlerMatches(RoutingType routingType,
                                                                      IRoute<R> route,
                                                                      HttpMethod httpMethod,
                                                                      List<String> acceptedContentTypes,
                                                                      URL url,
                                                                      int position) {

        if(!isRoutingTypeMatch(routingType, route)) {
            return null;
        }

        //==========================================
        // Validate the HTTP method.
        //==========================================
        if(!isRouteMatchHttpMethod(route, httpMethod)) {
            return null;
        }

        //==========================================
        // Validate the Accept content-types.
        //==========================================
        if(!isRouteMatchAcceptedContentType(route, acceptedContentTypes)) {
            return null;
        }

        //==========================================
        // Validate the route path.
        //==========================================
        String routePath = route.getPath();
        Map<String, String> matchingParams = validatePath(routePath, url);
        if(matchingParams == null) {
            return null;
        }

        //==========================================
        // Match!
        //==========================================
        List<IRouteHandlerMatch<R>> matches = new ArrayList<IRouteHandlerMatch<R>>();
        IRouteHandlerMatch<R> routeHandlerMatch = getRouteHandlerMatchFactory().create(route,
                                                                                       route.getMainHandler(),
                                                                                       matchingParams,
                                                                                       position);
        matches.add(routeHandlerMatch);

        //==========================================
        // If the main handler has inline "before" filters, 
        // we add them with the same configurations as it.
        //==========================================
        List<IHandler<R>> beforeFilters = route.getBeforeFilters();
        if(beforeFilters != null) {
            for(IHandler<R> beforeFilter : beforeFilters) {
                if(beforeFilter != null) {
                    IRouteHandlerMatch<R> beforeMethodRouteHandlerMatch =
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
        List<IHandler<R>> afterFilters = route.getAfterFilters();
        if(afterFilters != null) {

            for(IHandler<R> afterFilter : afterFilters) {
                if(afterFilter != null) {
                    IRouteHandlerMatch<R> afterMethodRouteHandlerMatch =
                            createHandlerMatchForBeforeOrAfterFilter(routeHandlerMatch,
                                                                     afterFilter,
                                                                     1);
                    matches.add(afterMethodRouteHandlerMatch);
                }
            }
        }

        return matches;
    }

    protected boolean isRouteMatchAcceptedContentType(IRoute<R> route,
                                                      List<String> requestContentTypes) {

        if(requestContentTypes == null || requestContentTypes.size() == 0) {
            return true;
        }

        Set<String> routeContentTypes = route.getAcceptedContentTypes();
        if(routeContentTypes == null || routeContentTypes.size() == 0) {
            return true;
        }

        for(String contentType : requestContentTypes) {
            if(contentType != null && routeContentTypes.contains(contentType.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates an handler match with no matching params.
     */
    protected IRouteHandlerMatch<R> createNoMatchingParamsHandlerMatch(IRoute<R> route,
                                                                       String id,
                                                                       IHandler<R> handler,
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
    protected IRouteHandlerMatch<R> createHandlerMatchForBeforeOrAfterFilter(IRouteHandlerMatch<R> mainRouteHandlerMatch,
                                                                             IHandler<R> beforeOrAfterMethod,
                                                                             int position) {

        IRouteHandlerMatch<R> routeHandlerMatch = getRouteHandlerMatchFactory().create(mainRouteHandlerMatch.getSourceRoute(),
                                                                                       beforeOrAfterMethod,
                                                                                       mainRouteHandlerMatch.getParameters(),
                                                                                       position);
        return routeHandlerMatch;
    }

    /**
     * Validate if a route matches the given HTTP method.
     */
    protected boolean isRouteMatchHttpMethod(IRoute<R> route, HttpMethod httpMethod) {
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
        if(routesAreCaseSensitive) {
            if(urlPathSlashesStriped.equals(routePathSlashesStriped)) {
                return new HashMap<String, String>();
            }
        } else {
            if(urlPathSlashesStriped.equalsIgnoreCase(routePathSlashesStriped)) {
                return new HashMap<String, String>();
            }
        }

        //==========================================
        // If the route path doesn't contain any "${" or "*{" there no
        // need to validate furthermore...
        //==========================================
        int pos = routePathSlashesStriped.indexOf("*{");
        boolean hasSplat = true;
        if(pos < 0) {
            hasSplat = false;
            pos = routePathSlashesStriped.indexOf("${");
            if(pos < 0) {
                return null;
            }
        }

        String[] routePathTokens = routePathSlashesStriped.split("/");
        if(routePathTokens.length == 1 && routePathTokens[0].equals("")) {
            routePathTokens = new String[0];
        }

        String[] urlPathTokens = urlPathSlashesStriped.split("/");
        if(urlPathTokens.length == 1 && urlPathTokens[0].equals("")) {
            urlPathTokens = new String[0];
        }

        //==========================================
        // The Url should have at least as many tokens
        // than the route path but can also have more in case
        // a splat "*{" token is used in the route path!
        //==========================================
        if(!hasSplat && urlPathTokens.length > routePathTokens.length) {
            return null;
        }

        Map<String, String> params = new HashMap<String, String>();

        int routePathTokenPos = 0;
        int urlTokenPos = 0;
        for(; routePathTokenPos < routePathTokens.length; routePathTokenPos++) {

            String routePathToken = routePathTokens[routePathTokenPos];

            String urlPathToken = "";
            if(!(routePathToken.startsWith("*{") && urlTokenPos >= urlPathTokens.length)) {

                if(urlTokenPos + 1 > urlPathTokens.length) {
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
            if(!routePathToken.startsWith("${") && !routePathToken.startsWith("*{")) {
                if(routesAreCaseSensitive) {
                    if(!urlPathToken.equals(routePathToken)) {
                        return null;
                    }
                } else {
                    if(!urlPathToken.equalsIgnoreCase(routePathToken)) {
                        return null;
                    }
                }
            } else {

                String paramName = routePathToken.substring(2, routePathToken.length() - 1);
                String paramValue;
                try {
                    paramValue = URLDecoder.decode(urlPathToken, "UTF-8");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }

                //==========================================
                // If there a pattern?
                //==========================================
                String pattern = null;
                if(routePathToken.startsWith("${")) {

                    int posComma = paramName.indexOf(":");
                    if(posComma > -1) {
                        pattern = paramName.substring(posComma + 1);
                        paramName = paramName.substring(0, posComma);

                        if(StringUtils.isBlank(pattern)) {
                            pattern = null;
                        } else {

                            //==========================================
                            // Is it a pattern alias?
                            //==========================================
                            if(pattern.startsWith("<") && pattern.endsWith(">")) {
                                pattern = getPatternFromAlias(pattern.substring(1, pattern.length() - 1));
                            }
                        }
                    }

                    if(pattern != null && !getPattern(pattern).matcher(urlPathToken).matches()) {
                        this.logger.debug("Url token '" + urlPathToken + "' doesn't match pattern '" + pattern + "'.");
                        return null;
                    }

                }
                //==========================================
                // Splat param : the value can contain multiple
                // tokens from the url.
                //==========================================
                else if(routePathToken.startsWith("*{")) {

                    int nbrTokensInSplat = urlPathTokens.length - routePathTokens.length + 1;
                    if(nbrTokensInSplat > 1) {

                        StringBuilder builder = new StringBuilder(paramValue);
                        for(int i = 1; i < nbrTokensInSplat; i++) {
                            builder.append("/").append(urlPathTokens[urlTokenPos]);
                            urlTokenPos++;
                        }
                        paramValue = builder.toString();
                    }
                }

                //==========================================
                // We do not collect the parameters without names.
                //==========================================
                if(!StringUtils.isBlank(paramName)) {
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

        if(alias == null) {
            return null;
        }

        for(Entry<String, String> entry : getRouteParamPatternAliases().entrySet()) {
            if(alias.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public void addRouteParamPatternAlias(String alias, String pattern) {

        if(StringUtils.isBlank(alias)) {
            throw new RuntimeException("The alias can't be empty.");
        }
        if(StringUtils.isBlank(pattern)) {
            throw new RuntimeException("The pattern can't be empty.");
        }

        getRouteParamPatternAliases().put(alias, pattern);
    }

    @Override
    public IRouteBuilder<R> GET(String path) {

        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.GET();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> POST(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.POST();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> PUT(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.PUT();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> DELETE(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.DELETE();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> OPTIONS(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.OPTIONS();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> TRACE(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.TRACE();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> HEAD(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.HEAD();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> PATCH(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.PATCH();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> ALL(String path) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.ALL();
        builder = builder.path(path);

        return builder;
    }

    @Override
    public IRouteBuilder<R> SOME(String path, HttpMethod... httpMethods) {

        if(httpMethods.length == 0) {
            throw new RuntimeException("Using SOME(...), you have to specify at least one HTTP method.");
        }

        return SOME(path, Sets.newHashSet(httpMethods));
    }

    @Override
    public IRouteBuilder<R> SOME(String path, Set<HttpMethod> httpMethods) {

        if(httpMethods == null || httpMethods.size() == 0) {
            throw new RuntimeException("Using SOME(...), you have to specify at least one HTTP method.");
        }

        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.SOME(httpMethods);
        builder = builder.path(path);

        return builder;
    }

    @Override
    public void before(IHandler<R> handler) {
        before(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void before(String path, IHandler<R> handler) {

        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.ALL();
        builder = builder.pos(-1);
        builder = builder.path(path);
        builder = addFilterDefaultRoutingTypes(builder);

        builder.save(handler);
    }

    @Override
    public void after(IHandler<R> handler) {
        after(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void after(String path, IHandler<R> handler) {
        IRouteBuilder<R> builder = getRouteBuilderFactory().create(this);
        builder = builder.ALL();
        builder = builder.pos(1);
        builder = builder.path(path);
        builder = addFilterDefaultRoutingTypes(builder);

        builder.save(handler);
    }

    @Override
    public void beforeAndAfter(IHandler<R> handler) {
        beforeAndAfter(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void beforeAndAfter(String path, IHandler<R> handler) {
        before(path, handler);
        after(path, handler);
    }

    protected IRouteBuilder<R> addFilterDefaultRoutingTypes(IRouteBuilder<R> builder) {

        Set<RoutingType> defaultRoutingTypes = getSpincastRouterConfig().getFilterDefaultRoutingTypes();
        for(RoutingType routingType : defaultRoutingTypes) {
            if(routingType == RoutingType.NORMAL) {
                builder.found();
            } else if(routingType == RoutingType.NOT_FOUND) {
                builder.notFound();
            } else if(routingType == RoutingType.EXCEPTION) {
                builder.exception();
            } else {
                throw new RuntimeException("Not managed : " + routingType);
            }
        }

        return builder;
    }

    @Override
    public void exception(IHandler<R> handler) {
        exception(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void exception(String path, IHandler<R> handler) {
        ALL(path).exception().save(handler);
    }

    @Override
    public void notFound(IHandler<R> handler) {
        notFound(DEFAULT_ROUTE_PATH, handler);
    }

    @Override
    public void notFound(String path, IHandler<R> handler) {
        ALL(path).notFound().save(handler);
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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
                 .found().notFound().save(new IHandler<R>() {

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
    public IStaticResourceBuilder<R> file(String url) {
        IStaticResourceBuilder<R> builder = getStaticResourceBuilderFactory().create(this, false);
        builder = builder.url(url);

        return builder;
    }

    @Override
    public IStaticResourceBuilder<R> dir(String url) {
        IStaticResourceBuilder<R> builder = getStaticResourceBuilderFactory().create(this, true);
        builder = builder.url(url);

        return builder;
    }

    @Override
    public void addStaticResource(final IStaticResource<R> staticResource) {

        if(staticResource.getUrlPath() == null) {
            throw new RuntimeException("The URL to the resource must be specified!");
        }

        if(staticResource.getResourcePath() == null) {
            throw new RuntimeException("A classpath or a file system path must be specified!");
        }

        if(staticResource.isClasspath() && staticResource.getGenerator() != null) {
            throw new RuntimeException("A resource generator can only be specified when a file system " +
                                       "path is used, not a classpath path.");
        }

        String urlPath = staticResource.getUrlPath();

        //==========================================
        // A file resource can't contains any dynamic parameters.
        // The serving handler of the HTTP server may not understand them.
        //
        // For a dir resource, we still allow them in the url path, but those 
        // dynamic parameters must all
        // be *after* the regular tokens. This way, we can remove 
        // the dynamic part and the result is the prefix path to be used
        // by the server.
        //==========================================
        boolean splatParamFound = false;
        StringBuilder urlPathForServerBuilder = new StringBuilder("");
        String[] tokens = staticResource.getUrlPath().split("/");
        for(String token : tokens) {
            token = token.trim();
            if(staticResource.isFileResource()) {
                if(token.startsWith("${") || token.startsWith("*{")) {
                    throw new RuntimeException("A file resource path can't contain dynamic parameters. Use 'dir()' instead or " +
                                               "a regular route which won't be considered as a static resource.");
                }
            } else {
                if(StringUtils.isBlank(token)) {
                    continue;
                }

                if(token.startsWith("${")) {
                    throw new RuntimeException("A dir static resource path can't contains any standard dynamic parameter. It can only contain " +
                                               "a splat parameter, at the very end of the path : " + token);
                } else if(token.startsWith("*{")) {
                    splatParamFound = true;
                } else {
                    if(splatParamFound) {
                        throw new RuntimeException("A dir resource path can contain a splat parameter, only at the very end of the path! " +
                                                   "For example, this is invalid as a path : '/one/*{param1}/two', but this is valid : '/one/two/*{param1}'.");
                    } else {
                        urlPathForServerBuilder.append("/").append(token);
                    }
                }
            }
        }

        //==========================================
        // We remove the splat parameters from the
        // url path for the server.
        //==========================================
        String urlPathPrefixWithoutSplatParameter = staticResource.getUrlPath();
        if(splatParamFound) {

            urlPathPrefixWithoutSplatParameter = urlPathForServerBuilder.toString();
            if(staticResource.isDirResource()) {
                urlPathPrefixWithoutSplatParameter = urlPathForServerBuilder.toString();
                if(StringUtils.isBlank(urlPathPrefixWithoutSplatParameter)) {
                    urlPathPrefixWithoutSplatParameter = "/";
                }
            }

            IStaticResource<R> staticResourceNoDynParams =
                    getStaticResourceFactory().create(staticResource.getStaticResourceType(),
                                                      urlPathPrefixWithoutSplatParameter,
                                                      staticResource.getResourcePath(),
                                                      staticResource.getGenerator(),
                                                      staticResource.getCorsConfig());
            getServer().addStaticResourceToServe(staticResourceNoDynParams);
        } else {
            getServer().addStaticResourceToServe(staticResource);
        }

        //==========================================
        // If the resource is dynamic, add its generator 
        // as a route for the same path! 
        // We also add an "after" filter which will try to
        // automatically save the generated resource. The headers
        // shouln't have been sent for that to work!
        //==========================================
        IRoute<R> route = null;
        IHandler<R> saveResourceFilter = null;
        if(staticResource.getGenerator() != null) {

            if(staticResource.isFileResource()) {
                saveResourceFilter = new IHandler<R>() {

                    @Override
                    public void handle(R context) {
                        getSpincastFilters().saveGeneratedResource(context, staticResource.getResourcePath());
                    }
                };
            } else {

                //==========================================
                // We make the route listen on anything under
                // the specified path! The path may already contain
                // a splat param though!
                //==========================================
                if(!splatParamFound) {
                    urlPath = StringUtils.stripEnd(urlPath, "/") + DEFAULT_ROUTE_PATH;
                }

                final String urlPathPrefixWithoutDynamicParametersFinal = urlPathPrefixWithoutSplatParameter;
                saveResourceFilter = new IHandler<R>() {

                    @Override
                    public void handle(R context) {

                        String urlPathPrefix = StringUtils.stripStart(urlPathPrefixWithoutDynamicParametersFinal, "/");

                        String requestPath = context.request().getRequestPath();
                        requestPath = StringUtils.stripStart(requestPath, "/");

                        if(!requestPath.startsWith(urlPathPrefix)) {
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

                            if(!resourceToGeneratePath.startsWith(resourcesRoot)) {
                                throw new RuntimeException("The requestPath '" + resourceToGeneratePath +
                                                           "' should be inside the root resources folder : " + resourcesRoot);
                            }
                        } catch(Exception ex) {
                            throw SpincastStatics.runtimize(ex);
                        }

                        getSpincastFilters().saveGeneratedResource(context, resourceToGeneratePath);
                    }
                };
            }

            route = getRouteFactory().createRoute(null,
                                                  Sets.newHashSet(HttpMethod.GET),
                                                  urlPath,
                                                  Sets.newHashSet(RoutingType.NORMAL),
                                                  null,
                                                  staticResource.getGenerator(),
                                                  saveResourceFilter != null ? Arrays.asList(saveResourceFilter) : null,
                                                  Sets.newHashSet(0),
                                                  null);

            addRoute(route);
        }
    }

    @Override
    public void httpAuth(String pathPrefix, String realmName) {

        if(StringUtils.isBlank(realmName)) {
            throw new RuntimeException("The realm name can't be empty");
        }
        if(StringUtils.isBlank(pathPrefix)) {
            pathPrefix = "/";
        } else if(!pathPrefix.startsWith("/")) {
            pathPrefix = "/" + pathPrefix;
        }

        String[] tokens = pathPrefix.split("/");
        for(String token : tokens) {
            token = token.trim();
            if(token.startsWith("${") || token.startsWith("*{")) {
                throw new RuntimeException("The path prefix for an HTTP authenticated section can't contain " +
                                           "any dynamic parameters: " + token);
            }
        }

        getServer().createHttpAuthenticationRealm(pathPrefix, realmName);
    }

}

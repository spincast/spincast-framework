package org.spincast.plugins.routing;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.RedirectRuleBuilderFactory;
import org.spincast.core.routing.RouteBuilderFactory;
import org.spincast.core.routing.StaticResourceBuilderFactory;
import org.spincast.core.routing.StaticResourceFactory;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketRouteBuilderFactory;
import org.spincast.core.websocket.WebsocketRouteHandlerFactory;
import org.spincast.plugins.routing.utils.SpincastRoutingUtils;

import com.google.inject.Inject;

/**
 * A wrapper object for the dependencies required by SpincastRouter.
 * We inject this wrapper instead of injecting each individual dependency.
 * We do this because the SpincastRouter is made to be extended frequently
 * by developers and :
 * <ul>
 *     <li>
 *     We want it to be easily extended without having to inject too many
 *     dependencies in the child class.
 *     </li>
 *     <li>
 *     We want to keep using constructor injection instead of setter and field
 *     injection.
 *     </li>
 *     <li>
 *     By using a wrapper, we can add new dependencies to SpincastRouter
 *     without breaking the client classes.
 *     </li>
 * </ul>
 */
public class SpincastRouterDeps<R extends RequestContext<?>, W extends WebsocketContext<?>> {

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
    private final Server server;
    private final SpincastRoutingUtils spincastRoutingUtils;
    private final LocaleResolver localeResolver;

    /**
     * Constructor
     */
    @Inject
    public SpincastRouterDeps(SpincastRouterConfig spincastRouterConfig,
                              RouteFactory<R> routeFactory,
                              SpincastConfig spincastConfig,
                              Dictionary dictionary,
                              Server server,
                              SpincastFilters<R> spincastFilters,
                              RouteBuilderFactory<R, W> routeBuilderFactory,
                              RedirectRuleBuilderFactory<R, W> redirectRuleBuilderFactory,
                              StaticResourceBuilderFactory<R, W> staticResourceBuilderFactory,
                              RouteHandlerMatchFactory<R> routeHandlerMatchFactory,
                              StaticResourceFactory<R> staticResourceFactory,
                              WebsocketRouteBuilderFactory<R, W> websocketRouteBuilderFactory,
                              WebsocketRouteHandlerFactory<R, W> websocketRouteHandlerFactory,
                              SpincastRoutingUtils spincastRoutingUtils,
                              LocaleResolver localeResolver) {

        this.spincastRouterConfig = spincastRouterConfig;
        this.routeFactory = routeFactory;
        this.spincastConfig = spincastConfig;
        this.dictionary = dictionary;
        this.server = server;
        this.spincastFilters = spincastFilters;
        this.routeBuilderFactory = routeBuilderFactory;
        this.redirectRuleBuilderFactory = redirectRuleBuilderFactory;
        this.staticResourceBuilderFactory = staticResourceBuilderFactory;
        this.routeHandlerMatchFactory = routeHandlerMatchFactory;
        this.staticResourceFactory = staticResourceFactory;
        this.websocketRouteBuilderFactory = websocketRouteBuilderFactory;
        this.websocketRouteHandlerFactory = websocketRouteHandlerFactory;
        this.spincastRoutingUtils = spincastRoutingUtils;
        this.localeResolver = localeResolver;
    }

    public RouteHandlerMatchFactory<R> getRouteHandlerMatchFactory() {
        return this.routeHandlerMatchFactory;
    }

    public RouteBuilderFactory<R, W> getRouteBuilderFactory() {
        return this.routeBuilderFactory;
    }

    public RedirectRuleBuilderFactory<R, W> getRedirectRuleBuilderFactory() {
        return this.redirectRuleBuilderFactory;
    }

    public StaticResourceBuilderFactory<R, W> getStaticResourceBuilderFactory() {
        return this.staticResourceBuilderFactory;
    }

    public StaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    public SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    public RouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    public SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public SpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    public WebsocketRouteBuilderFactory<R, W> getWebsocketRouteBuilderFactory() {
        return this.websocketRouteBuilderFactory;
    }

    public WebsocketRouteHandlerFactory<R, W> getWebsocketRouteHandlerFactory() {
        return this.websocketRouteHandlerFactory;
    }

    public Server getServer() {
        return this.server;
    }

    public SpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    public LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

}

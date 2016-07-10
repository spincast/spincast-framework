package org.spincast.plugins.routing;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.IRedirectRuleBuilderFactory;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketRouteBuilderFactory;
import org.spincast.core.websocket.IWebsocketRouteHandlerFactory;

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
public class SpincastRouterDeps<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    private final IRouteHandlerMatchFactory<R> routeHandlerMatchFactory;
    private final IRouteBuilderFactory<R, W> routeBuilderFactory;
    private final IRedirectRuleBuilderFactory<R, W> redirectRuleBuilderFactory;
    private final IStaticResourceBuilderFactory<R, W> staticResourceBuilderFactory;
    private final IStaticResourceFactory<R> staticResourceFactory;
    private final ISpincastRouterConfig spincastRouterConfig;
    private final IRouteFactory<R> routeFactory;
    private final ISpincastConfig spincastConfig;
    private final ISpincastDictionary spincastDictionary;
    private final ISpincastFilters<R> spincastFilters;
    private final IWebsocketRouteBuilderFactory<R, W> websocketRouteBuilderFactory;
    private final IWebsocketRouteHandlerFactory<R, W> websocketRouteHandlerFactory;
    private final IServer server;

    /**
     * Constructor
     */
    @Inject
    public SpincastRouterDeps(ISpincastRouterConfig spincastRouterConfig,
                              IRouteFactory<R> routeFactory,
                              ISpincastConfig spincastConfig,
                              ISpincastDictionary spincastDictionary,
                              IServer server,
                              ISpincastFilters<R> spincastFilters,
                              IRouteBuilderFactory<R, W> routeBuilderFactory,
                              IRedirectRuleBuilderFactory<R, W> redirectRuleBuilderFactory,
                              IStaticResourceBuilderFactory<R, W> staticResourceBuilderFactory,
                              IRouteHandlerMatchFactory<R> routeHandlerMatchFactory,
                              IStaticResourceFactory<R> staticResourceFactory,
                              IWebsocketRouteBuilderFactory<R, W> websocketRouteBuilderFactory,
                              IWebsocketRouteHandlerFactory<R, W> websocketRouteHandlerFactory) {

        this.spincastRouterConfig = spincastRouterConfig;
        this.routeFactory = routeFactory;
        this.spincastConfig = spincastConfig;
        this.spincastDictionary = spincastDictionary;
        this.server = server;
        this.spincastFilters = spincastFilters;
        this.routeBuilderFactory = routeBuilderFactory;
        this.redirectRuleBuilderFactory = redirectRuleBuilderFactory;
        this.staticResourceBuilderFactory = staticResourceBuilderFactory;
        this.routeHandlerMatchFactory = routeHandlerMatchFactory;
        this.staticResourceFactory = staticResourceFactory;
        this.websocketRouteBuilderFactory = websocketRouteBuilderFactory;
        this.websocketRouteHandlerFactory = websocketRouteHandlerFactory;
    }

    public IRouteHandlerMatchFactory<R> getRouteHandlerMatchFactory() {
        return this.routeHandlerMatchFactory;
    }

    public IRouteBuilderFactory<R, W> getRouteBuilderFactory() {
        return this.routeBuilderFactory;
    }

    public IRedirectRuleBuilderFactory<R, W> getRedirectRuleBuilderFactory() {
        return this.redirectRuleBuilderFactory;
    }

    public IStaticResourceBuilderFactory<R, W> getStaticResourceBuilderFactory() {
        return this.staticResourceBuilderFactory;
    }

    public IStaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    public ISpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    public IRouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    public ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    public ISpincastDictionary getSpincastDictionary() {
        return this.spincastDictionary;
    }

    public ISpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    public IWebsocketRouteBuilderFactory<R, W> getWebsocketRouteBuilderFactory() {
        return this.websocketRouteBuilderFactory;
    }

    public IWebsocketRouteHandlerFactory<R, W> getWebsocketRouteHandlerFactory() {
        return this.websocketRouteHandlerFactory;
    }

    public IServer getServer() {
        return this.server;
    }

}

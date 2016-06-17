package org.spincast.plugins.routing;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.core.websocket.IWebsocketRouteBuilderFactory;
import org.spincast.core.websocket.IWebsocketRouteHandlerFactory;

import com.google.inject.Inject;

/**
 * An implementaiton of the IDefaultRouter interface, for easy
 * usage of the default version of the router.
 */
public class DefaultRouter extends SpincastRouter<IDefaultRequestContext, IDefaultWebsocketContext>
                           implements IDefaultRouter {

    @Inject
    public DefaultRouter(ISpincastRouterConfig spincastRouterConfig, IRouteFactory<IDefaultRequestContext> routeFactory,
                         ISpincastConfig spincastConfig, ISpincastDictionary spincastDictionary, IServer server,
                         ISpincastFilters<IDefaultRequestContext> spincastFilters,
                         IRouteBuilderFactory<IDefaultRequestContext, IDefaultWebsocketContext> routeBuilderFactory,
                         IStaticResourceBuilderFactory<IDefaultRequestContext, IDefaultWebsocketContext> staticResourceBuilderFactory,
                         IRouteHandlerMatchFactory<IDefaultRequestContext> routeHandlerMatchFactory,
                         IStaticResourceFactory<IDefaultRequestContext> staticResourceFactory,
                         IWebsocketRouteBuilderFactory<IDefaultRequestContext, IDefaultWebsocketContext> websocketRouteBuilderFactory,
                         IWebsocketRouteHandlerFactory<IDefaultRequestContext, IDefaultWebsocketContext> websocketHandshakerFactory) {
        super(spincastRouterConfig,
              routeFactory,
              spincastConfig,
              spincastDictionary,
              server,
              spincastFilters,
              routeBuilderFactory,
              staticResourceBuilderFactory,
              routeHandlerMatchFactory,
              staticResourceFactory,
              websocketRouteBuilderFactory,
              websocketHandshakerFactory);
        // nothing required
    }

}

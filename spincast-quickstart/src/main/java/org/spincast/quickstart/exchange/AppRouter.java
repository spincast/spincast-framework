package org.spincast.quickstart.exchange;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IWebsocketRouteBuilderFactory;
import org.spincast.core.websocket.IWebsocketRouteHandlerFactory;
import org.spincast.plugins.routing.IRouteFactory;
import org.spincast.plugins.routing.IRouteHandlerMatchFactory;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.plugins.routing.IStaticResourceFactory;
import org.spincast.plugins.routing.SpincastRouter;

import com.google.inject.Inject;

/**
 * The only purpose of this class is to make it
 * easier to inject the application specific router by using
 * "IAppRouter" instead of "IRouter&lt;IAppRequestContext, IAppWebsocketContext&gt;".
 */
public class AppRouter extends SpincastRouter<IAppRequestContext, IAppWebsocketContext>
                       implements IAppRouter {

    @Inject
    public AppRouter(ISpincastRouterConfig spincastRouterConfig, IRouteFactory<IAppRequestContext> routeFactory,
                     ISpincastConfig spincastConfig, ISpincastDictionary spincastDictionary, IServer server,
                     ISpincastFilters<IAppRequestContext> spincastFilters,
                     IRouteBuilderFactory<IAppRequestContext, IAppWebsocketContext> routeBuilderFactory,
                     IStaticResourceBuilderFactory<IAppRequestContext, IAppWebsocketContext> staticResourceBuilderFactory,
                     IRouteHandlerMatchFactory<IAppRequestContext> routeHandlerMatchFactory,
                     IStaticResourceFactory<IAppRequestContext> staticResourceFactory,
                     IWebsocketRouteBuilderFactory<IAppRequestContext, IAppWebsocketContext> websocketRouteBuilderFactory,
                     IWebsocketRouteHandlerFactory<IAppRequestContext, IAppWebsocketContext> websocketHandshakerFactory) {
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

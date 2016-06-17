package org.spincast.tests.varia;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.core.websocket.IWebsocketRouteBuilderFactory;
import org.spincast.core.websocket.IWebsocketRouteHandlerFactory;
import org.spincast.plugins.routing.IRouteFactory;
import org.spincast.plugins.routing.IRouteHandlerMatchFactory;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.plugins.routing.IStaticResourceFactory;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class CustomRouter2 extends SpincastRouter<IDefaultRequestContext, IDefaultWebsocketContext> {

    @Inject
    public CustomRouter2(ISpincastRouterConfig spincastRouterConfig,
                         IRouteFactory<IDefaultRequestContext> routeFactory,
                         ISpincastConfig spincastConfig,
                         ISpincastDictionary spincastDictionary,
                         IServer server,
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
    }

    @Inject
    public void addRoute(ISpincastConfig spincastConfig) {

        GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });
    }
}

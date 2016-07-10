package org.spincast.tests.varia;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IStaticResourceBuilder;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

public class CustomRouter extends SpincastRouter<IDefaultRequestContext, IDefaultWebsocketContext> {

    @Inject
    public CustomRouter(SpincastRouterDeps<IDefaultRequestContext, IDefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
    }

    @Override
    public IStaticResourceBuilder<IDefaultRequestContext> dir(String urlPathPrefix) {
        throw new RuntimeException(customMethod());
    }

    public String customMethod() {
        return "test123";
    }
}

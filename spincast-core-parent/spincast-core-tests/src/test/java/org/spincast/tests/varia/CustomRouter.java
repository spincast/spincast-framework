package org.spincast.tests.varia;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.StaticResourceBuilder;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

public class CustomRouter extends SpincastRouter<DefaultRequestContext, DefaultWebsocketContext> {

    @Inject
    public CustomRouter(SpincastRouterDeps<DefaultRequestContext, DefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
    }

    @Override
    public StaticResourceBuilder<DefaultRequestContext> dir(String urlPathPrefix) {
        throw new RuntimeException(customMethod());
    }

    public String customMethod() {
        return "test123";
    }
}

package org.spincast.tests.varia;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class CustomRouter2 extends SpincastRouter<DefaultRequestContext, DefaultWebsocketContext> {

    @Inject
    public CustomRouter2(SpincastRouterDeps<DefaultRequestContext, DefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
    }

    @Inject
    public void addRoute(SpincastConfig spincastConfig) {

        GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });
    }
}

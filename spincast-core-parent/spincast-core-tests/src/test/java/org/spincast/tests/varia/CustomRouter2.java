package org.spincast.tests.varia;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class CustomRouter2 extends SpincastRouter<IDefaultRequestContext, IDefaultWebsocketContext> {

    @Inject
    public CustomRouter2(SpincastRouterDeps<IDefaultRequestContext, IDefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
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

package org.spincast.website.exchange;

import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

public class AppRouterDefault extends SpincastRouter<AppRequestContext, DefaultWebsocketContext>
                              implements AppRouter {

    @Inject
    public AppRouterDefault(SpincastRouterDeps<AppRequestContext, DefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
        // nothing required
    }

}

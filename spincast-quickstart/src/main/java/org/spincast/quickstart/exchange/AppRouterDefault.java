package org.spincast.quickstart.exchange;

import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

/**
 * The only purpose of this class is to make it
 * easier to inject the application specific Router by using
 * "AppRouter" instead of "Router&lt;AppRequestContext, AppWebsocketContext&gt;".
 */
public class AppRouterDefault extends SpincastRouter<AppRequestContext, AppWebsocketContext>
                              implements AppRouter {

    @Inject
    public AppRouterDefault(SpincastRouterDeps<AppRequestContext, AppWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
    }

}

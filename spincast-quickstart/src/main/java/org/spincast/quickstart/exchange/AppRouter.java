package org.spincast.quickstart.exchange;

import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

/**
 * The only purpose of this class is to make it
 * easier to inject the application specific router by using
 * "IAppRouter" instead of "IRouter&lt;IAppRequestContext, IAppWebsocketContext&gt;".
 */
public class AppRouter extends SpincastRouter<IAppRequestContext, IAppWebsocketContext>
                       implements IAppRouter {

    @Inject
    public AppRouter(SpincastRouterDeps<IAppRequestContext, IAppWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
    }

}

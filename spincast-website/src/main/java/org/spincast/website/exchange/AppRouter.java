package org.spincast.website.exchange;

import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.plugins.routing.SpincastRouter;
import org.spincast.plugins.routing.SpincastRouterDeps;

import com.google.inject.Inject;

/**
 * The only purpose of this class is to make it
 * easier to inject the application specific router by using
 * "IAppRouter" instead of "IRouter&lt;IAppRequestContext&gt;".
 */
public class AppRouter extends SpincastRouter<IAppRequestContext, IDefaultWebsocketContext>
                       implements IAppRouter {

    @Inject
    public AppRouter(SpincastRouterDeps<IAppRequestContext, IDefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
        // nothing required
    }

}

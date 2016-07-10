package org.spincast.plugins.routing;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.websocket.IDefaultWebsocketContext;

import com.google.inject.Inject;

/**
 * An implementaiton of the IDefaultRouter interface, for easy
 * usage of the default version of the router.
 */
public class DefaultRouter extends SpincastRouter<IDefaultRequestContext, IDefaultWebsocketContext>
                           implements IDefaultRouter {

    @Inject
    public DefaultRouter(SpincastRouterDeps<IDefaultRequestContext, IDefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
        // nothing required
    }

}

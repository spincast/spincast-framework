package org.spincast.plugins.routing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;

import com.google.inject.Inject;

/**
 * An implementation of the DefaultRouter interface, for easy
 * usage of the default version of the Router.
 */
public class DefaultRouterDefault extends SpincastRouter<DefaultRequestContext, DefaultWebsocketContext>
                                  implements DefaultRouter {

    @Inject
    public DefaultRouterDefault(SpincastRouterDeps<DefaultRequestContext, DefaultWebsocketContext> spincastRouterDeps) {
        super(spincastRouterDeps);
        // nothing required 
    }

}

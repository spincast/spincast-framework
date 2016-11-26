package org.spincast.plugins.routing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;

/**
 * Unparameterized default router interface to easily use a router with the
 * default request context class.
 * 
 * It's easier to inject "DefaultRouter" than "Router&lt;DefaultRequestContext, DefaultWebsocketContext&gt;"
 * for a quick application.
 */
public interface DefaultRouter extends Router<DefaultRequestContext, DefaultWebsocketContext> {

    // nothing required
}

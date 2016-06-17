package org.spincast.plugins.routing;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IRouter;
import org.spincast.core.websocket.IDefaultWebsocketContext;

/**
 * Unparameterized default router interface to easily use a router with the
 * default request context class.
 * 
 * It's easier to inject "IDefaultRouter" than "IRouter&lt;IDefaultRequestContext, IDefaultWebsocketContext&gt;"
 * for a quick application.
 */
public interface IDefaultRouter extends IRouter<IDefaultRequestContext, IDefaultWebsocketContext> {

    // nothing required
}

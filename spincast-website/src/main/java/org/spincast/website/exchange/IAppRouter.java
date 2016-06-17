package org.spincast.website.exchange;

import org.spincast.core.routing.IRouter;
import org.spincast.core.websocket.IDefaultWebsocketContext;

/**
 * The only purpose of this interface is to make it
 * easier to inject the application specific router by using
 * "IAppRouter" instead of "IRouter&lt;IAppRequestContext, IDefaultWebsocketContext&gt;".
 */
public interface IAppRouter extends IRouter<IAppRequestContext, IDefaultWebsocketContext> {
    // nothing required
}

package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Handle to generate the path to redirect a route to.
 */
public interface RedirectHandler<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    public String handle(R context, String originalPath);

}

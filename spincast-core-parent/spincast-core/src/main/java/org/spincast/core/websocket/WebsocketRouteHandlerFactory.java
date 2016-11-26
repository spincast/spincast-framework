package org.spincast.core.websocket;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;

public interface WebsocketRouteHandlerFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    public Handler<R> createWebsocketRouteHandler(WebsocketRoute<R, W> websocketRoute);

}

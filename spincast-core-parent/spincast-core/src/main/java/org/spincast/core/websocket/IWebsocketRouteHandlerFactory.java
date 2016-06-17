package org.spincast.core.websocket;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;

public interface IWebsocketRouteHandlerFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    public IHandler<R> createWebsocketRouteHandler(IWebsocketRoute<R, W> websocketRoute);

}

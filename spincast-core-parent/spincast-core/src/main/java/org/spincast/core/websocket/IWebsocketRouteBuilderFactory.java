package org.spincast.core.websocket;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IRouter;

/**
 * Factory to create a WebSocket route builder.
 */
public interface IWebsocketRouteBuilderFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * Creates a route builder without using a router.
     * The {@link org.spincast.core.websocket.IWebsocketRouteBuilder#save(IWebsocketHandler) save()} method
     * will throw an expception if called. Only 
     * {@link org.spincast.core.websocket.IWebsocketRouteBuilder#create(IWebsocketHandler) create()}
     * will be available.
     */
    public IWebsocketRouteBuilder<R, W> create();

    /**
     * Creates a WebSocket route builder using the specified router.
     */
    public IWebsocketRouteBuilder<R, W> create(IRouter<R, W> router);

}

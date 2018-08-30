package org.spincast.core.websocket;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Router;

/**
 * Factory to create a WebSocket route builder.
 */
public interface WebsocketRouteBuilderFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * Creates a route builder without using a router.
     * The {@link org.spincast.core.websocket.WebsocketRouteBuilder#handle(IWebsocketHandler) save()} method
     * will throw an expception if called. Only 
     * {@link org.spincast.core.websocket.WebsocketRouteBuilder#validationSet(IWebsocketHandler) create()}
     * will be available.
     */
    public WebsocketRouteBuilder<R, W> create();

    /**
     * Creates a WebSocket route builder using the specified router.
     */
    public WebsocketRouteBuilder<R, W> create(Router<R, W> router);

}

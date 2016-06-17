package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;

/**
 * Factory to create a route builder.
 */
public interface IRouteBuilderFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * Creates a route builder without using a router.
     * The {@link org.spincast.core.routing.IRouteBuilder#save(IHandler) save()} method
     * will throw an expception if called. Only 
     * {@link org.spincast.core.routing.IRouteBuilder#create(IHandler) create()}
     * will be available.
     */
    public IRouteBuilder<R> create();

    /**
     * Creates a route builder using the specified router.
     */
    public IRouteBuilder<R> create(IRouter<R, W> router);
}

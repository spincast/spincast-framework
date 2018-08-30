package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Factory to create a route builder.
 */
public interface RouteBuilderFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * Creates a route builder without using a router.
     * The {@link org.spincast.core.routing.RouteBuilder#handle(Handler) save()} method
     * will throw an expception if called. Only 
     * {@link org.spincast.core.routing.RouteBuilder#create(Handler) create()}
     * will be available.
     */
    public RouteBuilder<R> create();

    /**
     * Creates a route builder using the specified router.
     */
    public RouteBuilder<R> create(Router<R, W> router);
}

package org.spincast.core.websocket;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;

/**
 * Builder for WebSocket routes.
 */
public interface WebsocketRouteBuilder<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * The path that trigger the beginning of that
     * HTTP to WebSocket connection.
     */
    public WebsocketRouteBuilder<R, W> path(String path);

    /**
     * The WebSocket route id.
     */
    public WebsocketRouteBuilder<R, W> id(String id);

    /**
     * Adds a before filter. Those will be run before the
     * WebSocket connection handshake is started.
     * <p>
     * Note that there are no "after" filters because once a
     * WebSocket connection is established, the HTTP one
     * is no more.
     * </p>
     */
    public WebsocketRouteBuilder<R, W> before(Handler<R> beforeFilter);

    /**
     * Skip a "before" filter for this WebSocket route 
     * ("after" filters are never run).
     * <p>
     * This is useful when you set a global filter but want to skip
     * it one a specific route only.
     * </p>
     */
    public WebsocketRouteBuilder<R, W> skip(String beforeFilterId);

    /**
     * Saves the WebSocket route on the router.
     */
    public void save(WebsocketController<R, W> websocketController);

    /**
     * Creates and returns the WebSocket route without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the route 
     * to the router at the end of the build process!
     */
    public WebsocketRoute<R, W> create(WebsocketController<R, W> websocketController);

}

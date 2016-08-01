package org.spincast.core.websocket;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;

/**
 * Builder for WebSocket routes.
 */
public interface IWebsocketRouteBuilder<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * The path that trigger the beginning of that
     * HTTP to WebSocket connection.
     */
    public IWebsocketRouteBuilder<R, W> path(String path);

    /**
     * The WebSocket route id.
     */
    public IWebsocketRouteBuilder<R, W> id(String id);

    /**
     * Adds a before filter. Those will be run before the
     * WebSocket connection handshake is started.
     * <p>
     * Note that there are no "after" filters because once a
     * WebSocket connection is established, the HTTP one
     * is no more.
     * </p>
     */
    public IWebsocketRouteBuilder<R, W> before(IHandler<R> beforeFilter);

    /**
     * Skip a "before" filter for this WebSocket route 
     * ("after" filters are never run).
     * <p>
     * This is useful when you set a global filter but want to skip
     * it one a specific route only.
     * </p>
     */
    public IWebsocketRouteBuilder<R, W> skip(String beforeFilterId);

    /**
     * Saves the WebSocket route on the router.
     */
    public void save(IWebsocketController<R, W> websocketController);

    /**
     * Creates and returns the WebSocket route without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the route 
     * to the router at the end of the build process!
     */
    public IWebsocketRoute<R, W> create(IWebsocketController<R, W> websocketController);

}

package org.spincast.core.websocket;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;

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
     * A WebSocket route may have multiple "classes"
     * to identify and group them.
     * <p>
     * For example, multiple routes may share the
     * same "account" class and this information
     * could be used to set a menu item as being
     * <em>active</em> on an HTML page.
     */
    public WebsocketRouteBuilder<R, W> classes(String... classes);

    /**
     * This sould only by called by *plugins*.
     * <p>
     * When this method is called, the resulting route won't
     * be remove by default when the
     * {@link Router#removeAllRoutes()} method is used. The
     * {@link Router#removeAllRoutes(boolean)} with <code>true</code>
     * will have to be called to actually remove it.
     * <p>
     * This is useful during development, when an hotreload mecanism
     * is used to reload the Router without
     * restarting the application, when the application routes changed.
     * By default only the routes for which the
     * {@link #isSpicastCoreRouteOrPluginRoute()}
     * method has been called would then be reloaded.
     */
    public WebsocketRouteBuilder<R, W> spicastCoreRouteOrPluginRoute();

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
    public void handle(WebsocketController<R, W> websocketController);

    /**
     * Creates and returns the WebSocket route without adding it to
     * the router.
     *
     * NOTE : use <code>handle(...)</code> instead to save the route
     * to the router at the end of the build process!
     */
    public WebsocketRoute<R, W> create(WebsocketController<R, W> websocketController);



}

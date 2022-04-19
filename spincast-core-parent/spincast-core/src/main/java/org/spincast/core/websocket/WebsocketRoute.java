package org.spincast.core.websocket;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;

/**
 * A WebSocket route.
 */
public interface WebsocketRoute<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * The WebSocket route id.
     */
    public String getId();

    /**
     * The WebSocket route's classes.
     */
    public Set<String> getClasses();

    /**
     * Is this a route added by Spincast itself
     * or by a plugin? Otherwise, the route is
     * considered as an application route.
     */
    public boolean isSpicastCoreRouteOrPluginRoute();

    /**
     * The WebSocket route path.
     */
    public String getPath();

    /**
     * The "before" filters, if any.
     */
    public List<Handler<R>> getBeforeFilters();

    /**
     * The WebSocket controller to use.
     */
    public WebsocketController<R, W> getWebsocketController();

    /**
     * The ids of the filters that should be skipped for this
     * route.
     */
    public Set<String> getFilterIdsToSkip();

}

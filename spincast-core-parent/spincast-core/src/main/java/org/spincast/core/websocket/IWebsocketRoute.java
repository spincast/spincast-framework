package org.spincast.core.websocket;

import java.util.List;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;

/**
 * A WebSocket route.
 */
public interface IWebsocketRoute<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * The WebSocket route id.
     */
    public String getId();

    /**
     * The WebSocket route path.
     */
    public String getPath();

    /**
     * The "before" filters, if any.
     */
    public List<IHandler<R>> getBeforeFilters();

    /**
     * The WebSocket controller to use.
     */
    public IWebsocketController<R, W> getWebsocketController();

}

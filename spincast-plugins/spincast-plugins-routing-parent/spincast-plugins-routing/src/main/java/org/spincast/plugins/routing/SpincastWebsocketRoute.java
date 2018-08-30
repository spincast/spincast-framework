package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketController;
import org.spincast.core.websocket.WebsocketRoute;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastWebsocketRoute<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                   implements WebsocketRoute<R, W> {

    private final String id;
    private final boolean spicastCoreRouteOrPluginRoute;
    private final String path;
    private final List<Handler<R>> beforeFilters;
    private final Set<String> filterIdsToSkip;
    private final WebsocketController<R, W> websocketController;

    /**
     * Constructor
     */
    @AssistedInject
    public SpincastWebsocketRoute(@Assisted("isSpicastCoreRouteOrPluginRoute") boolean spicastCoreRouteOrPluginRoute,
                                  @Assisted("id") @Nullable String id,
                                  @Assisted("path") String path,
                                  @Assisted("before") @Nullable List<Handler<R>> beforeFilters,
                                  @Assisted("filterIdsToSkip") @Nullable Set<String> filterIdsToSkip,
                                  @Assisted("controller") WebsocketController<R, W> websocketController) {
        this.spicastCoreRouteOrPluginRoute = spicastCoreRouteOrPluginRoute;
        this.id = id;
        this.path = path;
        this.beforeFilters = beforeFilters;
        this.filterIdsToSkip = filterIdsToSkip;
        this.websocketController = websocketController;
    }

    @Override
    public boolean isSpicastCoreRouteOrPluginRoute() {
        return this.spicastCoreRouteOrPluginRoute;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public List<Handler<R>> getBeforeFilters() {
        return this.beforeFilters;
    }

    @Override
    public Set<String> getFilterIdsToSkip() {
        return this.filterIdsToSkip;
    }

    @Override
    public WebsocketController<R, W> getWebsocketController() {
        return this.websocketController;
    }

    @Override
    public String toString() {
        return "Websocket route - " + (getId() != null ? (getId() + " - ") : "") + getPath();
    }

}

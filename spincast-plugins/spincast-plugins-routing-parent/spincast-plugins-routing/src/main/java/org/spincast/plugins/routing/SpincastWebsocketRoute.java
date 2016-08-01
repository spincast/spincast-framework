package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketController;
import org.spincast.core.websocket.IWebsocketRoute;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastWebsocketRoute<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                   implements IWebsocketRoute<R, W> {

    private final String id;
    private final String path;
    private final List<IHandler<R>> beforeFilters;
    private final Set<String> filterIdsToSkip;
    private final IWebsocketController<R, W> websocketController;

    /**
     * Constructor
     */
    @AssistedInject
    public SpincastWebsocketRoute(@Assisted("id") @Nullable String id,
                                  @Assisted("path") String path,
                                  @Assisted("before") @Nullable List<IHandler<R>> beforeFilters,
                                  @Assisted("filterIdsToSkip") @Nullable Set<String> filterIdsToSkip,
                                  @Assisted("controller") IWebsocketController<R, W> websocketController) {

        this.id = id;
        this.path = path;
        this.beforeFilters = beforeFilters;
        this.filterIdsToSkip = filterIdsToSkip;
        this.websocketController = websocketController;
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
    public List<IHandler<R>> getBeforeFilters() {
        return this.beforeFilters;
    }

    @Override
    public Set<String> getFilterIdsToSkip() {
        return this.filterIdsToSkip;
    }

    @Override
    public IWebsocketController<R, W> getWebsocketController() {
        return this.websocketController;
    }

    @Override
    public String toString() {
        return "Websocket route - " + (getId() != null ? (getId() + " - ") : "") + getPath();
    }

}

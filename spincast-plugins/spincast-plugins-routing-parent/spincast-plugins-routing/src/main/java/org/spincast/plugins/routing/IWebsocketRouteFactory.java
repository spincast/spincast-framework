package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketController;
import org.spincast.core.websocket.IWebsocketRoute;

import com.google.inject.assistedinject.Assisted;

public interface IWebsocketRouteFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    public IWebsocketRoute<R, W> createRoute(@Assisted("id") String id,
                                             @Assisted("path") String path,
                                             @Assisted("before") List<IHandler<R>> beforeFilters,
                                             @Assisted("filterIdsToSkip") Set<String> filterIdsToSkip,
                                             @Assisted("controller") IWebsocketController<R, W> websocketController);
}

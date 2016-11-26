package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketController;
import org.spincast.core.websocket.WebsocketRoute;

import com.google.inject.assistedinject.Assisted;

public interface WebsocketRouteFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    public WebsocketRoute<R, W> createRoute(@Assisted("id") String id,
                                            @Assisted("path") String path,
                                            @Assisted("before") List<Handler<R>> beforeFilters,
                                            @Assisted("filterIdsToSkip") Set<String> filterIdsToSkip,
                                            @Assisted("controller") WebsocketController<R, W> websocketController);
}

package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketController;
import org.spincast.core.websocket.IWebsocketRoute;
import org.spincast.core.websocket.IWebsocketRouteBuilder;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class WebsocketRouteBuilder<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                  implements IWebsocketRouteBuilder<R, W> {

    private final IWebsocketRouteFactory<R, W> websocketRouteFactory;
    private final IRouter<R, W> router;
    private String path;
    private String id;
    private List<IHandler<R>> beforeFilters;
    private IWebsocketController<R, W> websocketController;

    @AssistedInject
    public WebsocketRouteBuilder(IWebsocketRouteFactory<R, W> websocketRouteFactory) {
        this(null, websocketRouteFactory);
    }

    @AssistedInject
    public WebsocketRouteBuilder(@Assisted IRouter<R, W> router,
                                 IWebsocketRouteFactory<R, W> websocketRouteFactory) {
        this.router = router;
        this.websocketRouteFactory = websocketRouteFactory;
    }

    protected IWebsocketRouteFactory<R, W> getWebsocketRouteFactory() {
        return this.websocketRouteFactory;
    }

    protected IRouter<R, W> getRouter() {
        return this.router;
    }

    public String getPath() {
        return this.path;
    }

    public String getId() {
        return this.id;
    }

    public IWebsocketController<R, W> getWebsocketController() {
        return this.websocketController;
    }

    @Override
    public IWebsocketRouteBuilder<R, W> path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public IWebsocketRouteBuilder<R, W> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public IWebsocketRouteBuilder<R, W> before(IHandler<R> beforeFilter) {

        Objects.requireNonNull(beforeFilter, "beforeFilter can't be NULL");

        getBeforeFilters().add(beforeFilter);
        return this;
    }

    public List<IHandler<R>> getBeforeFilters() {
        if(this.beforeFilters == null) {
            this.beforeFilters = new ArrayList<IHandler<R>>();
        }
        return this.beforeFilters;
    }

    @Override
    public void save(IWebsocketController<R, W> websocketController) {

        if(getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the Websocket route!");
        }

        IWebsocketRoute<R, W> websocketRoute = create(websocketController);
        getRouter().addWebsocketRoute(websocketRoute);
    }

    @Override
    public IWebsocketRoute<R, W> create(IWebsocketController<R, W> websocketController) {

        this.websocketController = websocketController;

        IWebsocketRoute<R, W> websocketRoute = getWebsocketRouteFactory().createRoute(getId(),
                                                                                      getPath(),
                                                                                      getBeforeFilters(),
                                                                                      websocketController);
        return websocketRoute;
    }

}

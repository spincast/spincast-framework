package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketController;
import org.spincast.core.websocket.WebsocketRoute;
import org.spincast.core.websocket.WebsocketRouteBuilder;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class WebsocketRouteBuilderDefault<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                         implements WebsocketRouteBuilder<R, W> {

    private final WebsocketRouteFactory<R, W> websocketRouteFactory;
    private final Router<R, W> router;
    private String path;
    private String id;
    private List<Handler<R>> beforeFilters;
    private WebsocketController<R, W> websocketController;
    private Set<String> beforeFilterIdsToSkip;
    private boolean isSpicastCoreRouteOrPluginRoute = false;

    @AssistedInject
    public WebsocketRouteBuilderDefault(WebsocketRouteFactory<R, W> websocketRouteFactory) {
        this(null, websocketRouteFactory);
    }

    @AssistedInject
    public WebsocketRouteBuilderDefault(@Assisted Router<R, W> router,
                                        WebsocketRouteFactory<R, W> websocketRouteFactory) {
        this.router = router;
        this.websocketRouteFactory = websocketRouteFactory;
    }

    protected WebsocketRouteFactory<R, W> getWebsocketRouteFactory() {
        return this.websocketRouteFactory;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    public String getPath() {
        return this.path;
    }

    public String getId() {
        return this.id;
    }

    public boolean isSpicastCoreRouteOrPluginRoute() {
        return this.isSpicastCoreRouteOrPluginRoute;
    }

    public Set<String> getBeforeFilterIdsToSkip() {
        if (this.beforeFilterIdsToSkip == null) {
            this.beforeFilterIdsToSkip = new HashSet<>();
        }
        return this.beforeFilterIdsToSkip;
    }

    public WebsocketController<R, W> getWebsocketController() {
        return this.websocketController;
    }

    @Override
    public WebsocketRouteBuilder<R, W> path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public WebsocketRouteBuilder<R, W> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public WebsocketRouteBuilder<R, W> spicastCoreRouteOrPluginRoute() {
        this.isSpicastCoreRouteOrPluginRoute = true;
        return this;
    }

    @Override
    public WebsocketRouteBuilder<R, W> before(Handler<R> beforeFilter) {

        Objects.requireNonNull(beforeFilter, "beforeFilter can't be NULL");

        getBeforeFilters().add(beforeFilter);
        return this;
    }

    public List<Handler<R>> getBeforeFilters() {
        if (this.beforeFilters == null) {
            this.beforeFilters = new ArrayList<Handler<R>>();
        }
        return this.beforeFilters;
    }

    @Override
    public void handle(WebsocketController<R, W> websocketController) {

        if (getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the Websocket route!");
        }

        WebsocketRoute<R, W> websocketRoute = create(websocketController);
        getRouter().addWebsocketRoute(websocketRoute);
    }

    @Override
    public WebsocketRoute<R, W> create(WebsocketController<R, W> websocketController) {

        this.websocketController = websocketController;

        WebsocketRoute<R, W> websocketRoute = getWebsocketRouteFactory().createRoute(isSpicastCoreRouteOrPluginRoute(),
                                                                                     getId(),
                                                                                     getPath(),
                                                                                     getBeforeFilters(),
                                                                                     getBeforeFilterIdsToSkip(),
                                                                                     websocketController);
        return websocketRoute;
    }

    @Override
    public WebsocketRouteBuilder<R, W> skip(String beforeFilterId) {

        if (StringUtils.isBlank(beforeFilterId)) {
            throw new RuntimeException("The beforeFilterId can't be empty.");
        }

        getBeforeFilterIdsToSkip().add(beforeFilterId);

        return this;
    }

}

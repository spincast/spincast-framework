package org.spincast.plugins.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RouteHandlerMatch;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteHandlerMatchDefault<R extends RequestContext<?>> implements RouteHandlerMatch<R> {

    private final Route<R> sourceRoute;
    private final Handler<R> routeHandler;
    private final Map<String, String> params;

    private final int position;

    @AssistedInject
    public RouteHandlerMatchDefault(@Assisted Route<R> sourceRoute,
                                    @Assisted Handler<R> routeHandler,
                                    @Assisted Map<String, String> params,
                                    @Assisted int position) {

        Objects.requireNonNull(sourceRoute, "sourceRoute can't be NULL");
        this.sourceRoute = sourceRoute;

        Objects.requireNonNull(routeHandler, "routeHandler can't be NULL");
        this.routeHandler = routeHandler;

        Objects.requireNonNull(position, "position can't be NULL");
        this.position = position;

        if(params == null) {
            params = new HashMap<String, String>();
        }
        this.params = params;
    }

    @Override
    public Route<R> getSourceRoute() {
        return this.sourceRoute;
    }

    @Override
    public Handler<R> getHandler() {
        return this.routeHandler;
    }

    @Override
    public Map<String, String> getPathParams() {
        return this.params;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "Match for route: " + getSourceRoute();
    }

}

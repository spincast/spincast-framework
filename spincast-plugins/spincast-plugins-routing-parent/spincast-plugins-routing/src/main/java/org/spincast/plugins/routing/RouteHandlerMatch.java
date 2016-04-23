package org.spincast.plugins.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.IRouteHandlerMatch;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteHandlerMatch<R extends IRequestContext<?>> implements IRouteHandlerMatch<R> {

    private final IRoute<R> sourceRoute;
    private final IHandler<R> routeHandler;
    private final Map<String, String> params;

    private final int position;

    @AssistedInject
    public RouteHandlerMatch(@Assisted IRoute<R> sourceRoute,
                             @Assisted IHandler<R> routeHandler,
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
    public IRoute<R> getSourceRoute() {
        return this.sourceRoute;
    }

    @Override
    public IHandler<R> getHandler() {
        return this.routeHandler;
    }

    @Override
    public Map<String, String> getParameters() {
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

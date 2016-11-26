package org.spincast.plugins.routing;

import java.util.Map;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RouteHandlerMatch;

public interface RouteHandlerMatchFactory<R extends RequestContext<?>> {

    public RouteHandlerMatch<R> create(Route<R> sourceRoute,
                                       Handler<R> routeHandler,
                                       Map<String, String> params,
                                       int position);
}

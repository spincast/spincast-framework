package org.spincast.plugins.routing;

import java.util.Map;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.IRouteHandlerMatch;

public interface IRouteHandlerMatchFactory<R extends IRequestContext<?>> {

    public IRouteHandlerMatch<R> create(IRoute<R> sourceRoute,
                                        IHandler<R> routeHandler,
                                        Map<String, String> params,
                                        int position);
}

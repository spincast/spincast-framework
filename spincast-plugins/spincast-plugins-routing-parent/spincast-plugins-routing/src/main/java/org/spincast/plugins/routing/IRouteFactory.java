package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.RoutingType;

import com.google.inject.assistedinject.Assisted;

public interface IRouteFactory<R extends IRequestContext<?>> {

    public IRoute<R> createRoute(@Assisted("id") String id,
                                 Set<HttpMethod> httpMethods,
                                 @Assisted("path") String path,
                                 Set<RoutingType> routingTypes,
                                 @Assisted("before") List<IHandler<R>> beforeFilters,
                                 @Assisted("main") IHandler<R> mainHandler,
                                 @Assisted("after") List<IHandler<R>> afterFilters,
                                 Set<Integer> positions,
                                 Set<String> acceptedContextTypes);
}

package org.spincast.plugins.routing;

import java.util.List;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RoutingType;

import com.google.inject.assistedinject.Assisted;

public interface RouteFactory<R extends RequestContext<?>> {

    public Route<R> createRoute(@Assisted("id") String id,
                                 @Assisted("httpMethods") Set<HttpMethod> httpMethods,
                                 @Assisted("path") String path,
                                 @Assisted("routingTypes") Set<RoutingType> routingTypes,
                                 @Assisted("before") List<Handler<R>> beforeFilters,
                                 @Assisted("main") Handler<R> mainHandler,
                                 @Assisted("after") List<Handler<R>> afterFilters,
                                 @Assisted("positions") Set<Integer> positions,
                                 @Assisted("acceptedContentTypes") Set<String> acceptedContentTypes,
                                 @Assisted("filterIdsToSkip") Set<String> filterIdsToSkip);
}

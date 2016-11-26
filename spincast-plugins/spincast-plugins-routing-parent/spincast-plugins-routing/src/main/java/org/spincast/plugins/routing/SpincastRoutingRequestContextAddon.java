package org.spincast.plugins.routing;

import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.routing.RoutingResult;

import com.google.inject.Inject;

public class SpincastRoutingRequestContextAddon<R extends RequestContext<R>>
                                               implements RoutingRequestContextAddon<R> {

    private final R requestContext;

    @Inject
    public SpincastRoutingRequestContextAddon(R requestContext) {
        this.requestContext = requestContext;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected RouteHandlerMatch<R> getCurrentRouteMatch() {

        @SuppressWarnings("unchecked")
        RouteHandlerMatch<R> match =
                (RouteHandlerMatch<R>)getRequestContext().variables()
                                                         .get(SpincastConstants.RequestScopedVariables.ROUTE_HANDLER_MATCH);
        return match;
    }

    @Override
    public int getPosition() {
        return getCurrentRouteMatch().getPosition();
    }

    @Override
    public RouteHandlerMatch<R> getCurrentRouteHandlerMatch() {
        return getCurrentRouteMatch();
    }

    @Override
    public RoutingResult<R> getRoutingResult() {

        @SuppressWarnings("unchecked")
        RoutingResult<R> routingResult =
                (RoutingResult<R>)getRequestContext().variables()
                                                     .get(SpincastConstants.RequestScopedVariables.ROUTING_RESULT);
        return routingResult;
    }

    @Override
    public boolean isNotFoundRoute() {
        Boolean isNotFoundRoute =
                getRequestContext().variables().get(SpincastConstants.RequestScopedVariables.IS_NOT_FOUND_ROUTE,
                                                    Boolean.class);
        return isNotFoundRoute != null && isNotFoundRoute;
    }

    @Override
    public boolean isExceptionRoute() {
        Boolean isExceptionHandling =
                getRequestContext().variables().get(SpincastConstants.RequestScopedVariables.IS_EXCEPTION_HANDLING,
                                                    Boolean.class);
        return isExceptionHandling != null && isExceptionHandling;
    }

    @Override
    public boolean isForwarded() {

        String forwardedUrl =
                getRequestContext().variables().getAsString(SpincastConstants.RequestScopedVariables.FORWARD_ROUTE_URL);
        return forwardedUrl != null;
    }

}

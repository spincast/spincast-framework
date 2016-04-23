package org.spincast.plugins.routing;

import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.routing.IRoutingResult;

import com.google.inject.Inject;

public class SpincastRoutingRequestContextAddon<R extends IRequestContext<R>>
                                               implements IRoutingRequestContextAddon<R> {

    private final R requestContext;

    @Inject
    public SpincastRoutingRequestContextAddon(R requestContext) {
        this.requestContext = requestContext;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected IRouteHandlerMatch<R> getCurrentRouteMatch() {

        @SuppressWarnings("unchecked")
        IRouteHandlerMatch<R> match =
                (IRouteHandlerMatch<R>)getRequestContext().variables()
                                                          .get(SpincastConstants.RequestScopedVariables.ROUTE_HANDLER_MATCH);
        return match;
    }

    @Override
    public int getPosition() {
        return getCurrentRouteMatch().getPosition();
    }

    @Override
    public IRouteHandlerMatch<R> getCurrentRouteHandlerMatch() {
        return getCurrentRouteMatch();
    }

    @Override
    public IRoutingResult<R> getRoutingResult() {

        @SuppressWarnings("unchecked")
        IRoutingResult<R> routingResult =
                (IRoutingResult<R>)getRequestContext().variables()
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

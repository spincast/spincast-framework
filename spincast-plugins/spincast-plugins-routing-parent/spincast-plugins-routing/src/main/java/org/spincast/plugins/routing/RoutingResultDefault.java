package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.List;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.RoutingResult;

public class RoutingResultDefault<R extends RequestContext<?>> implements RoutingResult<R> {

    private final List<RouteHandlerMatch<R>> routeHandlerMatches;

    public RoutingResultDefault(List<RouteHandlerMatch<R>> routeHandlerMatches) {
        if(routeHandlerMatches == null) {
            routeHandlerMatches = new ArrayList<RouteHandlerMatch<R>>();
        }
        this.routeHandlerMatches = routeHandlerMatches;
    }

    @Override
    public List<RouteHandlerMatch<R>> getRouteHandlerMatches() {
        return this.routeHandlerMatches;
    }

    @Override
    public String toString() {

        List<RouteHandlerMatch<R>> routeHandlerMatches = getRouteHandlerMatches();
        if(routeHandlerMatches.size() == 0) {
            return "No match";
        }

        for(RouteHandlerMatch<R> routeHandlerMatch : routeHandlerMatches) {
            if(routeHandlerMatch.getPosition() == 0) {
                return "Route matched : " + routeHandlerMatch.getHandler();
            }
        }
        return "Filters only";
    }

    @Override
    public RouteHandlerMatch<R> getMainRouteHandlerMatch() {

        List<RouteHandlerMatch<R>> routeHandlerMatches = getRouteHandlerMatches();
        if(routeHandlerMatches != null) {
            for(RouteHandlerMatch<R> routeHandlerMatch : routeHandlerMatches) {
                if(routeHandlerMatch != null &&
                   routeHandlerMatch.getPosition() == 0) {
                    return routeHandlerMatch;
                }
            }
        }
        return null;
    }

}

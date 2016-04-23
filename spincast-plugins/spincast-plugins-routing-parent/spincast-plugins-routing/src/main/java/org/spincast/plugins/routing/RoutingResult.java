package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.List;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRoutingResult;

public class RoutingResult<R extends IRequestContext<?>> implements IRoutingResult<R> {

    private final List<IRouteHandlerMatch<R>> routeHandlerMatches;

    public RoutingResult(List<IRouteHandlerMatch<R>> routeHandlerMatches) {
        if(routeHandlerMatches == null) {
            routeHandlerMatches = new ArrayList<IRouteHandlerMatch<R>>();
        }
        this.routeHandlerMatches = routeHandlerMatches;
    }

    @Override
    public List<IRouteHandlerMatch<R>> getRouteHandlerMatches() {
        return this.routeHandlerMatches;
    }

    @Override
    public String toString() {

        List<IRouteHandlerMatch<R>> routeHandlerMatches = getRouteHandlerMatches();
        if(routeHandlerMatches.size() == 0) {
            return "No match";
        }

        for(IRouteHandlerMatch<R> routeHandlerMatch : routeHandlerMatches) {
            if(routeHandlerMatch.getPosition() == 0) {
                return "Route matched : " + routeHandlerMatch.getHandler();
            }
        }
        return "Filters only";
    }

    @Override
    public IRouteHandlerMatch<R> getMainRouteHandlerMatch() {

        List<IRouteHandlerMatch<R>> routeHandlerMatches = getRouteHandlerMatches();
        if(routeHandlerMatches != null) {
            for(IRouteHandlerMatch<R> routeHandlerMatch : routeHandlerMatches) {
                if(routeHandlerMatch != null &&
                   routeHandlerMatch.getPosition() == 0) {
                    return routeHandlerMatch;
                }
            }
        }
        return null;
    }

}

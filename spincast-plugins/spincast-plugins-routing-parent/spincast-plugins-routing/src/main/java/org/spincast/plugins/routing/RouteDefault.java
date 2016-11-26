package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RoutingType;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteDefault<R extends RequestContext<?>> implements Route<R> {

    private final String id;
    private final String path;
    private final Set<HttpMethod> httpMethods;
    private final Set<String> acceptedContentTypes;
    private final Set<RoutingType> routingTypes;
    private final List<Handler<R>> beforeFilters;
    private final Handler<R> mainHandler;
    private final List<Handler<R>> afterFilters;
    private final List<Integer> positions;
    private final Set<String> filterIdsToSkip;

    /** 
     * Constructor
     */
    @AssistedInject
    public RouteDefault(@Assisted("id") @Nullable String id,
                        @Assisted("httpMethods") Set<HttpMethod> httpMethods,
                        @Assisted("path") String path,
                        @Assisted("routingTypes") Set<RoutingType> routingTypes,
                        @Assisted("before") @Nullable List<Handler<R>> beforeFilters,
                        @Assisted("main") Handler<R> mainHandler,
                        @Assisted("after") @Nullable List<Handler<R>> afterFilters,
                        @Assisted("positions") Set<Integer> positions,
                        @Assisted("acceptedContentTypes") @Nullable Set<String> acceptedContentTypes,
                        @Assisted("filterIdsToSkip") @Nullable Set<String> filterIdsToSkip) {
        this.id = id;

        this.positions = new ArrayList<Integer>(positions);
        Collections.sort(this.positions);

        this.httpMethods = httpMethods;
        this.path = path;
        this.routingTypes = routingTypes;

        if(beforeFilters == null) {
            beforeFilters = new ArrayList<Handler<R>>();
        }
        this.beforeFilters = beforeFilters;
        this.mainHandler = mainHandler;

        if(afterFilters == null) {
            afterFilters = new ArrayList<Handler<R>>();
        }
        this.afterFilters = afterFilters;

        if(acceptedContentTypes == null) {
            acceptedContentTypes = new HashSet<>();
        }

        this.acceptedContentTypes = new HashSet<String>();
        for(String acceptedContentType : acceptedContentTypes) {
            if(acceptedContentType != null) {
                this.acceptedContentTypes.add(acceptedContentType.toLowerCase());
            }
        }

        if(filterIdsToSkip == null) {
            filterIdsToSkip = new HashSet<String>();
        }
        this.filterIdsToSkip = filterIdsToSkip;
    }

    @Override
    public Set<HttpMethod> getHttpMethods() {
        return this.httpMethods;
    }

    @Override
    public Set<String> getAcceptedContentTypes() {
        return this.acceptedContentTypes;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Set<RoutingType> getRoutingTypes() {
        return this.routingTypes;
    }

    @Override
    public List<Handler<R>> getBeforeFilters() {
        return this.beforeFilters;
    }

    @Override
    public Handler<R> getMainHandler() {
        return this.mainHandler;
    }

    @Override
    public List<Handler<R>> getAfterFilters() {
        return this.afterFilters;
    }

    @Override
    public List<Integer> getPositions() {
        return this.positions;
    }

    @Override
    public Set<String> getFilterIdsToSkip() {
        return this.filterIdsToSkip;
    }

    @Override
    public String toString() {
        return "[" + StringUtils.join(getPositions(), ",") + "] " + getPath();
    }

}

package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.routing.StaticResource;

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteDefault<R extends RequestContext<?>> implements Route<R> {

    private final String id;
    private final boolean isWebsocketRoute;
    private final boolean isResourceRoute;
    private final StaticResource<R> staticResource;
    private final boolean spicastCoreRouteOrPluginRoute;
    private final String path;
    private final Set<HttpMethod> httpMethods;
    private final Set<String> acceptedContentTypes;
    private final Set<RoutingType> routingTypes;
    private final List<Handler<R>> beforeFilters;
    private final Handler<R> mainHandler;
    private final List<Handler<R>> afterFilters;
    private final int position;
    private final Set<String> filterIdsToSkip;
    private final boolean skipResourcesRequests;
    private final Object specs;
    private final List<Object> specsParameters;
    private final boolean specsIgnore;
    private final Set<String> classes;

    /**
     * Constructor
     */
    @AssistedInject
    public RouteDefault(@Assisted("id") @Nullable String id,
                        @Assisted("isWebsocketRoute") boolean isWebsocketRoute,
                        @Assisted("isResourceRoute") boolean isResourceRoute,
                        @Assisted("staticResource") @Nullable StaticResource<R> staticResource,
                        @Assisted("isSpicastCoreRouteOrPluginRoute") boolean spicastCoreRouteOrPluginRoute,
                        @Assisted("httpMethods") Set<HttpMethod> httpMethods,
                        @Assisted("path") String path,
                        @Assisted("routingTypes") Set<RoutingType> routingTypes,
                        @Assisted("before") @Nullable List<Handler<R>> beforeFilters,
                        @Assisted("main") Handler<R> mainHandler,
                        @Assisted("after") @Nullable List<Handler<R>> afterFilters,
                        @Assisted("position") int position,
                        @Assisted("acceptedContentTypes") @Nullable Set<String> acceptedContentTypes,
                        @Assisted("filterIdsToSkip") @Nullable Set<String> filterIdsToSkip,
                        @Assisted("skipResources") boolean skipResources,
                        @Assisted("specs") @Nullable Object specs,
                        @Assisted("specsParameters") @Nullable Object[] specsParameters,
                        @Assisted("specsIgnore") boolean specsIgnore,
                        @Assisted("classes") Set<String> classes) {
        this.id = id;
        this.isWebsocketRoute = isWebsocketRoute;
        this.isResourceRoute = isResourceRoute;
        this.staticResource = staticResource;
        this.spicastCoreRouteOrPluginRoute = spicastCoreRouteOrPluginRoute;
        this.position = position;
        this.httpMethods = httpMethods;
        this.path = path;
        this.routingTypes = routingTypes;

        if (beforeFilters == null) {
            beforeFilters = new ArrayList<Handler<R>>();
        }
        this.beforeFilters = beforeFilters;
        this.mainHandler = mainHandler;

        if (afterFilters == null) {
            afterFilters = new ArrayList<Handler<R>>();
        }
        this.afterFilters = afterFilters;

        if (acceptedContentTypes == null) {
            acceptedContentTypes = new HashSet<>();
        }

        this.acceptedContentTypes = new HashSet<String>();
        for (String acceptedContentType : acceptedContentTypes) {
            if (acceptedContentType != null) {
                this.acceptedContentTypes.add(acceptedContentType.toLowerCase());
            }
        }

        if (filterIdsToSkip == null) {
            filterIdsToSkip = new HashSet<String>();
        }
        this.filterIdsToSkip = filterIdsToSkip;
        this.skipResourcesRequests = skipResources;
        this.specs = specs;
        this.specsParameters = specsParameters != null ? Lists.newArrayList(specsParameters) : new ArrayList<Object>();
        this.specsIgnore = specsIgnore;
        this.classes = classes;
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
    public Set<String> getClasses() {
        return this.classes;
    }

    @Override
    public boolean isWebsocketRoute() {
        return this.isWebsocketRoute;
    }

    @Override
    public boolean isStaticResourceRoute() {
        return this.isResourceRoute;
    }

    @Override
    public StaticResource<R> getStaticResource() {
        return this.staticResource;
    }

    @Override
    public boolean isSpicastCoreRouteOrPluginRoute() {
        return this.spicastCoreRouteOrPluginRoute;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public boolean isSkipResourcesRequests() {
        return this.skipResourcesRequests;
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
    public int getPosition() {
        return this.position;
    }

    @Override
    public Set<String> getFilterIdsToSkip() {
        return this.filterIdsToSkip;
    }

    @Override
    public Object getSpecs() {
        return this.specs;
    }

    @Override
    public List<Object> getSpecsParameters() {
        return this.specsParameters;
    }

    @Override
    public boolean isSpecsIgnore() {
        return this.specsIgnore;
    }

    @Override
    public String toString() {
        return "[" + getPosition() + "] " +
               Arrays.toString(getHttpMethods().toArray(new HttpMethod[getHttpMethods().size()])) + " " + getPath();
    }



}

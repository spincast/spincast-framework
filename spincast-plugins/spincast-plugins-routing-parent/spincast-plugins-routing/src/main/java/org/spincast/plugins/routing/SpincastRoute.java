package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.RoutingType;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastRoute<R extends IRequestContext<?>> implements IRoute<R> {

    private final String id;
    private final String path;
    private final Set<HttpMethod> httpMethods;
    private final Set<String> acceptedContentTypes;
    private final Set<RoutingType> routingTypes;
    private final List<IHandler<R>> beforeFilters;
    private final IHandler<R> mainHandler;
    private final List<IHandler<R>> afterFilters;
    private final List<Integer> positions;

    /**
     * Constructor
     */
    @AssistedInject
    public SpincastRoute(@Assisted("id") @Nullable String id,
                         @Assisted Set<HttpMethod> httpMethods,
                         @Assisted("path") String path,
                         @Assisted Set<RoutingType> routingTypes,
                         @Assisted("before") @Nullable List<IHandler<R>> beforeFilters,
                         @Assisted("main") IHandler<R> mainHandler,
                         @Assisted("after") @Nullable List<IHandler<R>> afterFilters,
                         @Assisted Set<Integer> positions,
                         @Assisted @Nullable Set<String> acceptedContentTypes) {
        this.id = id;

        this.positions = new ArrayList<Integer>(positions);
        Collections.sort(this.positions);

        this.httpMethods = httpMethods;
        this.path = path;
        this.routingTypes = routingTypes;

        if(beforeFilters == null) {
            beforeFilters = new ArrayList<IHandler<R>>();
        }
        this.beforeFilters = beforeFilters;
        this.mainHandler = mainHandler;

        if(afterFilters == null) {
            afterFilters = new ArrayList<IHandler<R>>();
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
    public List<IHandler<R>> getBeforeFilters() {
        return this.beforeFilters;
    }

    @Override
    public IHandler<R> getMainHandler() {
        return this.mainHandler;
    }

    @Override
    public List<IHandler<R>> getAfterFilters() {
        return this.afterFilters;
    }

    @Override
    public List<Integer> getPositions() {
        return this.positions;
    }

    @Override
    public String toString() {
        return "[" + StringUtils.join(getPositions(), ",") + "] " + getPath();
    }

}

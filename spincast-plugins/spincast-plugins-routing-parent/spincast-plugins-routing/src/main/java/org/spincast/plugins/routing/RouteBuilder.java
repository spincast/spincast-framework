package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.IRouteBuilder;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.utils.ContentTypeDefaults;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteBuilder<R extends IRequestContext<?>> implements IRouteBuilder<R> {

    protected final Logger logger = LoggerFactory.getLogger(RouteBuilder.class);

    private final IRouter<R> router;
    private final IRouteFactory<R> routeFactory;
    private final ISpincastRouterConfig spincastRouterConfig;
    private final ISpincastFilters<R> spincastFilters;

    private Set<HttpMethod> httpMethods;
    private String id = null;
    private String path = null;
    private Set<Integer> positions;
    private Set<RoutingType> routingTypes;
    private List<IHandler<R>> beforeFilters;
    private IHandler<R> mainHandler;
    private List<IHandler<R>> afterFilters;
    private Set<String> acceptedContentTypes;

    @AssistedInject
    public RouteBuilder(IRouteFactory<R> routeFactory,
                        ISpincastRouterConfig spincastRouterConfig,
                        ISpincastFilters<R> spincastFilters) {
        this(null, routeFactory, spincastRouterConfig, spincastFilters);
    }

    @AssistedInject
    public RouteBuilder(@Assisted IRouter<R> router,
                        IRouteFactory<R> routeFactory,
                        ISpincastRouterConfig spincastRouterConfig,
                        ISpincastFilters<R> spincastFilters) {
        this.router = router;
        this.routeFactory = routeFactory;
        this.spincastRouterConfig = spincastRouterConfig;
        this.spincastFilters = spincastFilters;
    }

    protected IRouter<R> getRouter() {
        return this.router;
    }

    protected IRouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    protected ISpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected ISpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    public String getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public Set<Integer> getPositions() {
        if(this.positions == null) {
            this.positions = new HashSet<Integer>();
        }
        return this.positions;
    }

    public Set<RoutingType> getRoutingTypes() {
        if(this.routingTypes == null) {
            this.routingTypes = new HashSet<>();
        }
        return this.routingTypes;
    }

    public Set<HttpMethod> getHttpMethods() {
        if(this.httpMethods == null) {
            this.httpMethods = new HashSet<>();
        }
        return this.httpMethods;
    }

    public List<IHandler<R>> getBeforeFilters() {
        if(this.beforeFilters == null) {
            this.beforeFilters = new ArrayList<IHandler<R>>();
        }
        return this.beforeFilters;
    }

    public List<IHandler<R>> getAfterFilters() {
        if(this.afterFilters == null) {
            this.afterFilters = new ArrayList<IHandler<R>>();
        }
        return this.afterFilters;
    }

    public IHandler<R> getMainHandler() {
        return this.mainHandler;
    }

    public Set<String> getAcceptedContentTypes() {
        if(this.acceptedContentTypes == null) {
            this.acceptedContentTypes = new HashSet<>();
        }
        return this.acceptedContentTypes;
    }

    @Override
    public IRouteBuilder<R> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public IRouteBuilder<R> pos(int position) {
        getPositions().add(position);
        return this;
    }

    @Override
    public IRouteBuilder<R> allRoutingTypes() {

        getRoutingTypes().addAll(Sets.newHashSet(RoutingType.values()));
        return this;
    }

    @Override
    public IRouteBuilder<R> found() {
        getRoutingTypes().add(RoutingType.NORMAL);
        return this;
    }

    @Override
    public IRouteBuilder<R> notFound() {
        getRoutingTypes().add(RoutingType.NOT_FOUND);
        return this;
    }

    @Override
    public IRouteBuilder<R> exception() {
        getRoutingTypes().add(RoutingType.EXCEPTION);
        return this;
    }

    @Override
    public IRouteBuilder<R> before(IHandler<R> beforeFilter) {

        Objects.requireNonNull(beforeFilter, "beforeFilter can't be NULL");

        getBeforeFilters().add(beforeFilter);
        return this;
    }

    @Override
    public IRouteBuilder<R> after(IHandler<R> afterFilter) {

        Objects.requireNonNull(afterFilter, "afterFilter can't be NULL");

        getAfterFilters().add(afterFilter);
        return this;
    }

    @Override
    public IRouteBuilder<R> acceptAsString(String... acceptedContentTypes) {
        getAcceptedContentTypes().addAll(Sets.newHashSet(acceptedContentTypes));
        return this;
    }

    @Override
    public IRouteBuilder<R> acceptAsString(Set<String> acceptedContentTypes) {

        Objects.requireNonNull(acceptedContentTypes, "acceptedContentTypes can't be NULL");

        getAcceptedContentTypes().addAll(acceptedContentTypes);
        return this;
    }

    @Override
    public IRouteBuilder<R> accept(ContentTypeDefaults... acceptedContentTypes) {

        if(acceptedContentTypes != null) {
            for(ContentTypeDefaults contentTypeDefault : acceptedContentTypes) {
                getAcceptedContentTypes().addAll(contentTypeDefault.getVariations());
            }
        }
        return this;
    }

    @Override
    public IRouteBuilder<R> accept(Set<ContentTypeDefaults> acceptedContentTypes) {

        Objects.requireNonNull(acceptedContentTypes, "acceptedContentTypes can't be NULL");

        for(ContentTypeDefaults contentTypeDefault : acceptedContentTypes) {
            accept(contentTypeDefault);
        }
        return this;
    }

    @Override
    public IRouteBuilder<R> html() {
        getAcceptedContentTypes().add(ContentTypeDefaults.HTML.getMainVariation());
        return this;
    }

    @Override
    public IRouteBuilder<R> json() {
        getAcceptedContentTypes().add(ContentTypeDefaults.JSON.getMainVariation());
        return this;
    }

    @Override
    public IRouteBuilder<R> xml() {
        getAcceptedContentTypes().add(ContentTypeDefaults.XML.getMainVariation());
        return this;
    }

    @Override
    public IRouteBuilder<R> path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public IRouteBuilder<R> GET() {
        getHttpMethods().add(HttpMethod.GET);
        return this;
    }

    @Override
    public IRouteBuilder<R> POST() {
        getHttpMethods().add(HttpMethod.POST);
        return this;
    }

    @Override
    public IRouteBuilder<R> PUT() {
        getHttpMethods().add(HttpMethod.PUT);
        return this;
    }

    @Override
    public IRouteBuilder<R> DELETE() {
        getHttpMethods().add(HttpMethod.DELETE);
        return this;
    }

    @Override
    public IRouteBuilder<R> OPTIONS() {
        getHttpMethods().add(HttpMethod.OPTIONS);
        return this;
    }

    @Override
    public IRouteBuilder<R> TRACE() {
        getHttpMethods().add(HttpMethod.TRACE);
        return this;
    }

    @Override
    public IRouteBuilder<R> HEAD() {
        getHttpMethods().add(HttpMethod.HEAD);
        return this;
    }

    @Override
    public IRouteBuilder<R> PATCH() {
        getHttpMethods().add(HttpMethod.PATCH);
        return this;
    }

    @Override
    public IRouteBuilder<R> ALL() {
        getHttpMethods().addAll(Sets.newHashSet(HttpMethod.values()));
        return this;
    }

    @Override
    public IRouteBuilder<R> SOME(Set<HttpMethod> httpMethods) {
        getHttpMethods().addAll(httpMethods);
        return this;
    }

    @Override
    public IRouteBuilder<R> SOME(HttpMethod... httpMethods) {
        getHttpMethods().addAll(Sets.newHashSet(httpMethods));
        return this;
    }

    @Override
    public void save(IHandler<R> mainHandler) {

        if(getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the route!");
        }

        IRoute<R> route = create(mainHandler);
        getRouter().addRoute(route);
    }

    @Override
    public IRoute<R> create(IHandler<R> mainHandler) {

        this.mainHandler = mainHandler;

        Set<RoutingType> routingTypes = getRoutingTypes();
        if(routingTypes.size() == 0) {
            routingTypes = new HashSet<RoutingType>();
            routingTypes.add(RoutingType.NORMAL);
        }

        //==========================================
        // If no position was specified, the route is
        // considered as a main one, "0".
        //==========================================
        if(getPositions().size() == 0) {
            getPositions().add(0);
        }

        IRoute<R> route = getRouteFactory().createRoute(getId(),
                                                        getHttpMethods(),
                                                        getPath(),
                                                        routingTypes,
                                                        getBeforeFilters(),
                                                        mainHandler,
                                                        getAfterFilters(),
                                                        getPositions(),
                                                        getAcceptedContentTypes());
        return route;
    }

}

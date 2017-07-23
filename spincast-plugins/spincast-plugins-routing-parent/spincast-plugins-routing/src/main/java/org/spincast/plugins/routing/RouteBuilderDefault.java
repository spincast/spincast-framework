package org.spincast.plugins.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RouteBuilder;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RouteBuilderDefault<R extends RequestContext<?>, W extends WebsocketContext<?>> implements RouteBuilder<R> {

    protected final Logger logger = LoggerFactory.getLogger(RouteBuilderDefault.class);

    private final Router<R, W> router;
    private final RouteFactory<R> routeFactory;
    private final SpincastRouterConfig spincastRouterConfig;
    private final SpincastFilters<R> spincastFilters;
    private final SpincastConfig spincastConfig;

    private Set<HttpMethod> httpMethods;
    private String id = null;
    private String path = null;
    private int position = 0;
    private Set<RoutingType> routingTypes;
    private List<Handler<R>> beforeFilters;
    private Handler<R> mainHandler;
    private List<Handler<R>> afterFilters;
    private Set<String> acceptedContentTypes;
    private Set<String> filterIdsToSkip;

    @AssistedInject
    public RouteBuilderDefault(RouteFactory<R> routeFactory,
                               SpincastRouterConfig spincastRouterConfig,
                               SpincastFilters<R> spincastFilters,
                               SpincastConfig spincastConfig) {
        this(null, routeFactory, spincastRouterConfig, spincastFilters, spincastConfig);
    }

    @AssistedInject
    public RouteBuilderDefault(@Assisted Router<R, W> router,
                               RouteFactory<R> routeFactory,
                               SpincastRouterConfig spincastRouterConfig,
                               SpincastFilters<R> spincastFilters,
                               SpincastConfig spincastConfig) {
        this.router = router;
        this.routeFactory = routeFactory;
        this.spincastRouterConfig = spincastRouterConfig;
        this.spincastFilters = spincastFilters;
        this.spincastConfig = spincastConfig;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected RouteFactory<R> getRouteFactory() {
        return this.routeFactory;
    }

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected SpincastFilters<R> getSpincastFilters() {
        return this.spincastFilters;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    public String getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public int getPosition() {
        return this.position;
    }

    public Set<RoutingType> getRoutingTypes() {
        if (this.routingTypes == null) {
            this.routingTypes = new HashSet<>();
        }
        return this.routingTypes;
    }

    public Set<HttpMethod> getHttpMethods() {
        if (this.httpMethods == null) {
            this.httpMethods = new HashSet<>();
        }
        return this.httpMethods;
    }

    public List<Handler<R>> getBeforeFilters() {
        if (this.beforeFilters == null) {
            this.beforeFilters = new ArrayList<Handler<R>>();
        }
        return this.beforeFilters;
    }

    public List<Handler<R>> getAfterFilters() {
        if (this.afterFilters == null) {
            this.afterFilters = new ArrayList<Handler<R>>();
        }
        return this.afterFilters;
    }

    public Handler<R> getMainHandler() {
        return this.mainHandler;
    }

    public Set<String> getAcceptedContentTypes() {
        if (this.acceptedContentTypes == null) {
            this.acceptedContentTypes = new HashSet<>();
        }
        return this.acceptedContentTypes;
    }

    public Set<String> getFilterIdsToSkip() {
        if (this.filterIdsToSkip == null) {
            this.filterIdsToSkip = new HashSet<>();
        }
        return this.filterIdsToSkip;
    }

    @Override
    public RouteBuilder<R> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public RouteBuilder<R> pos(int position) {
        this.position = position;
        return this;
    }

    @Override
    public RouteBuilder<R> allRoutingTypes() {

        getRoutingTypes().addAll(Sets.newHashSet(RoutingType.values()));
        return this;
    }

    @Override
    public RouteBuilder<R> found() {
        getRoutingTypes().add(RoutingType.FOUND);
        return this;
    }

    @Override
    public RouteBuilder<R> notFound() {
        getRoutingTypes().add(RoutingType.NOT_FOUND);
        return this;
    }

    @Override
    public RouteBuilder<R> exception() {
        getRoutingTypes().add(RoutingType.EXCEPTION);
        return this;
    }

    @Override
    public RouteBuilder<R> before(Handler<R> beforeFilter) {

        Objects.requireNonNull(beforeFilter, "beforeFilter can't be NULL");

        getBeforeFilters().add(beforeFilter);
        return this;
    }

    @Override
    public RouteBuilder<R> after(Handler<R> afterFilter) {

        Objects.requireNonNull(afterFilter, "afterFilter can't be NULL");

        getAfterFilters().add(afterFilter);
        return this;
    }

    @Override
    public RouteBuilder<R> acceptAsString(String... acceptedContentTypes) {
        getAcceptedContentTypes().addAll(Sets.newHashSet(acceptedContentTypes));
        return this;
    }

    @Override
    public RouteBuilder<R> acceptAsString(Set<String> acceptedContentTypes) {

        Objects.requireNonNull(acceptedContentTypes, "acceptedContentTypes can't be NULL");

        getAcceptedContentTypes().addAll(acceptedContentTypes);
        return this;
    }

    @Override
    public RouteBuilder<R> accept(ContentTypeDefaults... acceptedContentTypes) {

        if (acceptedContentTypes != null) {
            for (ContentTypeDefaults contentTypeDefault : acceptedContentTypes) {
                getAcceptedContentTypes().addAll(contentTypeDefault.getVariations());
            }
        }
        return this;
    }

    @Override
    public RouteBuilder<R> accept(Set<ContentTypeDefaults> acceptedContentTypes) {

        Objects.requireNonNull(acceptedContentTypes, "acceptedContentTypes can't be NULL");

        for (ContentTypeDefaults contentTypeDefault : acceptedContentTypes) {
            accept(contentTypeDefault);
        }
        return this;
    }

    @Override
    public RouteBuilder<R> html() {
        getAcceptedContentTypes().add(ContentTypeDefaults.HTML.getMainVariation());
        return this;
    }

    @Override
    public RouteBuilder<R> json() {
        getAcceptedContentTypes().add(ContentTypeDefaults.JSON.getMainVariation());
        return this;
    }

    @Override
    public RouteBuilder<R> xml() {
        getAcceptedContentTypes().add(ContentTypeDefaults.XML.getMainVariation());
        return this;
    }

    @Override
    public RouteBuilder<R> path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public RouteBuilder<R> GET() {
        getHttpMethods().add(HttpMethod.GET);
        return this;
    }

    @Override
    public RouteBuilder<R> POST() {
        getHttpMethods().add(HttpMethod.POST);
        return this;
    }

    @Override
    public RouteBuilder<R> PUT() {
        getHttpMethods().add(HttpMethod.PUT);
        return this;
    }

    @Override
    public RouteBuilder<R> DELETE() {
        getHttpMethods().add(HttpMethod.DELETE);
        return this;
    }

    @Override
    public RouteBuilder<R> OPTIONS() {
        getHttpMethods().add(HttpMethod.OPTIONS);
        return this;
    }

    @Override
    public RouteBuilder<R> TRACE() {
        getHttpMethods().add(HttpMethod.TRACE);
        return this;
    }

    @Override
    public RouteBuilder<R> HEAD() {
        getHttpMethods().add(HttpMethod.HEAD);
        return this;
    }

    @Override
    public RouteBuilder<R> PATCH() {
        getHttpMethods().add(HttpMethod.PATCH);
        return this;
    }

    @Override
    public RouteBuilder<R> ALL() {
        getHttpMethods().addAll(Sets.newHashSet(HttpMethod.values()));
        return this;
    }

    @Override
    public RouteBuilder<R> SOME(Set<HttpMethod> httpMethods) {
        getHttpMethods().addAll(httpMethods);
        return this;
    }

    @Override
    public RouteBuilder<R> SOME(HttpMethod... httpMethods) {
        getHttpMethods().addAll(Sets.newHashSet(httpMethods));
        return this;
    }

    @Override
    public void save(Handler<R> mainHandler) {

        if (getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the route!");
        }

        Route<R> route = create(mainHandler);
        getRouter().addRoute(route);
    }

    @Override
    public Route<R> create(Handler<R> mainHandler) {

        this.mainHandler = mainHandler;

        Set<RoutingType> routingTypes = getRoutingTypes();
        if (routingTypes == null || routingTypes.size() == 0) {
            routingTypes = new HashSet<RoutingType>();

            //==========================================
            // Default routing types for a filter
            //==========================================
            if (getPosition() != 0) {

                Set<RoutingType> defaultRoutingTypes = getSpincastRouterConfig().getFilterDefaultRoutingTypes();
                routingTypes.addAll(defaultRoutingTypes);
            } else {
                routingTypes.add(RoutingType.FOUND);
            }
        }

        Route<R> route = getRouteFactory().createRoute(getId(),
                                                       getHttpMethods(),
                                                       getPath(),
                                                       routingTypes,
                                                       getBeforeFilters(),
                                                       mainHandler,
                                                       getAfterFilters(),
                                                       getPosition(),
                                                       getAcceptedContentTypes(),
                                                       getFilterIdsToSkip());
        return route;
    }

    @Override
    public RouteBuilder<R> noCache() {

        before(new Handler<R>() {

            @Override
            public void handle(R context) {
                context.cacheHeaders().noCache();
            }
        });

        return this;
    }

    @Override
    public RouteBuilder<R> cache() {
        return cache(getCacheSecondsByDefault());
    }

    @Override
    public RouteBuilder<R> cache(int seconds) {
        return cache(seconds, isCachePrivateByDefault());
    }

    @Override
    public RouteBuilder<R> cache(int seconds, boolean isPrivate) {
        return cache(seconds, isPrivate, getCacheCdnSecondsByDefault());
    }

    @Override
    public RouteBuilder<R> cache(final int seconds,
                                 final boolean isPrivate,
                                 final Integer secondsCdn) {

        before(new Handler<R>() {

            @Override
            public void handle(R context) {
                getSpincastFilters().cache(context, seconds, isPrivate, secondsCdn);
            }
        });
        return this;
    }

    protected int getCacheSecondsByDefault() {
        return getSpincastConfig().getDefaultRouteCacheFilterSecondsNbr();
    }

    protected boolean isCachePrivateByDefault() {
        return getSpincastConfig().isDefaultRouteCacheFilterPrivate();
    }

    protected Integer getCacheCdnSecondsByDefault() {
        return getSpincastConfig().getDefaultRouteCacheFilterSecondsNbrCdns();
    }

    @Override
    public RouteBuilder<R> skip(String filterId) {

        if (StringUtils.isBlank(filterId)) {
            throw new RuntimeException("The filterId can't be empty.");
        }

        getFilterIdsToSkip().add(filterId);

        return this;
    }

}

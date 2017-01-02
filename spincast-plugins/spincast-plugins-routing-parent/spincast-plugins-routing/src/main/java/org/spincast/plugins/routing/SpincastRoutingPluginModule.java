package org.spincast.plugins.routing;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.routing.ETagFactory;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.RedirectRuleBuilder;
import org.spincast.core.routing.RedirectRuleBuilderFactory;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.RouteBuilder;
import org.spincast.core.routing.RouteBuilderFactory;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceBuilder;
import org.spincast.core.routing.StaticResourceBuilderFactory;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.routing.StaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceFactory;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketRoute;
import org.spincast.core.websocket.WebsocketRouteBuilder;
import org.spincast.core.websocket.WebsocketRouteBuilderFactory;
import org.spincast.core.websocket.WebsocketRouteHandler;
import org.spincast.core.websocket.WebsocketRouteHandlerFactory;
import org.spincast.plugins.routing.utils.SpincastRoutingUtils;
import org.spincast.plugins.routing.utils.SpincastRoutingUtilsDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SpincastRoutingPluginModule extends SpincastGuiceModuleBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastRoutingPluginModule.class);

    private Class<? extends Router<?, ?>> specificRouterImplementationClass;

    public SpincastRoutingPluginModule() {
        this(null, null, null);
    }

    public SpincastRoutingPluginModule(Class<? extends Router<?, ?>> specificRouterImplementationClass) {
        this(null, null, specificRouterImplementationClass);
    }

    public SpincastRoutingPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    public SpincastRoutingPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                      Class<? extends Router<?, ?>> specificRouterImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
        this.specificRouterImplementationClass = specificRouterImplementationClass;
    }

    protected Class<? extends Router<?, ?>> getSpecificRouterImplementationClass() {
        return this.specificRouterImplementationClass;
    }

    @Override
    protected void configure() {

        //==========================================
        // Validate the requirements
        //==========================================
        validateRequirements();

        //==========================================
        // Bind the router
        //==========================================
        bindRouter();

        //==========================================
        // The assisted factory to create routes
        //==========================================
        bindRouteFactory();

        //==========================================
        // The assisted factory to create route builders
        //==========================================
        bindRouteBuilderFactory();

        //==========================================
        // The assisted factory to create redirect rules builders
        //==========================================
        bindRedirectRuleBuilderFactory();

        //==========================================
        // The assisted factory to create route handler matches
        //==========================================
        bindRouteHandlerMatchFactory();

        //==========================================
        // The assisted factory to create static resources
        //==========================================
        bindStaticResourceFactory();

        //==========================================
        // The assisted factory to create static resources
        // builders.
        //==========================================
        bindStaticResourceBuilderFactory();

        //==========================================
        // The assisted factory to create static resources
        // cors config
        //==========================================
        bindStaticResourceCorsConfigFactory();

        //==========================================
        // The assisted factory to create static resources
        // cache config
        //==========================================
        bindStaticResourceCacheConfigFactory();

        //==========================================
        // The request context add-on
        //==========================================
        bindRequestContextAddon();

        //==========================================
        // The assisted factory to create Websocket routes
        //==========================================
        bindWebsocketRouteFactory();

        //==========================================
        // The assisted factory to create Websocket route builders
        //==========================================
        bindWebsocketRouteBuilderFactory();

        //==========================================
        // The assisted factory to create 
        // Websocket route handler.
        //==========================================
        bindWebsocketRouteHandlerFactory();

        //==========================================
        // The ETag factory
        //==========================================
        bindETagFactory();

        //==========================================
        // The Spincast routing utils
        //==========================================
        bindSpincastRoutingUtils();

    }

    protected void validateRequirements() {
        requireBinding(SpincastRouterConfig.class);
    }

    protected Key<?> getRouterImplementationKey() {

        //==========================================
        // Specific Router implementation class to use?
        //==========================================
        if(getSpecificRouterImplementationClass() != null) {
            return Key.get(getSpecificRouterImplementationClass());
        }

        //==========================================
        // If we use the default request context and default
        // Websocket context, we directly bind the DefaultRouter that
        // implements the "DefaultRouter" interface, for
        // easy injection of the default router.
        //==========================================
        if(getRequestContextInterface().equals(DefaultRequestContext.class) &&
           getWebsocketContextInterface().equals(DefaultWebsocketContext.class)) {
            return Key.get(DefaultRouterDefault.class);
        } else {
            return parameterizeWithContextInterfaces(SpincastRouter.class);
        }
    }

    protected Key<?> getRouteKey() {
        return parameterizeWithContextInterfaces(RouteDefault.class);
    }

    protected Key<?> getWebsocketRouteKey() {
        return parameterizeWithContextInterfaces(SpincastWebsocketRoute.class);
    }

    protected Key<?> getStaticResourceKey() {
        return parameterizeWithContextInterfaces(StaticResourceDefault.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouter() {

        Key key = getRouterImplementationKey();
        try {
            key.getTypeLiteral().getSupertype(Router.class);
        } catch(Exception ex) {
            throw new RuntimeException("The router Key must implement " + Router.class.getName() + " : " + key);
        }

        //==========================================
        // Make sure we only use one instance for this object, wathever
        // interface we bind it to.
        //==========================================
        bind(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a parameterized version.
        //==========================================
        bind(parameterizeWithContextInterfaces(Router.class)).to(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a generic "Router" version.
        //==========================================
        bind(Router.class).to(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a generic "Router<?, ?>" version.
        //==========================================
        bind(new TypeLiteral<Router<?, ?>>() {}).to(key).in(Scopes.SINGLETON);

        //==========================================
        // If the default request context class is used
        // and the router extends DefaultRouter, we can bind the 
        // "DefaultRouter" interface for easier access to the parameterized 
        // router!
        //==========================================
        if(getRequestContextInterface().equals(DefaultRequestContext.class) &&
           DefaultRouter.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
            bind(DefaultRouter.class).to(key).in(Scopes.SINGLETON);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouteFactory() {

        Key key = getRouteKey();
        try {
            key.getTypeLiteral().getSupertype(Route.class);
        } catch(Exception ex) {
            throw new RuntimeException("The route Key must implement " +
                                       Route.class.getName() + " : " + key);
        }

        Key routeKey = parameterizeWithRequestContext(Route.class);
        Key factoryKey = parameterizeWithRequestContext(RouteFactory.class);

        // Bind as assisted factory
        Annotation annotation = key.getAnnotation();
        if(annotation != null) {
            install(new FactoryModuleBuilder().implement(routeKey.getTypeLiteral(),
                                                         annotation,
                                                         key.getTypeLiteral())
                                              .build(factoryKey));
        } else {
            install(new FactoryModuleBuilder().implement(routeKey, key.getTypeLiteral())
                                              .build(factoryKey));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindWebsocketRouteFactory() {

        Key key = getWebsocketRouteKey();
        try {
            key.getTypeLiteral().getSupertype(WebsocketRoute.class);
        } catch(Exception ex) {
            throw new RuntimeException("The websocket route Key must implement " +
                                       WebsocketRoute.class.getName() + " : " + key);
        }

        Key routeKey = parameterizeWithContextInterfaces(WebsocketRoute.class);
        Key factoryKey = parameterizeWithContextInterfaces(WebsocketRouteFactory.class);

        // Bind as assisted factory
        Annotation annotation = key.getAnnotation();
        if(annotation != null) {
            install(new FactoryModuleBuilder().implement(routeKey.getTypeLiteral(),
                                                         annotation,
                                                         key.getTypeLiteral())
                                              .build(factoryKey));
        } else {
            install(new FactoryModuleBuilder().implement(routeKey, key.getTypeLiteral())
                                              .build(factoryKey));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouteBuilderFactory() {

        Key interfaceKey = parameterizeWithRequestContext(RouteBuilder.class);
        Key implementationKey = parameterizeWithContextInterfaces(getRouteBuilderImplClass());
        Key factoryKey = parameterizeWithContextInterfaces(RouteBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends RouteBuilder> getRouteBuilderImplClass() {
        return RouteBuilderDefault.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRedirectRuleBuilderFactory() {

        Key implementationKey = parameterizeWithContextInterfaces(getRedirectRuleBuilderImplClass());
        Key factoryKey = parameterizeWithContextInterfaces(RedirectRuleBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(RedirectRuleBuilder.class, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    protected Class<? extends RedirectRuleBuilder> getRedirectRuleBuilderImplClass() {
        return RedirectRuleBuilderDefault.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindWebsocketRouteBuilderFactory() {

        Key interfaceKey = parameterizeWithContextInterfaces(WebsocketRouteBuilder.class);
        Key implementationKey = parameterizeWithContextInterfaces(getWebsocketRouteBuilderImplClass());
        Key factoryKey = parameterizeWithContextInterfaces(WebsocketRouteBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends WebsocketRouteBuilder> getWebsocketRouteBuilderImplClass() {
        return WebsocketRouteBuilderDefault.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindWebsocketRouteHandlerFactory() {

        Key interfaceKey = parameterizeWithRequestContext(Handler.class);
        Key implementationKey = parameterizeWithContextInterfaces(getWebsocketRouteHandlerImplClass());
        Key factoryKey = parameterizeWithContextInterfaces(WebsocketRouteHandlerFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends Handler> getWebsocketRouteHandlerImplClass() {
        return WebsocketRouteHandler.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouteHandlerMatchFactory() {
        Key interfaceKey = parameterizeWithRequestContext(RouteHandlerMatch.class);
        Key implementationKey = parameterizeWithRequestContext(getRouteHandlerMatchImplClass());
        Key factoryKey = parameterizeWithRequestContext(RouteHandlerMatchFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends RouteHandlerMatch> getRouteHandlerMatchImplClass() {
        return RouteHandlerMatchDefault.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindStaticResourceFactory() {

        Key key = getStaticResourceKey();
        try {
            key.getTypeLiteral().getSupertype(StaticResource.class);
        } catch(Exception ex) {
            throw new RuntimeException("The static resource Key must implement " +
                                       StaticResource.class.getName() + " : " + key);
        }

        Key staticResourceKey = parameterizeWithRequestContext(StaticResource.class);
        Key factoryKey = parameterizeWithRequestContext(StaticResourceFactory.class);

        // Bind as assisted factory
        Annotation annotation = key.getAnnotation();
        if(annotation != null) {
            install(new FactoryModuleBuilder().implement(staticResourceKey.getTypeLiteral(),
                                                         annotation,
                                                         key.getTypeLiteral())
                                              .build(factoryKey));
        } else {
            install(new FactoryModuleBuilder().implement(staticResourceKey, key.getTypeLiteral())
                                              .build(factoryKey));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindStaticResourceBuilderFactory() {
        Key interfaceKey = parameterizeWithRequestContext(StaticResourceBuilder.class);
        Key implementationKey = parameterizeWithContextInterfaces(getStaticResourceBuilderImplClass());
        Key factoryKey = parameterizeWithContextInterfaces(StaticResourceBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends StaticResourceBuilder> getStaticResourceBuilderImplClass() {
        return StaticResourceBuilderDefault.class;
    }

    protected void bindStaticResourceCorsConfigFactory() {
        install(new FactoryModuleBuilder().implement(StaticResourceCorsConfig.class,
                                                     getStaticResourceCorsConfigImplClass())
                                          .build(StaticResourceCorsConfigFactory.class));
    }

    protected Class<? extends StaticResourceCorsConfig> getStaticResourceCorsConfigImplClass() {
        return StaticResourceCorsConfigDefault.class;
    }

    protected void bindStaticResourceCacheConfigFactory() {
        install(new FactoryModuleBuilder().implement(StaticResourceCacheConfig.class,
                                                     getStaticResourceCacheConfigImplClass())
                                          .build(StaticResourceCacheConfigFactory.class));
    }

    protected Class<? extends StaticResourceCacheConfig> getStaticResourceCacheConfigImplClass() {
        return StaticResourceCacheConfigDefault.class;
    }

    protected void bindRequestContextAddon() {
        bind(parameterizeWithRequestContext(RoutingRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastRoutingRequestContextAddon.class))
                                                                              .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindETagFactory() {
        bind(ETagFactory.class).to(getETagFactoryImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ETagFactory> getETagFactoryImplClass() {
        return ETagFactoryDefault.class;
    }

    protected void bindSpincastRoutingUtils() {
        bind(SpincastRoutingUtils.class).to(getSpincastRoutingUtilsImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastRoutingUtils> getSpincastRoutingUtilsImplClass() {
        return SpincastRoutingUtilsDefault.class;
    }

}

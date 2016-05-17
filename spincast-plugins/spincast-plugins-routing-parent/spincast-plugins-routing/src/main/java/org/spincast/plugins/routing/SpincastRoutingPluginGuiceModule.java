package org.spincast.plugins.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.routing.IRoute;
import org.spincast.core.routing.IRouteBuilder;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceBuilder;
import org.spincast.core.routing.IStaticResourceBuilderFactory;
import org.spincast.core.routing.IStaticResourceCorsConfig;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Routing plugin.
 */
public class SpincastRoutingPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastRoutingPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
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
        // The request context add-on
        //==========================================
        bindRequestContextAddon();
    }

    protected void validateRequirements() {
        requireBinding(ISpincastRouterConfig.class);
    }

    protected Key<?> getRouterImplementationKey() {

        //==========================================
        // If we use the defaulre quest context, we
        // directly bind the DefaultRouter that
        // implements the "IDefaultRouter" interface, for
        // easy injection of the default router.
        //==========================================
        if(getRequestContextType().equals(IDefaultRequestContext.class)) {
            return Key.get(DefaultRouter.class);
        } else {
            return parameterizeWithRequestContextInterface(SpincastRouter.class);
        }
    }

    protected Key<?> getRouteKey() {
        return parameterizeWithRequestContextInterface(SpincastRoute.class);
    }

    protected Key<?> getStaticResourceKey() {
        return parameterizeWithRequestContextInterface(StaticResource.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouter() {

        Key key = getRouterImplementationKey();
        try {
            key.getTypeLiteral().getSupertype(IRouter.class);
        } catch(Exception ex) {
            throw new RuntimeException("The router Key must implement " + IRouter.class.getName() + " : " + key);
        }

        //==========================================
        // Make sure we only use one instance for this object, wathever
        // interface we bind it to.
        //==========================================
        bind(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a parameterized version.
        //==========================================
        bind(parameterizeWithRequestContextInterface(IRouter.class)).to(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a generic "IRouter" version.
        //==========================================
        bind(IRouter.class).to(key).in(Scopes.SINGLETON);

        //==========================================
        // Bind a generic "IRouter<?>" version.
        //==========================================
        bind(new TypeLiteral<IRouter<?>>() {}).to(key).in(Scopes.SINGLETON);

        //==========================================
        // If the default request context class is used
        // and the router extends IDefaultRouter, we can bind the 
        // "IDefaultRouter" interface for easier access to the parameterized 
        // router!
        //==========================================
        if(getRequestContextType().equals(IDefaultRequestContext.class) &&
           IDefaultRouter.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
            bind(IDefaultRouter.class).to(key).in(Scopes.SINGLETON);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouteFactory() {

        Key key = getRouteKey();
        try {
            key.getTypeLiteral().getSupertype(IRoute.class);
        } catch(Exception ex) {
            throw new RuntimeException("The route Key must implement " +
                                       IRoute.class.getName() + " : " + key);
        }

        Key routeKey = parameterizeWithRequestContextInterface(IRoute.class);
        Key factoryKey = parameterizeWithRequestContextInterface(IRouteFactory.class);

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

        Key interfaceKey = parameterizeWithRequestContextInterface(IRouteBuilder.class);
        Key implementationKey = parameterizeWithRequestContextInterface(getRouteBuilderImplClass());
        Key factoryKey = parameterizeWithRequestContextInterface(IRouteBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends IRouteBuilder> getRouteBuilderImplClass() {
        return RouteBuilder.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRouteHandlerMatchFactory() {
        Key interfaceKey = parameterizeWithRequestContextInterface(IRouteHandlerMatch.class);
        Key implementationKey = parameterizeWithRequestContextInterface(getRouteHandlerMatchImplClass());
        Key factoryKey = parameterizeWithRequestContextInterface(IRouteHandlerMatchFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends IRouteHandlerMatch> getRouteHandlerMatchImplClass() {
        return RouteHandlerMatch.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindStaticResourceFactory() {

        Key key = getStaticResourceKey();
        try {
            key.getTypeLiteral().getSupertype(IStaticResource.class);
        } catch(Exception ex) {
            throw new RuntimeException("The static resource Key must implement " +
                                       IStaticResource.class.getName() + " : " + key);
        }

        Key staticResourceKey = parameterizeWithRequestContextInterface(IStaticResource.class);
        Key factoryKey = parameterizeWithRequestContextInterface(IStaticResourceFactory.class);

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
        Key interfaceKey = parameterizeWithRequestContextInterface(IStaticResourceBuilder.class);
        Key implementationKey = parameterizeWithRequestContextInterface(getStaticResourceBuilderImplClass());
        Key factoryKey = parameterizeWithRequestContextInterface(IStaticResourceBuilderFactory.class);

        install(new FactoryModuleBuilder().implement(interfaceKey, implementationKey.getTypeLiteral())
                                          .build(factoryKey));
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends IStaticResourceBuilder> getStaticResourceBuilderImplClass() {
        return StaticResourceBuilder.class;
    }

    protected void bindStaticResourceCorsConfigFactory() {
        install(new FactoryModuleBuilder().implement(IStaticResourceCorsConfig.class, getStaticResourceCorsConfigImplClass())
                                          .build(IStaticResourceCorsConfigFactory.class));
    }

    protected Class<? extends IStaticResourceCorsConfig> getStaticResourceCorsConfigImplClass() {
        return StaticResourceCorsConfig.class;
    }

    protected void bindRequestContextAddon() {
        bind(parameterizeWithRequestContextInterface(IRoutingRequestContextAddon.class))
                                                                                       .to(parameterizeWithRequestContextInterface(SpincastRoutingRequestContextAddon.class))
                                                                                       .in(SpincastGuiceScopes.REQUEST);
    }

}

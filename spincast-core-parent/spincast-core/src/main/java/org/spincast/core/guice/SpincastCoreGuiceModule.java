package org.spincast.core.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.controllers.IFrontController;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.cookies.ICookiesRequestContextAddon;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.IRequestContextFactory;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.exchange.IResponseRequestContextAddon;
import org.spincast.core.exchange.IVariablesRequestContextAddon;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.filters.CorsFilter;
import org.spincast.core.filters.ICorsFilter;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.IJsonObjectFactory;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.routing.DefaultRouteParamAliasesBinder;
import org.spincast.core.routing.IRouteBuilderFactory;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.server.IServer;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.templating.ITemplatingRequestContextAddon;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * The Spincast core Guice module. This module makes sure every 
 * required component is bound, and also binds some core stuff.                                           
 */
public class SpincastCoreGuiceModule extends SpincastGuiceModuleBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastCoreGuiceModule.class);

    private Type requestContextType;
    private final String[] mainArgs;

    /**
     * Constructor
     */
    public SpincastCoreGuiceModule() {
        this(null);
    }

    /**
     * Constructor
     * 
     * @param mainArgs The main method's arguments. If specified, they will
     * be bound using the {@link org.spincast.core.guice.MainArgs @MainArgs} annotation.
     */
    public SpincastCoreGuiceModule(String[] mainArgs) {
        if(mainArgs == null) {
            mainArgs = new String[0];
        }
        this.mainArgs = mainArgs;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    @Override
    protected void configure() {

        //==========================================
        // Validate the bindings that need to be bound by other
        // modules.
        //==========================================
        validateRequirements();

        //==========================================
        // Bind the main arguments
        //==========================================
        bindMainArgs();

        //==========================================
        // Bind the request context key
        //==========================================
        bindRequestContextType();

        //==========================================
        // Bind Spincast request scope
        //==========================================
        bindSpincastRequestScope();

        //==========================================
        // Bind the request context in request scope
        //==========================================
        bindRequestContextInRequestScope();

        //==========================================
        // Bind the request context base's depenedencies
        // wrapper
        //==========================================
        bindRequestContextBaseDeps();

        //==========================================
        // Bind the default predefined route parameter patterns binder.
        //==========================================
        bindDefaultPredefinedRouteParamPatternsBinder();

        //==========================================
        // The factory to create request context
        //==========================================
        bindRequestContextFactory();

        //==========================================
        // The front controller
        //==========================================
        bindFrontController();

        //==========================================
        // Bind Spincast utils class
        //==========================================
        bindSpincastUtilsClass();

        //==========================================
        // Bind Spincast filters
        //==========================================
        bindSpincastFilters();

        //==========================================
        // Bind JsonObject factory
        //==========================================
        bindJsonObjectFactory();

    }

    /**
     * Validates the bindings that have to be done by other modules.
     */
    protected void validateRequirements() {

        requireBinding(IServer.class);
        requireBinding(parameterizeWithRequestContextInterface(IRouter.class));
        requireBinding(Key.get(new TypeLiteral<IRouter<?>>() {}));
        requireBinding(parameterizeWithRequestContextInterface(IRouteBuilderFactory.class));
        requireBinding(ITemplatingEngine.class);
        requireBinding(IJsonManager.class);
        requireBinding(IXmlManager.class);
        requireBinding(ISpincastConfig.class);
        requireBinding(ISpincastDictionary.class);
        requireBinding(ICookieFactory.class);
        requireBinding(ILocaleResolver.class);
        requireBinding(parameterizeWithRequestContextInterface(IRequestRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContextInterface(IResponseRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContextInterface(IRoutingRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContextInterface(ICookiesRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContextInterface(ITemplatingRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContextInterface(IVariablesRequestContextAddon.class));
    }

    protected void bindMainArgs() {
        bind(new TypeLiteral<String[]>() {}).annotatedWith(MainArgs.class).toInstance(getMainArgs());
        bind(new TypeLiteral<List<String>>() {}).annotatedWith(MainArgs.class).toInstance(Arrays.asList(getMainArgs()));
    }

    protected void bindRequestContextType() {
        Type type = getRequestContextType();
        bind(Type.class).annotatedWith(RequestContextType.class).toInstance(type);
    }

    protected void bindSpincastRequestScope() {

        // @see https://github.com/google/guice/wiki/CustomScopes
        bindScope(SpincastRequestScoped.class, SpincastGuiceScopes.REQUEST);
        bind(SpincastRequestScope.class).toInstance(SpincastGuiceScopes.REQUEST);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRequestContextInRequestScope() {

        Key key = Key.get(getRequestContextType());
        bind(key)
                 .toProvider((Provider)SpincastRequestScope.getSeedErrorProvider(key))
                 .in(SpincastGuiceScopes.REQUEST);

        //==========================================
        // Generic "IRequestContext<?>" version
        //==========================================
        TypeLiteral typeLiteral = new TypeLiteral<IRequestContext<?>>() {};
        bind(Key.get(typeLiteral))
                                  .toProvider((Provider)SpincastRequestScope.getSeedErrorProvider(Key.get(typeLiteral)))
                                  .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindRequestContextBaseDeps() {
        bind(parameterizeWithRequestContextInterface(RequestContextBaseDeps.class)).in(Scopes.SINGLETON);
    }

    @Override
    protected Type getRequestContextType() {
        if(this.requestContextType == null) {

            Key<? extends IRequestContext<?>> key = Key.get(getRequestContextImplementationClass());

            TypeLiteral<?> requestContextTypeLiteral = key.getTypeLiteral().getSupertype(IRequestContext.class);
            this.requestContextType = ((ParameterizedType)requestContextTypeLiteral.getType()).getActualTypeArguments()[0];
        }
        return this.requestContextType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindSpincastFilters() {

        Key key = getSpincastFiltersKey();
        try {
            key.getTypeLiteral().getSupertype(ISpincastFilters.class);
        } catch(Exception ex) {
            throw new RuntimeException("The Spincast Filters Key must implement " + ISpincastFilters.class.getName() +
                                       " : " + key);
        }

        bind(parameterizeWithRequestContextInterface(ISpincastFilters.class)).to(key).in(Scopes.SINGLETON);

        bind(ICorsFilter.class).to(getCorsFilterClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ICorsFilter> getCorsFilterClass() {
        return CorsFilter.class;
    }

    @SuppressWarnings("rawtypes")
    protected Key<SpincastFilters> getSpincastFiltersKey() {
        return parameterizeWithRequestContextInterface(SpincastFilters.class);
    }

    protected void bindDefaultPredefinedRouteParamPatternsBinder() {
        bind(parameterizeWithRequestContextInterface(DefaultRouteParamAliasesBinder.class)).asEagerSingleton();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRequestContextFactory() {

        Key<? extends IRequestContext<?>> key = Key.get(getRequestContextImplementationClass());

        Key requestContextFactoryKey = parameterizeWithRequestContextInterface(IRequestContextFactory.class);

        Annotation annotation = key.getAnnotation();
        if(annotation != null) {
            install(new FactoryModuleBuilder().implement((Class)getRequestContextType(),
                                                         annotation,
                                                         key.getTypeLiteral())
                                              .build(requestContextFactoryKey));
        } else {
            install(new FactoryModuleBuilder().implement((Class)getRequestContextType(), key.getTypeLiteral())
                                              .build(requestContextFactoryKey));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindFrontController() {

        Key key = getFrontControllerKey();
        try {
            key.getTypeLiteral().getSupertype(IFrontController.class);
        } catch(Exception ex) {
            throw new RuntimeException("The front controller Key must implement " + IFrontController.class.getName() +
                                       " : " + key);
        }

        bind(IFrontController.class).to(key).in(Scopes.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    protected void bindSpincastUtilsClass() {

        @SuppressWarnings("rawtypes")
        Key key = getSpincastUtilsKey();
        try {
            key.getTypeLiteral().getSupertype(ISpincastUtils.class);
        } catch(Exception ex) {
            throw new RuntimeException("The Spincast Utils Key must implement " + ISpincastUtils.class.getName() +
                                       " : " + key);
        }

        bind(ISpincastUtils.class).to(key).in(Scopes.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    protected void bindJsonObjectFactory() {

        @SuppressWarnings("rawtypes")
        Key jsonObjectKey = getJsonObjectKey();
        try {
            jsonObjectKey.getTypeLiteral().getSupertype(IJsonObject.class);
        } catch(Exception ex) {
            throw new RuntimeException("The json object Key must implement " + IJsonObject.class.getName() + " : " +
                                       jsonObjectKey);
        }

        @SuppressWarnings("rawtypes")
        Key jsonArrayKey = getJsonArrayKey();
        try {
            jsonArrayKey.getTypeLiteral().getSupertype(IJsonArray.class);
        } catch(Exception ex) {
            throw new RuntimeException("The json array Key must implement " + IJsonArray.class.getName() + " : " +
                                       jsonArrayKey);
        }

        Annotation jsonObjectKeyAnnotation = jsonObjectKey.getAnnotation();
        Annotation jsonArrayKeyAnnotation = jsonArrayKey.getAnnotation();

        if(jsonObjectKeyAnnotation != null && jsonArrayKeyAnnotation != null) {
            install(new FactoryModuleBuilder().implement(IJsonObject.class,
                                                         jsonObjectKeyAnnotation,
                                                         jsonObjectKey.getTypeLiteral())
                                              .implement(IJsonArray.class,
                                                         jsonArrayKeyAnnotation,
                                                         jsonArrayKey.getTypeLiteral())
                                              .build(IJsonObjectFactory.class));
        } else if(jsonObjectKeyAnnotation != null) {
            install(new FactoryModuleBuilder().implement(IJsonObject.class,
                                                         jsonObjectKeyAnnotation,
                                                         jsonObjectKey.getTypeLiteral())
                                              .implement(IJsonArray.class, jsonArrayKey.getTypeLiteral())
                                              .build(IJsonObjectFactory.class));
        } else if(jsonArrayKeyAnnotation != null) {
            install(new FactoryModuleBuilder().implement(IJsonObject.class, jsonObjectKey.getTypeLiteral())
                                              .implement(IJsonArray.class,
                                                         jsonArrayKeyAnnotation,
                                                         jsonArrayKey.getTypeLiteral())
                                              .build(IJsonObjectFactory.class));
        } else {
            install(new FactoryModuleBuilder().implement(IJsonObject.class, jsonObjectKey.getTypeLiteral())
                                              .implement(IJsonArray.class, jsonArrayKey.getTypeLiteral())
                                              .build(IJsonObjectFactory.class));
        }
    }

    /**
     * The implementation to use for the request context objects.
     * 
     * Extend this method to create a custom request context type.
     * 
     * @see <a href="https://www.spincast.org/documentation#extending_request_context">Extending the request context</a>
     */
    protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
        return DefaultRequestContext.class;
    }

    protected Key<?> getFrontControllerKey() {
        return parameterizeWithRequestContextInterface(SpincastFrontController.class);
    }

    protected Key<?> getSpincastUtilsKey() {
        return Key.get(SpincastUtils.class);
    }

    protected Key<?> getJsonObjectKey() {
        return Key.get(JsonObject.class);
    }

    protected Key<?> getJsonArrayKey() {
        return Key.get(JsonArray.class);
    }

}

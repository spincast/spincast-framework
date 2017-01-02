package org.spincast.core.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.config.SpincastInitValidator;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookiesRequestContextAddon;
import org.spincast.core.exchange.CacheHeadersRequestContextAddon;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.exchange.RequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.exchange.ResponseRequestContextAddon;
import org.spincast.core.exchange.VariablesRequestContextAddon;
import org.spincast.core.filters.CorsFilter;
import org.spincast.core.filters.CorsFilterDefault;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.filters.SpincastFiltersDefault;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonArrayDefault;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.core.json.JsonPathUtils;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.DefaultRouteParamAliasesBinder;
import org.spincast.core.routing.ETagFactory;
import org.spincast.core.routing.RedirectRuleBuilderFactory;
import org.spincast.core.routing.RouteBuilderFactory;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.routing.StaticResourceFactory;
import org.spincast.core.server.Server;
import org.spincast.core.session.FlashMessage;
import org.spincast.core.session.FlashMessageDefault;
import org.spincast.core.session.FlashMessageFactory;
import org.spincast.core.session.FlashMessagesHolder;
import org.spincast.core.session.FlashMessagesHolderDefault;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.templating.TemplatingRequestContextAddon;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.ObjectConverterDefault;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.SpincastUtilsDefault;
import org.spincast.core.utils.ssl.SSLContextFactory;
import org.spincast.core.utils.ssl.SSLContextFactoryDefault;
import org.spincast.core.validation.JsonArrayValidationBuilderKey;
import org.spincast.core.validation.JsonArrayValidationBuilderKeyDefault;
import org.spincast.core.validation.JsonArrayValidationSet;
import org.spincast.core.validation.JsonArrayValidationSetDefault;
import org.spincast.core.validation.JsonObjectValidationBuilderKey;
import org.spincast.core.validation.JsonObjectValidationBuilderKeyDefault;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.JsonObjectValidationSetDefault;
import org.spincast.core.validation.ValidationBuilderArray;
import org.spincast.core.validation.ValidationBuilderArrayDefault;
import org.spincast.core.validation.ValidationBuilderCore;
import org.spincast.core.validation.ValidationBuilderCoreDefault;
import org.spincast.core.validation.ValidationBuilderKey;
import org.spincast.core.validation.ValidationBuilderKeyDefault;
import org.spincast.core.validation.ValidationBuilderTarget;
import org.spincast.core.validation.ValidationBuilderTargetDefault;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationMessageDefault;
import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.ValidationSetDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketContextFactory;
import org.spincast.core.websocket.WebsocketContextType;
import org.spincast.core.websocket.WebsocketEndpointHandler;
import org.spincast.core.websocket.WebsocketEndpointHandlerDefault;
import org.spincast.core.websocket.WebsocketEndpointHandlerFactory;
import org.spincast.core.websocket.WebsocketEndpointToControllerManager;
import org.spincast.core.websocket.WebsocketEndpointToControllerManagerDefault;
import org.spincast.core.websocket.WebsocketRouteBuilderFactory;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SpincastCorePluginModule extends SpincastGuiceModuleBase {

    public SpincastCorePluginModule() {
        super();
    }

    public SpincastCorePluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                    Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        //==========================================
        // Validate the bindings that need to be bound by other
        // modules.
        //==========================================
        validateRequirements();

        //==========================================
        // Bind the request context key
        //==========================================
        bindRequestContextType();

        //==========================================
        // Bind the Websocket context key
        //==========================================
        bindWebsocketContextType();

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

        //==========================================
        // Bind Websocket handlers factory
        //==========================================
        bindWebsocketEndpointHandlerFactory();

        //==========================================
        // Bind Websocket context factory
        //==========================================
        bindWebsocketContextFactory();

        //==========================================
        // Bind Websocket endpoint to controller manager.
        //==========================================
        bindWebsocketEndpointToControllerManager();

        //==========================================
        // Bind SSL Context Factory.
        //==========================================
        bindSSLContextFactory();

        //==========================================
        // Bind validation factory
        //==========================================
        bindValidationFactory();

        //==========================================
        // Bind Flash Messages components.
        //==========================================
        bindFlashMessageFactory();
        bindFlashMessagesHolder();

        //==========================================
        // This will run some validations when
        // the application starts.
        //==========================================
        bindSpincastInitValidator();

        //==========================================
        // Binds the object converter.
        //==========================================
        bindObjectConverter();

    }

    /**
     * Validates the bindings that have to be done by other modules.
     */
    protected void validateRequirements() {

        requireBinding(Server.class);
        requireBinding(parameterizeWithContextInterfaces(Router.class));
        requireBinding(Key.get(new TypeLiteral<Router<?, ?>>() {}));
        requireBinding(parameterizeWithContextInterfaces(RouteBuilderFactory.class));
        requireBinding(parameterizeWithRequestContext(StaticResourceFactory.class));
        requireBinding(parameterizeWithContextInterfaces(RedirectRuleBuilderFactory.class));
        requireBinding(parameterizeWithContextInterfaces(WebsocketRouteBuilderFactory.class));
        requireBinding(ETagFactory.class);
        requireBinding(TemplatingEngine.class);
        requireBinding(JsonManager.class);
        requireBinding(JsonPathUtils.class);
        requireBinding(XmlManager.class);
        requireBinding(SpincastConfig.class);
        requireBinding(SpincastDictionary.class);
        requireBinding(CookieFactory.class);
        requireBinding(LocaleResolver.class);
        requireBinding(parameterizeWithRequestContext(RequestRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(ResponseRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(RoutingRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(CookiesRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(TemplatingRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(VariablesRequestContextAddon.class));
        requireBinding(parameterizeWithRequestContext(CacheHeadersRequestContextAddon.class));
    }

    protected void bindSpincastInitValidator() {
        bind(SpincastInitValidator.class).asEagerSingleton();
    }

    protected void bindRequestContextType() {
        Type type = getRequestContextInterface();
        bind(Type.class).annotatedWith(RequestContextType.class).toInstance(type);
    }

    protected void bindWebsocketContextType() {
        Type type = getWebsocketContextInterface();
        bind(Type.class).annotatedWith(WebsocketContextType.class).toInstance(type);
    }

    protected void bindSpincastRequestScope() {

        // @see https://github.com/google/guice/wiki/CustomScopes
        bindScope(SpincastRequestScoped.class, SpincastGuiceScopes.REQUEST);
        bind(SpincastRequestScope.class).toInstance(SpincastGuiceScopes.REQUEST);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRequestContextInRequestScope() {

        Key key = Key.get(getRequestContextInterface());
        bind(key)
                 .toProvider(SpincastRequestScope.getSeedErrorProvider(key))
                 .in(SpincastGuiceScopes.REQUEST);

        //==========================================
        // Generic "RequestContext<?>" version
        //==========================================
        TypeLiteral typeLiteral = new TypeLiteral<RequestContext<?>>() {};
        bind(Key.get(typeLiteral))
                                  .toProvider(SpincastRequestScope.getSeedErrorProvider(Key.get(typeLiteral)))
                                  .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindRequestContextBaseDeps() {
        bind(parameterizeWithRequestContext(RequestContextBaseDeps.class)).in(Scopes.SINGLETON);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindSpincastFilters() {

        Key key = getSpincastFiltersKey();
        try {
            key.getTypeLiteral().getSupertype(SpincastFilters.class);
        } catch (Exception ex) {
            throw new RuntimeException("The Spincast Filters Key must implement " + SpincastFilters.class.getName() +
                                       " : " + key);
        }

        bind(parameterizeWithRequestContext(SpincastFilters.class)).to(key).in(Scopes.SINGLETON);

        bind(CorsFilter.class).to(getCorsFilterClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends CorsFilter> getCorsFilterClass() {
        return CorsFilterDefault.class;
    }

    @SuppressWarnings("rawtypes")
    protected Key<SpincastFiltersDefault> getSpincastFiltersKey() {
        return parameterizeWithContextInterfaces(SpincastFiltersDefault.class);
    }

    protected void bindDefaultPredefinedRouteParamPatternsBinder() {
        bind(parameterizeWithContextInterfaces(DefaultRouteParamAliasesBinder.class)).asEagerSingleton();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindRequestContextFactory() {

        Key<? extends RequestContext<?>> key = Key.get(getRequestContextImplementationClass());

        Key requestContextFactoryKey = parameterizeWithRequestContext(RequestContextFactory.class);

        Annotation annotation = key.getAnnotation();
        if (annotation != null) {
            install(new FactoryModuleBuilder().implement((Class)getRequestContextInterface(),
                                                         annotation,
                                                         key.getTypeLiteral())
                                              .build(requestContextFactoryKey));
        } else {
            install(new FactoryModuleBuilder().implement((Class)getRequestContextInterface(), key.getTypeLiteral())
                                              .build(requestContextFactoryKey));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindFrontController() {

        Key key = getFrontControllerKey();
        try {
            key.getTypeLiteral().getSupertype(FrontController.class);
        } catch (Exception ex) {
            throw new RuntimeException("The front controller Key must implement " + FrontController.class.getName() +
                                       " : " + key);
        }

        bind(FrontController.class).to(key).in(Scopes.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    protected void bindSpincastUtilsClass() {

        @SuppressWarnings("rawtypes")
        Key key = getSpincastUtilsKey();
        try {
            key.getTypeLiteral().getSupertype(SpincastUtils.class);
        } catch (Exception ex) {
            throw new RuntimeException("The Spincast Utils Key must implement " + SpincastUtils.class.getName() +
                                       " : " + key);
        }

        bind(SpincastUtils.class).to(key).in(Scopes.SINGLETON);
    }

    protected void bindJsonObjectFactory() {

        install(new FactoryModuleBuilder()
                                          .implement(JsonObject.class, getJsonObjectImplClass())
                                          .implement(JsonArray.class, getJsonArrayImplClass())
                                          .build(JsonObjectFactory.class));
    }

    protected Key<?> getFrontControllerKey() {
        return parameterizeWithContextInterfaces(SpincastFrontController.class);
    }

    protected Key<?> getSpincastUtilsKey() {
        return Key.get(SpincastUtilsDefault.class);
    }

    protected Class<? extends JsonObject> getJsonObjectImplClass() {
        return JsonObjectDefault.class;
    }

    protected Class<? extends JsonArray> getJsonArrayImplClass() {
        return JsonArrayDefault.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindWebsocketEndpointHandlerFactory() {

        Key factoryKey = parameterizeWithContextInterfaces(WebsocketEndpointHandlerFactory.class);

        install(new FactoryModuleBuilder().implement((Class)WebsocketEndpointHandler.class,
                                                     getWebsocketEndpointHandlerKey().getTypeLiteral())
                                          .build(factoryKey));
    }

    protected Key<?> getWebsocketEndpointHandlerKey() {
        return parameterizeWithContextInterfaces(WebsocketEndpointHandlerDefault.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void bindWebsocketContextFactory() {

        Key factoryKey = parameterizeWithWebsocketContext(WebsocketContextFactory.class);

        install(new FactoryModuleBuilder().implement((Class)getWebsocketContextInterface(),
                                                     getWebsocketContextImplementationClass())
                                          .build(factoryKey));
    }

    protected void bindWebsocketEndpointToControllerManager() {
        bind(WebsocketEndpointToControllerManager.class).to(getWebsocketEndpointToControllerKeysMapClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends WebsocketEndpointToControllerManager> getWebsocketEndpointToControllerKeysMapClass() {
        return WebsocketEndpointToControllerManagerDefault.class;
    }

    protected void bindSSLContextFactory() {
        bind(SSLContextFactory.class).to(getSSLContextFactoryClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SSLContextFactory> getSSLContextFactoryClass() {
        return SSLContextFactoryDefault.class;
    }

    protected void bindValidationFactory() {
        install(new FactoryModuleBuilder().implement(ValidationSet.class, getValidationResultBuilderImplClass())
                                          .implement(ValidationMessage.class, getValidationMessageImplClass())
                                          .implement(ValidationBuilderKey.class, getValidationBuilderKeyImplClass())
                                          .implement(JsonObjectValidationBuilderKey.class,
                                                     getJsonObjectValidationBuilderKeyImplClass())
                                          .implement(JsonArrayValidationBuilderKey.class,
                                                     getJsonArrayValidationBuilderKeyImplClass())
                                          .implement(ValidationBuilderTarget.class, getValidationBuilderTargetImplClass())
                                          .implement(ValidationBuilderCore.class, getValidationBuilderCoreImplClass())
                                          .implement(ValidationBuilderArray.class, getValidationBuilderArrayImplClass())
                                          .implement(JsonObjectValidationSet.class, getJsonObjectValidationSetImplClass())
                                          .implement(JsonArrayValidationSet.class, getJsonArrayValidationSetImplClass())
                                          .build(ValidationFactory.class));
    }

    protected Class<? extends JsonObjectValidationSet> getJsonObjectValidationSetImplClass() {
        return JsonObjectValidationSetDefault.class;
    }

    protected Class<? extends JsonArrayValidationSet> getJsonArrayValidationSetImplClass() {
        return JsonArrayValidationSetDefault.class;
    }

    protected Class<? extends ValidationSet> getValidationResultBuilderImplClass() {
        return ValidationSetDefault.class;
    }

    protected Class<? extends ValidationMessage> getValidationMessageImplClass() {
        return ValidationMessageDefault.class;
    }

    protected Class<? extends ValidationBuilderKey> getValidationBuilderKeyImplClass() {
        return ValidationBuilderKeyDefault.class;
    }

    protected Class<? extends ValidationBuilderTarget> getValidationBuilderTargetImplClass() {
        return ValidationBuilderTargetDefault.class;
    }

    protected Class<? extends JsonObjectValidationBuilderKey> getJsonObjectValidationBuilderKeyImplClass() {
        return JsonObjectValidationBuilderKeyDefault.class;
    }

    protected Class<? extends JsonArrayValidationBuilderKey> getJsonArrayValidationBuilderKeyImplClass() {
        return JsonArrayValidationBuilderKeyDefault.class;
    }

    protected Class<? extends ValidationBuilderCore> getValidationBuilderCoreImplClass() {
        return ValidationBuilderCoreDefault.class;
    }

    protected Class<? extends ValidationBuilderArray> getValidationBuilderArrayImplClass() {
        return ValidationBuilderArrayDefault.class;
    }

    protected void bindFlashMessageFactory() {
        install(new FactoryModuleBuilder().implement(FlashMessage.class, getFlashMessageImplClass())
                                          .build(FlashMessageFactory.class));
    }

    protected Class<? extends FlashMessage> getFlashMessageImplClass() {
        return FlashMessageDefault.class;
    }

    protected void bindFlashMessagesHolder() {
        bind(FlashMessagesHolder.class).to(getFlashMessagesHolderImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends FlashMessagesHolder> getFlashMessagesHolderImplClass() {
        return FlashMessagesHolderDefault.class;
    }

    protected void bindObjectConverter() {
        bind(ObjectConverter.class).to(getObjectConverterImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ObjectConverter> getObjectConverterImplClass() {
        return ObjectConverterDefault.class;
    }

}
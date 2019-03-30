package org.spincast.plugins.openapi.bottomup;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfig;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfigDefault;
import org.spincast.plugins.openapi.bottomup.utils.SwaggerAnnotationsCreator;
import org.spincast.plugins.openapi.bottomup.utils.SwaggerAnnotationsCreatorDefault;

import com.google.inject.Scopes;

/**
 * Spincast Open API BottomUp plugin module.
 */
public class SpincastOpenApiBottomUpPluginModule extends SpincastGuiceModuleBase {

    public SpincastOpenApiBottomUpPluginModule() {
        super();
    }

    public SpincastOpenApiBottomUpPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                               Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(parameterizeWithContextInterfaces(getSpincastOpenApiManagerImpl())).in(Scopes.SINGLETON);
        bind(SpincastOpenApiManager.class).to(parameterizeWithContextInterfaces(getSpincastOpenApiManagerImpl()))
                                          .in(Scopes.SINGLETON);
        bind(SpincastOpenApiBottomUpPluginConfig.class).to(getSpincastOpenApiBottomUpPluginConfigImpl()).in(Scopes.SINGLETON);
        bind(SwaggerAnnotationsCreator.class).to(getSwaggerAnnotationsCreatorImpl()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastOpenApiManager> getSpincastOpenApiManagerImpl() {
        return SpincastOpenApiManagerDefault.class;
    }

    protected Class<? extends SpincastOpenApiBottomUpPluginConfig> getSpincastOpenApiBottomUpPluginConfigImpl() {
        return SpincastOpenApiBottomUpPluginConfigDefault.class;
    }

    protected Class<? extends SwaggerAnnotationsCreator> getSwaggerAnnotationsCreatorImpl() {
        return SwaggerAnnotationsCreatorDefault.class;
    }

}

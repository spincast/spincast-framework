package org.spincast.plugins.swagger.ui;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.swagger.ui.config.SpincastSwaggerUiConfig;
import org.spincast.plugins.swagger.ui.config.SpincastSwaggerUiConfigDefault;

import com.google.inject.Scopes;

/**
 * Spincast Swagger UI plugin module.
 */
public class SpincastSwaggerUiPluginModule extends SpincastGuiceModuleBase {

    public SpincastSwaggerUiPluginModule() {
        super();
    }

    public SpincastSwaggerUiPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                         Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastSwaggerUiManager.class).asEagerSingleton();
        bind(SpincastSwaggerUiConfig.class).to(getSpincastSwaggerUiConfigImpl()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastSwaggerUiConfig> getSpincastSwaggerUiConfigImpl() {
        return SpincastSwaggerUiConfigDefault.class;
    }
}

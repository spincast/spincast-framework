package org.spincast.plugins.config;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Config Plugin.
 */
public class SpincastConfigPluginModule extends SpincastGuiceModuleBase {

    private Class<? extends SpincastConfig> specificConfigImplClass;

    public SpincastConfigPluginModule() {
        this(null, null, null);
    }

    public SpincastConfigPluginModule(Class<? extends SpincastConfig> specificConfigImplClass) {
        this(null, null, specificConfigImplClass);
    }

    public SpincastConfigPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    public SpincastConfigPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                      Class<? extends SpincastConfig> specificConfigImplClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
        this.specificConfigImplClass = specificConfigImplClass;
    }

    @Override
    protected void configure() {
        bind(getConfigImplClass()).in(Scopes.SINGLETON);

        //==========================================
        // asEagerSingleton() : we want to validate
        // the configurations as asoon as the app starts.
        //==========================================
        bind(SpincastConfig.class).to(getConfigImplClass()).asEagerSingleton();
    }

    protected Class<? extends SpincastConfig> getSpecificConfigImplClass() {
        return this.specificConfigImplClass;
    }

    protected Class<? extends SpincastConfig> getConfigImplClass() {
        if (getSpecificConfigImplClass() != null) {
            return getSpecificConfigImplClass();
        }
        return getDefaultConfigImplClass();
    }

    protected Class<? extends SpincastConfig> getDefaultConfigImplClass() {
        return SpincastConfigDefault.class;
    }

}

package org.spincast.plugins.config;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

public class SpincastConfigPluginModule extends SpincastGuiceModuleBase {

    private Class<? extends SpincastConfig> specificSpincastConfigImplClass;

    public SpincastConfigPluginModule() {
        this(null, null, null);
    }

    public SpincastConfigPluginModule(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        this(null, null, specificSpincastConfigImplClass);
    }

    public SpincastConfigPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    /**
     * Constructor
     * 
     * @param specificSpincastConfigImplClass the specific
     * configuration implementation class to use.
     */
    public SpincastConfigPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                      Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
        this.specificSpincastConfigImplClass = specificSpincastConfigImplClass;
    }

    protected Class<? extends SpincastConfig> getSpecificSpincastConfigImplClass() {
        return this.specificSpincastConfigImplClass;
    }

    protected Class<? extends SpincastConfig> getSpincastConfigImplClass() {
        if(getSpecificSpincastConfigImplClass() != null) {
            return getSpecificSpincastConfigImplClass();
        }
        return getDefaultSpincastConfigImplClass();
    }

    protected Class<? extends SpincastConfig> getDefaultSpincastConfigImplClass() {
        return SpincastConfigDefault.class;
    }

    @Override
    protected void configure() {
        bindSpincastConfig();
    }

    protected void bindSpincastConfig() {
        bind(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
        bind(SpincastConfig.class).to(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
    }

}

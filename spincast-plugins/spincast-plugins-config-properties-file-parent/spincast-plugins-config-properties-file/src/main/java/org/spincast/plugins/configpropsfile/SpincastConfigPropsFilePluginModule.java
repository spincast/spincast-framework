package org.spincast.plugins.configpropsfile;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.config.SpincastConfigPluginModule;

/**
 * Guice module for the Spincast Config based on a properties file plugin.
 */
public class SpincastConfigPropsFilePluginModule extends SpincastConfigPluginModule {

    public SpincastConfigPropsFilePluginModule() {
        this(null, null, null);
    }

    public SpincastConfigPropsFilePluginModule(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        this(null, null, specificSpincastConfigImplClass);
    }

    public SpincastConfigPropsFilePluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                    Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    public SpincastConfigPropsFilePluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                    Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                                    Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass, specificSpincastConfigImplClass);
    }

    @Override
    protected Class<? extends SpincastConfig> getDefaultSpincastConfigImplClass() {
        return SpincastConfigPropsFileBased.class;
    }

}

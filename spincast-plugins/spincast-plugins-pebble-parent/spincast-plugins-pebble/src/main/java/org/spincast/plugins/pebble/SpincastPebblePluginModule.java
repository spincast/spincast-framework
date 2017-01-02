package org.spincast.plugins.pebble;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

public class SpincastPebblePluginModule extends SpincastGuiceModuleBase {

    public SpincastPebblePluginModule() {
        super();
    }

    public SpincastPebblePluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(TemplatingEngine.class).to(getSpincastPebbleTemplatingEngineClass()).in(Scopes.SINGLETON);
        bind(SpincastPebbleExtension.class).to(getSpincastPebbleExtensionClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends TemplatingEngine> getSpincastPebbleTemplatingEngineClass() {
        return SpincastPebbleTemplatingEngine.class;
    }

    protected Class<? extends SpincastPebbleExtension> getSpincastPebbleExtensionClass() {
        return SpincastPebbleExtensionDefault.class;
    }

}

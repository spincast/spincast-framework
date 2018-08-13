package org.spincast.plugins.pebble;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.mitchellbosecke.pebble.extension.Extension;

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

        //==========================================
        // Spincast extension
        //==========================================
        bind(SpincastMainPebbleExtension.class).to(getSpincastPebbleExtensionClass()).in(Scopes.SINGLETON);

        //==========================================
        // Multibinder for Extensions
        //==========================================
        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastMainPebbleExtension.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends TemplatingEngine> getSpincastPebbleTemplatingEngineClass() {
        return SpincastPebbleTemplatingEngine.class;
    }

    protected Class<? extends SpincastMainPebbleExtension> getSpincastPebbleExtensionClass() {
        return SpincastMainPebbleExtensionDefault.class;
    }

}

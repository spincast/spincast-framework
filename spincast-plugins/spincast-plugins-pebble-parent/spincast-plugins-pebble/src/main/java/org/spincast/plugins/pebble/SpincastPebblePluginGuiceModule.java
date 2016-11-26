package org.spincast.plugins.pebble;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.templating.TemplatingEngine;

import com.google.inject.Scopes;

public class SpincastPebblePluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastPebblePluginGuiceModule(Type requestContextType,
                                           Type websocketContextType) {
        super(requestContextType, websocketContextType);
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

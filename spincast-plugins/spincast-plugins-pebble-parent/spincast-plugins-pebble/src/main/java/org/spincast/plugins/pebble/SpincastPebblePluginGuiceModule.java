package org.spincast.plugins.pebble;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.templating.ITemplatingEngine;

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

        bind(ITemplatingEngine.class).to(getSpincastPebbleTemplatingEngineClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ITemplatingEngine> getSpincastPebbleTemplatingEngineClass() {
        return SpincastPebbleTemplatingEngine.class;
    }

}

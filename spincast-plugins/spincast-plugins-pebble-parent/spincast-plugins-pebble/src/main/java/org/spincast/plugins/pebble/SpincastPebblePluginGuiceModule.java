package org.spincast.plugins.pebble;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.templating.ITemplatingEngine;

import com.google.inject.Scopes;

public class SpincastPebblePluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastPebblePluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {

        bind(ITemplatingEngine.class).to(SpincastPebbleTemplatingEngine.class).in(Scopes.SINGLETON);
    }

}

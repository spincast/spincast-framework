package org.spincast.plugins.variables;

import java.lang.reflect.Type;

import org.spincast.core.exchange.VariablesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Variables plugin.
 */
public class SpincastVariablesPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastVariablesPluginGuiceModule(Type requestContextType,
                                              Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(VariablesRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastVariablesRequestContextAddon.class))
                                                                                 .in(SpincastGuiceScopes.REQUEST);
    }

}

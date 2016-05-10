package org.spincast.plugins.variables;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IVariablesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Variables plugin.
 */
public class SpincastVariablesPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastVariablesPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parametrizeWithRequestContextInterface(IVariablesRequestContextAddon.class))
                                                                                         .to(parametrizeWithRequestContextInterface(SpincastVariablesRequestContextAddon.class))
                                                                                         .in(SpincastGuiceScopes.REQUEST);
    }

}

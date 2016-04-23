package org.spincast.plugins.variables;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IVariablesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;

/**
 * Guice module for the Spincast Variables plugin.
 */
public class SpincastVariablesPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastVariablesPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    protected void bindRequestContextAddon() {

        bind(parametrizeWithRequestContextInterface(IVariablesRequestContextAddon.class))
                                                                                         .to(parametrizeWithRequestContextInterface(SpincastVariablesRequestContextAddon.class))
                                                                                         .in(SpincastGuiceScopes.REQUEST);
    }

}

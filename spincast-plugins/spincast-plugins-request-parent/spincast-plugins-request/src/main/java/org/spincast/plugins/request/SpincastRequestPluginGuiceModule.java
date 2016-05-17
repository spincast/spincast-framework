package org.spincast.plugins.request;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Request plugin.
 */
public class SpincastRequestPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastRequestPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContextInterface(IRequestRequestContextAddon.class))
                                                                                       .to(parameterizeWithRequestContextInterface(SpincastRequestRequestContextAddon.class))
                                                                                       .in(SpincastGuiceScopes.REQUEST);
    }

}

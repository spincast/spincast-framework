package org.spincast.plugins.response;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IResponseRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Response plugin.
 */
public class SpincastResponsePluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastResponsePluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContextInterface(IResponseRequestContextAddon.class))
                                                                                        .to(parameterizeWithRequestContextInterface(SpincastResponseRequestContextAddon.class))
                                                                                        .in(SpincastGuiceScopes.REQUEST);
    }

}

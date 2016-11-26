package org.spincast.plugins.request;

import java.lang.reflect.Type;

import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Request plugin.
 */
public class SpincastRequestPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastRequestPluginGuiceModule(Type requestContextType,
                                            Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(RequestRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastRequestRequestContextAddon.class))
                                                                               .in(SpincastGuiceScopes.REQUEST);
    }

}

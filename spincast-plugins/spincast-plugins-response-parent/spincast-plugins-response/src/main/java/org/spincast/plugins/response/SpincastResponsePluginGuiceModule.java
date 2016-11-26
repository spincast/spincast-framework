package org.spincast.plugins.response;

import java.lang.reflect.Type;

import org.spincast.core.exchange.ResponseRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast Response plugin.
 */
public class SpincastResponsePluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastResponsePluginGuiceModule(Type requestContextType,
                                             Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(ResponseRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastResponseRequestContextAddon.class))
                                                                                .in(SpincastGuiceScopes.REQUEST);
    }

}

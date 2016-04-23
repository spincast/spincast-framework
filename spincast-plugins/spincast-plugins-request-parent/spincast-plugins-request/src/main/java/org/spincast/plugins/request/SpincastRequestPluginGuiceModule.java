package org.spincast.plugins.request;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;

/**
 * Guice module for the Spincast Request plugin.
 */
public class SpincastRequestPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastRequestPluginGuiceModule(Type requestContextType) {
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

        bind(parametrizeWithRequestContextInterface(IRequestRequestContextAddon.class))
                                                                                       .to(parametrizeWithRequestContextInterface(SpincastRequestRequestContextAddon.class))
                                                                                       .in(SpincastGuiceScopes.REQUEST);
    }

}

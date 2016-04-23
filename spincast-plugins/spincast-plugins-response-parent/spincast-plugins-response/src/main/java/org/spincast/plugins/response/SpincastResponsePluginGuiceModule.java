package org.spincast.plugins.response;

import java.lang.reflect.Type;

import org.spincast.core.exchange.IResponseRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;

/**
 * Guice module for the Spincast Response plugin.
 */
public class SpincastResponsePluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastResponsePluginGuiceModule(Type requestContextType) {
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

        bind(parametrizeWithRequestContextInterface(IResponseRequestContextAddon.class))
                                                                                        .to(parametrizeWithRequestContextInterface(SpincastResponseRequestContextAddon.class))
                                                                                        .in(SpincastGuiceScopes.REQUEST);
    }

}

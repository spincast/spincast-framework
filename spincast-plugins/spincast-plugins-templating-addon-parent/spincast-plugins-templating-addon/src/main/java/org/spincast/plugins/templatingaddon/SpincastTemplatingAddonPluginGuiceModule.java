package org.spincast.plugins.templatingaddon;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.templating.ITemplatingRequestContextAddon;

/**
 * Guice module for the Spincast Templating Addon plugin.
 */
public class SpincastTemplatingAddonPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     * We need the request context type to use.
     */
    public SpincastTemplatingAddonPluginGuiceModule(Type requestContextType) {
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

        bind(parametrizeWithRequestContextInterface(ITemplatingRequestContextAddon.class)).to(parametrizeWithRequestContextInterface(SpincastTemplatingRequestContextAddon.class))
                                                                                          .in(SpincastGuiceScopes.REQUEST);
    }

}

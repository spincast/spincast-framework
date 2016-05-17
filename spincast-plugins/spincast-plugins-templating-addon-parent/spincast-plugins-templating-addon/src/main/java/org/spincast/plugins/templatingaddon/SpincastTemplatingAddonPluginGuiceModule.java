package org.spincast.plugins.templatingaddon;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.templating.ITemplatingRequestContextAddon;

/**
 * Guice module for the Spincast Templating Addon plugin.
 */
public class SpincastTemplatingAddonPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastTemplatingAddonPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContextInterface(ITemplatingRequestContextAddon.class)).to(parameterizeWithRequestContextInterface(SpincastTemplatingRequestContextAddon.class))
                                                                                          .in(SpincastGuiceScopes.REQUEST);
    }

}

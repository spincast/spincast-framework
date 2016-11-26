package org.spincast.plugins.templatingaddon;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.templating.TemplatingRequestContextAddon;

/**
 * Guice module for the Spincast Templating Addon plugin.
 */
public class SpincastTemplatingAddonPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastTemplatingAddonPluginGuiceModule(Type requestContextType,
                                                    Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(TemplatingRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastTemplatingRequestContextAddon.class))
                                                                                  .in(SpincastGuiceScopes.REQUEST);
    }

}

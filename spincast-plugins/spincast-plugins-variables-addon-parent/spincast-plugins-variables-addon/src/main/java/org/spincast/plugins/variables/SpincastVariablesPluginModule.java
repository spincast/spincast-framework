package org.spincast.plugins.variables;

import org.spincast.core.exchange.VariablesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;

/**
 * Spincast Variables plugin module.
 */
public class SpincastVariablesPluginModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(VariablesRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastVariablesRequestContextAddon.class))
                                                                                .in(SpincastGuiceScopes.REQUEST);
    }
}

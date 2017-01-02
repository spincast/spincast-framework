package org.spincast.plugins.templatingaddon;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.templating.TemplatingRequestContextAddon;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Spincast Templating Addon plugin module.
 */
public class SpincastTemplatingAddonPluginModule extends SpincastGuiceModuleBase {

    public SpincastTemplatingAddonPluginModule() {
        super();
    }

    public SpincastTemplatingAddonPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                               Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
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

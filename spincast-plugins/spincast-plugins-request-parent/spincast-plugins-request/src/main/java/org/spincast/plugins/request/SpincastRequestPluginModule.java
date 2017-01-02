package org.spincast.plugins.request;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Guice module for the Spincast Request plugin.
 */
public class SpincastRequestPluginModule extends SpincastGuiceModuleBase {

    public SpincastRequestPluginModule() {
        super();
    }

    public SpincastRequestPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                       Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
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

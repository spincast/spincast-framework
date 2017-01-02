package org.spincast.plugins.response;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.ResponseRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Spincast Response plugin.
 */
public class SpincastResponsePluginModule extends SpincastGuiceModuleBase {

    public SpincastResponsePluginModule() {
        super();
    }

    public SpincastResponsePluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                        Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
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

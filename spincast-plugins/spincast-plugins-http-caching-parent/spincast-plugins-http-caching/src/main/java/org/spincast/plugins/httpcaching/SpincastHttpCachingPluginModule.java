package org.spincast.plugins.httpcaching;

import org.spincast.core.exchange.CacheHeadersRequestContextAddon;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Spincast HTTP Caching plugin module.
 */
public class SpincastHttpCachingPluginModule extends SpincastGuiceModuleBase {

    public SpincastHttpCachingPluginModule() {
        super();
    }

    public SpincastHttpCachingPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                           Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(CacheHeadersRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastCacheHeadersRequestContextAddon.class))
                                                                                   .in(SpincastGuiceScopes.REQUEST);
    }
}

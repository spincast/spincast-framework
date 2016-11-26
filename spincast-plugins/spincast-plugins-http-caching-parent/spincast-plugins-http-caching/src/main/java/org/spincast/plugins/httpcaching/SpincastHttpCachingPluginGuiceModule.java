package org.spincast.plugins.httpcaching;

import java.lang.reflect.Type;

import org.spincast.core.exchange.CacheHeadersRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

/**
 * Guice module for the Spincast HTTP Caching plugin.
 */
public class SpincastHttpCachingPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastHttpCachingPluginGuiceModule(Type requestContextType,
                                                Type websocketContextType) {
        super(requestContextType, websocketContextType);
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

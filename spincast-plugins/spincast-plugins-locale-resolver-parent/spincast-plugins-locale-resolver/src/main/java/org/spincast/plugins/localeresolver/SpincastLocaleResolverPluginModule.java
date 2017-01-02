package org.spincast.plugins.localeresolver;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

/**
 * Spincast Locale Resolver plugin module.
 */
public class SpincastLocaleResolverPluginModule extends SpincastGuiceModuleBase {

    public SpincastLocaleResolverPluginModule() {
        super();
    }

    public SpincastLocaleResolverPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                              Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bindLocaleResolver();
    }

    protected void bindLocaleResolver() {
        bind(LocaleResolver.class).to(LocaleResolverDefault.class).in(Scopes.SINGLETON);
    }

}

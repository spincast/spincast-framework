package org.spincast.plugins.cookies;

import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookiesRequestContextAddon;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Spincast Cookies plugin module.
 */
public class SpincastCookiesPluginModule extends SpincastGuiceModuleBase {

    public SpincastCookiesPluginModule() {
        super();
    }

    public SpincastCookiesPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                       Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
        bindCookieFactory();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(CookiesRequestContextAddon.class)).to(parameterizeWithRequestContext(SpincastCookiesRequestContextAddon.class))
                                                                              .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindCookieFactory() {
        install(new FactoryModuleBuilder().implement(Cookie.class, CookieDefault.class)
                                          .build(CookieFactory.class));
    }

}

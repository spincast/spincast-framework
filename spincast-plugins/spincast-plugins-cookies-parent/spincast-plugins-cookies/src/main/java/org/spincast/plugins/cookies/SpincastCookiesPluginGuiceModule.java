package org.spincast.plugins.cookies;

import java.lang.reflect.Type;

import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookiesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Cookies plugin.
 */
public class SpincastCookiesPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastCookiesPluginGuiceModule(Type requestContextType,
                                            Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
        bindCookieFactory();
    }

    protected void bindRequestContextAddon() {

        bind(parameterizeWithRequestContext(CookiesRequestContextAddon.class)).to(parameterizeWithContextInterfaces(SpincastCookiesRequestContextAddon.class))
                                                                               .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindCookieFactory() {
        install(new FactoryModuleBuilder().implement(Cookie.class, CookieDefault.class)
                                          .build(CookieFactory.class));
    }

}

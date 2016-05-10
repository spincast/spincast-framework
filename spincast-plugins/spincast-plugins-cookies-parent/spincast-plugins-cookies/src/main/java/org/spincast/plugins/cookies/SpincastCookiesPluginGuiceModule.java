package org.spincast.plugins.cookies;

import java.lang.reflect.Type;

import org.spincast.core.cookies.ICookie;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.cookies.ICookiesRequestContextAddon;
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
    public SpincastCookiesPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindRequestContextAddon();
        bindCookieFactory();
    }

    protected void bindRequestContextAddon() {

        bind(parametrizeWithRequestContextInterface(ICookiesRequestContextAddon.class))
                                                                                       .to(parametrizeWithRequestContextInterface(SpincastCookiesRequestContextAddon.class))
                                                                                       .in(SpincastGuiceScopes.REQUEST);
    }

    protected void bindCookieFactory() {
        install(new FactoryModuleBuilder().implement(ICookie.class, Cookie.class)
                                          .build(ICookieFactory.class));
    }

}

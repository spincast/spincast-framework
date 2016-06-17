package org.spincast.plugins.localeresolver;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.locale.ILocaleResolver;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Locale Resolver plugin.
 */
public class SpincastLocaleResolverPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastLocaleResolverPluginGuiceModule(Type requestContextType,
                                                   Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindLocaleResolver();
    }

    protected void bindLocaleResolver() {
        bind(ILocaleResolver.class).to(SpincastLocaleResolver.class).in(Scopes.SINGLETON);
    }

}

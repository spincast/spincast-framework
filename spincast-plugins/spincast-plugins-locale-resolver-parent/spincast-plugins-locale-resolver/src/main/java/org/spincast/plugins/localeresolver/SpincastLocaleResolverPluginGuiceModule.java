package org.spincast.plugins.localeresolver;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;

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
        bind(LocaleResolver.class).to(LocaleResolverDefault.class).in(Scopes.SINGLETON);
    }

}

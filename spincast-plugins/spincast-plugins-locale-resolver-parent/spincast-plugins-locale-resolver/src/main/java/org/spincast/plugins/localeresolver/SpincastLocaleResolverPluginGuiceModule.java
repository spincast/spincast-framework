package org.spincast.plugins.localeresolver;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.ILocaleResolver;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Locale Resolver plugin.
 */
public class SpincastLocaleResolverPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor
     */
    public SpincastLocaleResolverPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected void configure() {
        bindLocaleResolver();
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    protected void bindLocaleResolver() {
        bind(ILocaleResolver.class).to(SpincastLocaleResolver.class).in(Scopes.SINGLETON);
    }

}

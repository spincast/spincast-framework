package org.spincast.plugins.config;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Config plugin.
 */
public class SpincastConfigPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastConfigPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
    }

    @Override
    protected void configure() {
        bindSpincastConfig();
    }

    protected void bindSpincastConfig() {
        bind(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
        bind(ISpincastConfig.class).to(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ISpincastConfig> getSpincastConfigImplClass() {
        return SpincastConfig.class;
    }

}

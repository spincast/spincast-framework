package org.spincast.plugins.config;

import java.lang.reflect.Type;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Config plugin.
 */
public class SpincastConfigPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastConfigPluginGuiceModule(Type requestContextType,
                                           Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindSpincastConfig();
    }

    protected void bindSpincastConfig() {
        bind(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
        bind(SpincastConfig.class).to(getSpincastConfigImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastConfig> getSpincastConfigImplClass() {
        return SpincastConfigDefault.class;
    }

}

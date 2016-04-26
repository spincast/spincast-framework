package org.spincast.plugins.configpropsfile;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Config based on a properties file plugin.
 */
public class SpincastConfigPropsFilePluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastConfigPropsFilePluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
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
        return SpincastConfigPropsFileBased.class;
    }

}

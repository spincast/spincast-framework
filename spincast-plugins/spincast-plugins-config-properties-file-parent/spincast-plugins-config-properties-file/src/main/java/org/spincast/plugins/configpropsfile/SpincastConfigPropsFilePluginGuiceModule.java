package org.spincast.plugins.configpropsfile;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Config based on a properties file plugin.
 */
public class SpincastConfigPropsFilePluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastConfigPropsFilePluginGuiceModule(Type requestContextType,
                                                    Type websocketContextType) {
        super(requestContextType, websocketContextType);
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

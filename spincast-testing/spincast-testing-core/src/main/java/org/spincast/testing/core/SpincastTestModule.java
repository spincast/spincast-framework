package org.spincast.testing.core;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.SpincastCoreGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;

/**
 * Default module for Spincast tests. Binds a custom Spincast
 * configurations class.
 * 
 */
public class SpincastTestModule extends SpincastCoreGuiceModule {

    @Override
    protected void configure() {
        super.configure();
        bindTestConfig();
    }

    protected void bindTestConfig() {
        install(new SpincastConfigPluginGuiceModule(getRequestContextType()) {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigImplClass() {
                return SpincastTestConfig.class;
            }
        });
    }

}

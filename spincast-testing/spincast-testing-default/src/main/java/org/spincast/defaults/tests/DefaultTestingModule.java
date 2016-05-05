package org.spincast.defaults.tests;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;
import org.spincast.plugins.httpclient.SpincastHttpClientPluginGuiceModule;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.testing.core.SpincastTestConfig;

/**
 * Testing module using the default implementations
 * for the required components.
 */
public class DefaultTestingModule extends SpincastDefaultGuiceModule {

    public DefaultTestingModule() {
        this(null);
    }

    public DefaultTestingModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {
        super.configure();

        //==========================================
        // Install the Spincast Http Client Module
        //==========================================
        install(new SpincastHttpClientPluginGuiceModule(getRequestContextType()));
    }

    @Override
    protected void bindConfigPlugin() {
        install(new SpincastConfigPluginGuiceModule(getRequestContextType()) {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigImplClass() {
                return getSpincastConfigClass();
            }
        });
    }

    protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
        return SpincastTestConfig.class;
    }

    @Override
    protected Class<? extends ISpincastRouterConfig> getSpincastRoutingPluginConfigClass() {
        return SpincastTestSpincastRoutingPluginConfig.class;
    }

}

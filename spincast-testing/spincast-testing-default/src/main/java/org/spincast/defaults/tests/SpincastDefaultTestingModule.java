package org.spincast.defaults.tests;

import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketPluginGuiceModule;
import org.spincast.testing.core.SpincastTestConfig;

/**
 * Testing module using the default implementations
 * for the required components.
 */
public class SpincastDefaultTestingModule extends SpincastDefaultGuiceModule {

    public SpincastDefaultTestingModule() {
        this(null);
    }

    public SpincastDefaultTestingModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {
        super.configure();

        //==========================================
        // Install the Spincast HTTP Client with Websocket 
        // support module.
        //==========================================
        install(new SpincastHttpClientWithWebsocketPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    @Override
    protected void bindConfigPlugin() {
        install(new SpincastConfigPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

            @Override
            protected Class<? extends SpincastConfig> getSpincastConfigImplClass() {
                return getSpincastConfigClass();
            }
        });
    }

    protected Class<? extends SpincastConfig> getSpincastConfigClass() {
        return SpincastTestConfig.class;
    }

}

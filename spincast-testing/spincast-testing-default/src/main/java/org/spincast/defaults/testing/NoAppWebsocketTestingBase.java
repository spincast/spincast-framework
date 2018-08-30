package org.spincast.defaults.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

/**
 * Base class for WebSocket testing without an App.
 */
public abstract class NoAppWebsocketTestingBase extends
                                                AppBasedWebsocketTestingBase<DefaultRequestContext, DefaultWebsocketContext> {

    protected final Logger logger = LoggerFactory.getLogger(NoAppWebsocketTestingBase.class);

    @Override
    protected final AppTestingConfigs getAppTestingConfigs() {
        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return false;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return getTestingConfigImplClass();
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return null;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return null;
            }
        };
    }

    protected Class<? extends SpincastConfig> getTestingConfigImplClass() {
        return SpincastConfigTestingDefault.class;
    }

    @Override
    protected final void callAppMainMethod() {
        Spincast.configure()
                .bindCurrentClass(false)
                .init(getMainArgs());
    }

    protected String[] getMainArgs() {
        return null;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        beforeStartServer();

        //==========================================
        // Starts the Server
        //==========================================
        startServer();
    }

    protected void startServer() {
        getServer().start();
    }

    /**
     * Allows some initialization to be run once the Guice context
     * is created, but before the Server is started.
     */
    protected void beforeStartServer() {
        // nothing by default
    }

    /**
     * Ran before every test.
     */
    @Override
    public void beforeTest() {
        super.beforeTest();
        clearRoutes();
    }

    protected void clearRoutes() {
        getRouter().removeAllRoutes(removeSpincastRoutesToo());
        getServer().removeAllStaticResourcesServed();
    }

    /**
     * Should the default <em>Spincast Routes</em>
     * be removed too? Or only the custom ones?
     */
    protected boolean removeSpincastRoutesToo() {
        return false;
    }

}

package org.spincast.defaults.testing;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

/**
 * Base class for testing without an App but when an HTTP
 * server is required.
 */
public abstract class NoAppStartHttpServerCustomContextTypesTestingBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                                                       extends AppBasedTestingBase<R, W> {

    @Override
    protected final AppTestingConfigs getAppTestingConfigs() {
        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return false;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return getTestingConfigImplementationClass2();
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

    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return SpincastConfigTestingDefault.class;
    }

    @Override
    protected final void startApp() {
        createBootstrapper().init(getMainArgs());
    }

    protected SpincastBootstrapper createBootstrapper() {
        return Spincast.configure()
                       .bindCurrentClass(false)
                       .requestContextImplementationClass(getRequestContextImplementationClass())
                       .websocketContextImplementationClass(getWebsocketContextImplementationClass());
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

    protected abstract Class<? extends RequestContext<?>> getRequestContextImplementationClass();

    protected abstract Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass();

}

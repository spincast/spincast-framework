package org.spincast.testing.core;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Base class for Spincast tests which creates
 * the Guice injector using a specified module.
 * 
 * Automatically starts the HTTP server.
 * 
 * Removes all routes and static resources before each test.
 * 
 */
public abstract class SpincastGuiceModuleBasedIntegrationTestBase<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                                                 extends SpincastIntegrationTestBase<R, W> {

    @Override
    protected Injector createInjector() {

        Module overridingModule = getOverridingModule();
        if(overridingModule == null) {
            return Guice.createInjector(getTestingModule());
        } else {
            return Guice.createInjector(Modules.override(getTestingModule())
                                               .with(overridingModule));
        }
    }

    /**
     * An overriding module to use?
     */
    protected Module getOverridingModule() {
        return null;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        beforeStartServer();

        //==========================================
        // Since this is a Module based integration test,
        // the server have to be started manually.
        //==========================================
        startServer();
    }

    /**
     * Allows some initialization to be run once the Guice context
     * is created, but before the server is started.
     */
    protected void beforeStartServer() {
        // nothing by default
    }

    /**
     * Ran before each test.
     */
    @Override
    public void beforeTest() {
        super.beforeTest();
        clearRoutes();
    }

    protected void startServer() {
        getServer().start();
    }

    protected void clearRoutes() {
        getRouter().removeAllRoutes();
        getServer().removeAllStaticResourcesServed();
    }

    /**
     * The testing module to use.
     */
    public abstract Module getTestingModule();

}

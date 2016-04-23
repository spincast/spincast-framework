package org.spincast.testing.core;

import org.spincast.core.exchange.IRequestContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Base class for Spincast tests which creates
 * the Guice injector using a specified module.
 * 
 * Automatically starts the HTTP server.
 * 
 * Removes all routes and static resources before each test.
 * 
 */
public abstract class SpincastGuiceModuleBasedIntegrationTestBase<R extends IRequestContext<?>> extends SpincastIntegrationTestBase<R> {

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(getTestingModule());
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        //==========================================
        // Since this is a Module based integration test,
        // the server have to be started manually.
        //==========================================
        startServer();
    }

    /**
     * Ran before each test.
     */
    @Override
    public void before() {
        super.before();
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

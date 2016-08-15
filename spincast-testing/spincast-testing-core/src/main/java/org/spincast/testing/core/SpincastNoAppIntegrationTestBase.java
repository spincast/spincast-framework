package org.spincast.testing.core;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Base class for tests that needs the HTTP/WebSocket server
 * to be started, but that are not using the boostraping
 * process of an existing application to create the Guice context
 * and start the server.
 * <p>
 * In other words, instead of calling an application's 
 * <code>createApp(...)</code> method to create the Guice context
 * and start the server, this class bootstarts eveything by itself
 * using <code>Guice.createInjector(...)</code> directly. The
 * HTTP/WebSocket server is started automatically.
 * </p>
 * <p>
 * Also, this class will clean everything related to routing
 * before each test (routes, static resources, etc.). 
 * When you test an existing application,
 * by extending  <code>SpincastIntegrationTestBase</code> directly,
 * you usually don't want to clean the routes of the application
 * before each test, since you want to test the application as it
 * is created. But here, you start each test with a fresh and
 * empty router.
 * </p>
 * 
 */
public abstract class SpincastNoAppIntegrationTestBase<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
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
     * The main Guice module to use.
     */
    public abstract Module getTestingModule();

    /**
     * An overriding module to use?
     */
    protected Module getOverridingModule() {
        return null;
    }

    protected String[] getMainArgsToUse() {
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
        getRouter().removeAllRoutes(removeSpincastRoutesToo());
        getServer().removeAllStaticResourcesServed();
    }

    protected boolean removeSpincastRoutesToo() {
        return false;
    }

}

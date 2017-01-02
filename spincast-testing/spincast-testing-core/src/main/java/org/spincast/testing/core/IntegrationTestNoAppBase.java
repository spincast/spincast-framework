package org.spincast.testing.core;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Base class for integration testing when there is no
 * Application to start via a <code>main()</code> method.
 * In that case, the Guice Injector must be provided
 * explicitly.
 * <p>
 * The Server will be explicitly started since no app
 * is going to do so by itself.
 */
public abstract class IntegrationTestNoAppBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                              extends IntegrationTestBase<R, W> {

    /**
     * Since we do not call "App.main(...)" in test
     * class in this branch, we don't need to bind
     * the current class to the Guice context : the
     * Server will be started explicitly and the
     * @Test methods called explicitly.
     */
    @Override
    protected GuiceTweaker createGuiceTweaker() {

        GuiceTweaker guiceTweaker = super.createGuiceTweaker();
        if(guiceTweaker == null) {
            guiceTweaker = new GuiceTweaker();
        }
        guiceTweaker.bindCurrentClassByDefault(false);

        return guiceTweaker;
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

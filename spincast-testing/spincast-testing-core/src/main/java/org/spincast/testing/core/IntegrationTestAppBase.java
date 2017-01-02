package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;

/**
 * Base for integration test classes that use an existing
 * Application to start the Server (calling its <code>main()</code>
 * method).
 * <p>
 * This class needs to be parametrized with the <code>Request context</code> type
 * and <code>WebSocket Context</code> type to use.
 */
public abstract class IntegrationTestAppBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                            extends IntegrationTestBase<R, W> {

    /**
     * final method : it is not allowed to disable the Guice Tweaker
     * when an App is used since it is the only way to get
     * the Injector!
     */
    @Override
    protected final boolean isEnableGuiceTweaker() {
        return true;
    }

    /**
     * At this level, <code>createInjector()</code> expects
     * your application to use the <code>Spincast</code> utility class
     * from the <code>spincast-default</code> artifact to initialize 
     * your app. It is going to use a
     * {@link GuiceTweaker} ThreadLocal variable to
     * tweak how the Guice context is created and to retrieve
     * the resulting Guice injector.
     * <p>
     * If your application doesn't use the <code>Spincast</code> utility class
     * from the <code>spincast-default</code> artifact to initialize 
     * your app, you should probably override this method and it's up to you
     * return the proper Guice Injector to use.
     */
    @Override
    protected Injector createInjector() {

        //==========================================
        // Starts the app!
        //==========================================
        initApp();

        //==========================================
        // The Guice injector should now have been added
        // to the SpincastPluginThreadLocal...
        //==========================================
        Injector injector = getSpincastPluginFromThreadLocal().getInjector();
        assertNotNull(injector);

        return injector;
    }

    /**
     * Starts the application.
     */
    protected abstract void initApp();

}

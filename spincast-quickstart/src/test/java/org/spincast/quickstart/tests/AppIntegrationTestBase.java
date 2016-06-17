package org.spincast.quickstart.tests;

import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.quickstart.exchange.IAppWebsocketContext;
import org.spincast.testing.core.SpincastIntegrationTestBase;

import com.google.inject.Injector;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppIntegrationTestBase extends SpincastIntegrationTestBase<IAppRequestContext, IAppWebsocketContext> {

    /**
     * Creates the application and returns the Guice
     * injector.
     */
    @Override
    protected Injector createInjector() {

        //==========================================
        // Make sure you add the Guice Module returned by
        // getTestOverridingModule(...) as an overriding Module!
        // You can also modify it, before adding it.
        //==========================================
        return App.createApp(getMainArgs(), getTestOverridingModule(IAppRequestContext.class, IDefaultWebsocketContext.class));
    }

    protected String[] getMainArgs() {
        return null;
    }
}

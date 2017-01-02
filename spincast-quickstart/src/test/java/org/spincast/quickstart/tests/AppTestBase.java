package org.spincast.quickstart.tests;

import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppWebsocketContext;
import org.spincast.testing.core.IntegrationTestAppBase;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppTestBase extends IntegrationTestAppBase<AppRequestContext, AppWebsocketContext> {

    @Override
    protected void initApp() {
        App.main(getMainArgs());
    }

    protected String[] getMainArgs() {
        return null;
    }
}

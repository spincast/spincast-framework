package org.spincast.quickstart.tests;

import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.testing.core.SpincastIntegrationTestBase;

import com.google.inject.Injector;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppIntegrationTestBase extends SpincastIntegrationTestBase<IAppRequestContext> {

    /**
     * Create the application and return the Guice
     * injector.
     */
    @Override
    protected Injector createInjector() {
        return App.createApp();
    }
}

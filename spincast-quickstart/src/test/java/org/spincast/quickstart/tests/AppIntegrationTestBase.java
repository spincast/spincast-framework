package org.spincast.quickstart.tests;

import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.quickstart.exchange.IAppWebsocketContext;
import org.spincast.testing.core.SpincastIntegrationTestBase;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

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
        return App.createApp(getMainArgs(), getOverridingModule());
    }

    protected String[] getMainArgs() {
        return null;
    }

    /**
     * We create our overriding module. 
     * 
     * It is recommended to always kept the default overriding module by combining it with
     * our custom one.
     */
    protected Module getOverridingModule() {

        Module defaultOverridingModule = getDefaultOverridingModule(IAppRequestContext.class,
                                                                    IAppWebsocketContext.class);

        Module appOverridingModule = new AbstractModule() {

            @Override
            protected void configure() {

                // your tests bindings...
            }
        };

        return Modules.combine(defaultOverridingModule, appOverridingModule);
    }

}

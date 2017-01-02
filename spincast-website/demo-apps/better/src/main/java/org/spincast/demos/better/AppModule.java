package org.spincast.demos.better;

import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        // Binds our controller
        bind(AppController.class).in(Scopes.SINGLETON);

        // ... binds all our other application components here
    }
}

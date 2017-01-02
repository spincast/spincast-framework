package org.spincast.demos.supercalifragilisticexpialidocious;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        // Binds our controller
        bind(AppController.class).asEagerSingleton();

        // Overrides the default configuration binding
        bind(SpincastConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);

        // ... other bindings
    }
}

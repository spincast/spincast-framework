package org.spincast.quickstart.guice;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.config.AppConfigDefault;
import org.spincast.quickstart.controller.AppController;
import org.spincast.quickstart.exchange.AppRouter;
import org.spincast.quickstart.exchange.AppRouterDefault;

import com.google.inject.Scopes;

/**
 * Guice module for the application.
 */
public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        //==========================================
        // Bind our configuration implementation class to the
        // AppConfig interface. Spincast will automatically
        // find and use this implementation for the "SpincastConfig"
        // binding too.
        //==========================================
        bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our Router implementation to our custom
        // AppRouter interface. Spincast will automatically
        // find and use this implementation for the default Router
        // bindings too.
        //==========================================
        bind(AppRouter.class).to(AppRouterDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our controller.
        //==========================================
        bind(AppController.class).in(Scopes.SINGLETON);
    }

}

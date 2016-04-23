package org.spincast.quickstart.guice;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.quickstart.App;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.config.IAppConfig;
import org.spincast.quickstart.controller.AppController;
import org.spincast.quickstart.controller.IAppController;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppRouter;
import org.spincast.quickstart.exchange.IAppRouter;

import com.google.inject.Key;
import com.google.inject.Scopes;

/**
 * Guice module for the application.
 * It extends SpincastDefaultGuiceModule so we start with
 * all the default bindings.
 */
public class AppModule extends SpincastDefaultGuiceModule {

    /**
     * Constructor
     */
    public AppModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {
        super.configure();

        //==========================================
        // One instance only of our configuration class.
        //==========================================
        bind(AppConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our configuration implementation class to our
        // IAppConfig interface.
        //==========================================
        bind(IAppConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // One instance only of our router class.
        //==========================================
        bind(AppRouter.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our router implementation to our custom
        // and already parameterized "IAppRouter" interface.
        //==========================================
        bind(IAppRouter.class).to(AppRouter.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our controller.
        //==========================================
        bind(IAppController.class).to(AppController.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind the App itself.
        //==========================================
        bind(App.class).in(Scopes.SINGLETON);
    }

    /**
     * Tells Spincast to use our custom request context type
     * instead of the default one. Spincast will automatically find the
     * associated interface, "IAppRequestContext", and will use
     * it to parameterize some of the components, like "IRouter".
     */
    @Override
    protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
        return AppRequestContext.class;
    }

    /**
     * Instead of installing the default "spincast-default-plugin" plugin,
     * which only provides hardcoded values for the Spincast configurations,
     * we directly bind the required "ISpincastConfig" component to our
     * custom implementation class.
     */
    @Override
    protected void bindConfigPlugin() {
        bind(ISpincastConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);
    }

    /**
     * Inline, we override the router implementation bound by the 
     * "spincast-routing" plugin, so our custom class is used.
     */
    @Override
    protected void installRoutingPlugin() {
        install(new SpincastRoutingPluginGuiceModule(getRequestContextType()) {

            @Override
            protected Key<?> getRouterKey() {
                return Key.get(AppRouter.class);
            }
        });
    }

}

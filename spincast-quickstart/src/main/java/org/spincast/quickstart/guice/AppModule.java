package org.spincast.quickstart.guice;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.quickstart.App;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.config.AppConfigDefault;
import org.spincast.quickstart.controller.AppController;
import org.spincast.quickstart.exchange.AppRequestContextDefault;
import org.spincast.quickstart.exchange.AppRouter;
import org.spincast.quickstart.exchange.AppRouterDefault;
import org.spincast.quickstart.exchange.AppWebsocketContextDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;

/**
 * Guice module for the application.
 * 
 * It extends SpincastDefaultGuiceModule so starts with
 * all the default bindings.
 */
public class AppModule extends SpincastDefaultGuiceModule {

    public AppModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {
        super.configure();

        //==========================================
        // One instance only of our configuration object.
        //==========================================
        bind(AppConfigDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our configuration implementation class to the
        // AppConfig interface.
        //==========================================
        bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // One instance only of our Router class.
        //==========================================
        bind(AppRouterDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our Router implementation to our custom
        // and already parameterized "AppRouter" interface.
        //==========================================
        bind(AppRouter.class).to(AppRouterDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our controller.
        //==========================================
        bind(AppController.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind the App itself.
        //==========================================
        bind(App.class).in(Scopes.SINGLETON);
    }

    /**
     * Tells Spincast to use our custom Request Context type
     * instead of the default one. Spincast will automatically find the
     * associated interface, "AppRequestContext", and will use
     * it to parameterize some of the components, like "Router".
     */
    @Override
    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return AppRequestContextDefault.class;
    }

    /**
     * Tells Spincast to use our custom Websocket Context type
     * instead of the default one. Spincast will automatically find the
     * associated interface, "AppWebsocketContext", and will use
     * it to parameterize some of the components, like "Router".
     */
    @Override
    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return AppWebsocketContextDefault.class;
    }

    /**
     * Instead of installing the default "spincast-default-plugin" plugin,
     * which only provides hardcoded values for the Spincast configurations,
     * we directly bind the required "SpincastConfig" component to our
     * custom implementation class.
     */
    @Override
    protected void bindConfigPlugin() {
        bind(SpincastConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);
    }

    /**
     * Inline, we override the Router implementation bound by the 
     * "spincast-routing" plugin, so our custom class is used.
     */
    @Override
    protected void bindRoutingPlugin() {
        install(new SpincastRoutingPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

            @Override
            protected Key<?> getRouterImplementationKey() {
                return Key.get(AppRouterDefault.class);
            }
        });
    }

}

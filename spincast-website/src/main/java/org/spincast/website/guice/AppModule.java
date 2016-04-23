package org.spincast.website.guice;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.IAppConfig;
import org.spincast.website.controllers.AppController;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Scopes;

public class AppModule extends SpincastDefaultGuiceModule {

    public AppModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {
        super.configure();

        //==========================================
        // The application itself
        //==========================================
        bind(App.class).in(Scopes.SINGLETON);

        //==========================================
        // The application config
        //==========================================
        bind(IAppConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // The application controllers
        //==========================================
        bind(AppController.class).in(Scopes.SINGLETON);
        bind(ErrorController.class).in(Scopes.SINGLETON);

    }

    /**
     * We use our application config instead of the Spincast
     * default config.
     */
    @Override
    protected void bindConfigPlugin() {
        install(new SpincastConfigPluginGuiceModule(getRequestContextType()) {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigImplClass() {
                return AppConfig.class;
            }
        });
    }

    @Override
    protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
        return AppRequestContext.class;
    }

}

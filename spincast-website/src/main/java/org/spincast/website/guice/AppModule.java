package org.spincast.website.guice;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.plugins.pebble.ISpincastPebbleTemplatingEngineConfig;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConfigPropsFileBasedConfig;
import org.spincast.website.AppPebbleTemplatingEngineConfig;
import org.spincast.website.HttpAuthInit;
import org.spincast.website.IAppConfig;
import org.spincast.website.controllers.AppController;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.controllers.FeedController;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.exchange.AppRouter;
import org.spincast.website.exchange.IAppRouter;
import org.spincast.website.pebble.AppPebbleExtension;
import org.spincast.website.repositories.INewsRepository;
import org.spincast.website.repositories.TemplateFilesRepository;
import org.spincast.website.services.INewsService;
import org.spincast.website.services.NewsService;

import com.google.inject.Key;
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
        // Initilizes the HTTP authentication.
        // Eager singleton!
        //==========================================
        bind(HttpAuthInit.class).asEagerSingleton();

        //==========================================
        // Bind custom configurations for the .properties 
        // file based config plugin.
        //==========================================
        bind(ISpincastConfigPropsFileBasedConfig.class).to(AppConfigPropsFileBasedConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind custom configurations for the Pebble plugin.
        //==========================================
        bind(AppPebbleExtension.class).in(Scopes.SINGLETON);
        bind(ISpincastPebbleTemplatingEngineConfig.class).to(AppPebbleTemplatingEngineConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // The application config
        //==========================================
        bind(IAppConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // The application controllers
        //==========================================
        bind(AppController.class).in(Scopes.SINGLETON);
        bind(ErrorController.class).in(Scopes.SINGLETON);
        bind(FeedController.class).in(Scopes.SINGLETON);

        //==========================================
        // The application services and repositories
        //==========================================
        bind(INewsService.class).to(NewsService.class).in(Scopes.SINGLETON);
        bind(INewsRepository.class).to(TemplateFilesRepository.class).in(Scopes.SINGLETON);

        //==========================================
        // One instance only of our router class.
        //==========================================
        bind(AppRouter.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our router implementation to our custom
        // and already parameterized "IAppRouter" interface.
        //==========================================
        bind(IAppRouter.class).to(AppRouter.class).in(Scopes.SINGLETON);
    }

    /**
     * We use our application config class instead of the 
     * default one for the ISpincastConfig interface.
     */
    @Override
    protected void bindConfigPlugin() {
        install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

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

    /**
     * Inline, we override the router implementation bound by the 
     * "spincast-routing" plugin, so our custom class is used.
     */
    @Override
    protected void bindRoutingPlugin() {
        install(new SpincastRoutingPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

            @Override
            protected Key<?> getRouterImplementationKey() {
                return Key.get(AppRouter.class);
            }
        });
    }

}

package org.spincast.website.guice;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConfigDefault;
import org.spincast.website.AppConfigPropsFileBasedConfig;
import org.spincast.website.AppPebbleTemplatingEngineConfig;
import org.spincast.website.HttpAuthInit;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.controllers.FeedController;
import org.spincast.website.controllers.MainPagesController;
import org.spincast.website.controllers.WebsocketsDemoEchoAllController;
import org.spincast.website.exchange.AppRouter;
import org.spincast.website.exchange.AppRouterDefault;
import org.spincast.website.pebble.AppPebbleExtension;
import org.spincast.website.repositories.NewsRepository;
import org.spincast.website.repositories.TemplateFilesRepository;
import org.spincast.website.services.NewsService;
import org.spincast.website.services.NewsServiceDefault;

import com.google.inject.Scopes;

public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        //==========================================
        // Initilizes the HTTP authentication.
        // Eager singleton!
        //==========================================
        bind(HttpAuthInit.class).asEagerSingleton();

        //==========================================
        // Bind custom configurations for the .properties 
        // file based config plugin.
        //==========================================
        bind(SpincastConfigPropsFilePluginConfig.class).to(AppConfigPropsFileBasedConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind custom configurations for the Pebble plugin.
        //==========================================
        bind(AppPebbleExtension.class).in(Scopes.SINGLETON);
        bind(SpincastPebbleTemplatingEngineConfig.class).to(AppPebbleTemplatingEngineConfig.class).in(Scopes.SINGLETON);

        //==========================================
        // The application config
        //==========================================
        bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);

        //==========================================
        // The application controllers
        //==========================================
        bind(MainPagesController.class).in(Scopes.SINGLETON);
        bind(ErrorController.class).in(Scopes.SINGLETON);
        bind(FeedController.class).in(Scopes.SINGLETON);
        bind(WebsocketsDemoEchoAllController.class).asEagerSingleton(); // init() method

        //==========================================
        // The application services and repositories
        //==========================================
        bind(NewsService.class).to(NewsServiceDefault.class).in(Scopes.SINGLETON);
        bind(NewsRepository.class).to(TemplateFilesRepository.class).in(Scopes.SINGLETON);

        //==========================================
        // Bind our router implementation to our custom
        // and already parameterized "AppRouter" interface.
        //==========================================
        bind(AppRouter.class).to(AppRouterDefault.class).in(Scopes.SINGLETON);
    }

}

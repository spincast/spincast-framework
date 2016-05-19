package org.spincast.website.guice;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConfigPropsFileBasedConfig;
import org.spincast.website.IAppConfig;
import org.spincast.website.controllers.AppController;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.controllers.FeedController;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.repositories.HardcodedNewsRepository;
import org.spincast.website.repositories.INewsRepository;
import org.spincast.website.services.INewsService;
import org.spincast.website.services.NewsService;

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
        // Bind custom configurations fo the .properties 
        // file based config plugin.
        //==========================================
        bind(ISpincastConfigPropsFileBasedConfig.class).to(AppConfigPropsFileBasedConfig.class).in(Scopes.SINGLETON);

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
        bind(INewsRepository.class).to(HardcodedNewsRepository.class).in(Scopes.SINGLETON);

    }

    /**
     * We use our application config class instead of the 
     * default one for the ISpincastConfig interface.
     */
    @Override
    protected void bindConfigPlugin() {
        install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType()) {

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

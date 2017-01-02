package org.spincast.quickstart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.controller.AppController;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppRequestContextDefault;
import org.spincast.quickstart.exchange.AppRouter;
import org.spincast.quickstart.exchange.AppWebsocketContextDefault;
import org.spincast.quickstart.guice.AppModule;

import com.google.inject.Inject;

/**
 * Application main class.
 */
public class App {

    protected final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Main method
     */
    public static void main(String[] args) {

        /**
         * We use the bootstrapper to initialize
         * our application.
         */
        Spincast.configure()
                .module(new AppModule())
                .plugin(new SpincastHttpClientPlugin())
                .requestContextImplementationClass(AppRequestContextDefault.class)
                .websocketContextImplementationClass(AppWebsocketContextDefault.class)
                .mainArgs(args)
                .init();
    }

    /**
     * Init method, automatically called when the Guice context is ready.
     */
    @Inject
    protected void init(Server server,
                        AppConfig config,
                        AppRouter router,
                        AppController appController,
                        SpincastFilters<AppRequestContext> spincastFilters) {

        addRoutes(router, appController, spincastFilters);

        server.start();
        this.logger.info("Server started...");

        displayStartedMessage(config);
    }

    /**
     * Route definitions
     */
    protected void addRoutes(final AppRouter router,
                             final AppController ctrl,
                             final SpincastFilters<AppRequestContext> spincastFilters) {

        //==========================================
        // Serves everything under "/public" as 
        // Static Resources.
        //==========================================
        router.dir("/public").classpath("/public").save();

        //==========================================
        // Some common files which are not expected to
        // be under a "/public" URL.
        //==========================================
        router.file("/favicon.ico").classpath("/public/favicon.ico").save();
        router.file("/robots.txt").classpath("/public/robots.txt").save();
        router.file("/humans.txt").classpath("/public/humans.txt").save();
        router.file("/browserconfig.xml").classpath("/public/browserconfig.xml").save();
        router.file("/apple-touch-icon.png").classpath("/public/apple-touch-icon.png").save();
        router.file("/tile-wide.png").classpath("/public/tile-wide.png").save();
        router.file("/tile.png").classpath("/public/tile.png").save();

        //==========================================
        // Add some security headers on every route
        //==========================================
        router.before().save(spincastFilters::addSecurityHeaders);

        //==========================================
        // Index page, can be cached.
        //==========================================
        router.GET("/").cache(3600).save(ctrl::index);

        //==========================================
        // Movie Info page
        //==========================================
        router.GET("/movie").save(ctrl::movieInfo);

        //==========================================
        // Exception example
        //==========================================
        router.GET("/exception-example").save(ctrl::exceptionExample);

        //==========================================
        // "Not Found" handler
        //==========================================
        router.notFound(ctrl::notFound);

        //==========================================
        // Exceptions handler
        //==========================================
        router.exception(ctrl::exception);
    }

    protected void displayStartedMessage(AppConfig config) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("Spincast Quick Start application started!");
        System.out.println(config.getPublicServerSchemeHostPort());
        System.out.println("==========================================");
    }

}

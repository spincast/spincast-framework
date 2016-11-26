package org.spincast.quickstart;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.routing.Handler;
import org.spincast.core.server.Server;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.controller.AppController;
import org.spincast.quickstart.exchange.AppHandler;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppRouter;
import org.spincast.quickstart.guice.AppModule;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * The main class of the application. Everything starts with the
 * <code>main(...)</code> method.
 */
public class App {

    protected final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * The entry point for the application.
     */
    public static void main(String[] args) {
        createApp(args, null);
    }

    /**
     * Creates an App instance using the given
     * parameters, an overriding module, and returns the 
     * Guice injector.
     * 
     * @param overridingModule Mostly useful for the integration tests. Those
     * can override some bindings by specifying this overriding module.
     */
    public static Injector createApp(String[] args, Module overridingModule) {

        if(args == null) {
            args = new String[]{};
        }

        //==========================================
        // Should we override the base app modules
        // with an overring module?
        //==========================================
        Injector guice = null;
        if(overridingModule != null) {
            guice = Guice.createInjector(Modules.override(getAppModules(args))
                                                .with(overridingModule));
        } else {
            guice = Guice.createInjector(getAppModules(args));
        }

        App app = guice.getInstance(App.class);
        app.start();

        return guice;
    }

    /**
     * The app's Guice modules to use.
     */
    protected static List<? extends Module> getAppModules(String[] args) {
        return Lists.newArrayList(new AppModule(args));
    }

    //==========================================
    // The application
    //==========================================
    private final Server server;
    private final AppConfig appConfig;
    private final AppRouter router;
    private final AppController appController;
    private final SpincastFilters<AppRequestContext> spincastFilters;

    /**
     * The application constructor that Guice will call
     * with the required dependencies.
     */
    @Inject
    public App(Server server,
               AppConfig config,
               AppRouter router,
               AppController appController,
               SpincastFilters<AppRequestContext> spincastFilters) {
        this.server = server;
        this.appConfig = config;
        this.router = router;
        this.appController = appController;
        this.spincastFilters = spincastFilters;
    }

    protected AppConfig getConfig() {
        return this.appConfig;
    }

    protected Server getServer() {
        return this.server;
    }

    protected AppRouter getRouter() {
        return this.router;
    }

    protected AppController getAppController() {
        return this.appController;
    }

    protected SpincastFilters<AppRequestContext> getSpincastFilters() {
        return this.spincastFilters;
    }

    /**
     * Once the application instance is created by Guice, start() is called
     * and the real work begins!
     */
    protected void start() {

        //==========================================
        // We add the application routes.
        //==========================================
        addRoutes();

        //==========================================
        // We start the server!
        //==========================================
        getServer().start();

        //==========================================
        // We display a "started" message.
        //==========================================
        displayStartedMessage();
    }

    /**
     * Adds the application routes.
     */
    protected void addRoutes() {

        //==========================================
        // A GET route for the index page.
        //
        // Note that, with Java 8, we could simply use 
        // a method handler :
        //
        // getRouter().GET("/", getAppController()::indexPage);
        //
        // Or a Lambda :
        //
        // getRouter().GET("/").save(context -> getAppController().indexPage(context));
        //
        //==========================================
        getRouter().GET("/").cache(3600).save(new AppHandler() {

            @Override
            public void handle(AppRequestContext context) {
                getAppController().indexPage(context);
            }
        });

        //==========================================
        // Another GET route. 
        // Note that here we are using "Handler<AppRequestContext>" 
        // instead of "AppHandler" for the handler type : 
        // they are equivalent.
        //==========================================
        getRouter().GET("/greet/${name}").save(new Handler<AppRequestContext>() {

            @Override
            public void handle(AppRequestContext context) {

                // Example call to a method we added on our
                // custom Request Context type
                context.customGreetingMethod();
            }
        });

        //==========================================
        // A POST route. 
        // This route is the one used in Spincast's 
        // documentation to demonstrate how to write a test.
        //==========================================
        getRouter().POST("/sum").save(new AppHandler() {

            @Override
            public void handle(AppRequestContext context) {
                getAppController().sumRoute(context);
            }
        });

        //==========================================
        // "Not Found" handler
        //==========================================
        getRouter().notFound(new AppHandler() {

            @Override
            public void handle(AppRequestContext context) {
                getAppController().notFound(context);
            }
        });

        //==========================================
        // Exceptions handler
        //==========================================
        getRouter().exception(new AppHandler() {

            @Override
            public void handle(AppRequestContext context) {
                getAppController().exception(context);
            }
        });

        //==========================================
        // Serves everything under "/public" as
        // static resources.
        //==========================================
        getRouter().dir("/public").classpath("/public").save();

        //==========================================
        // Some default files which
        // are not under the "/public" URL.
        //==========================================
        getRouter().file("/favicon.ico").classpath("/public/favicon.ico").save();
        getRouter().file("/robots.txt").classpath("/public/robots.txt").save();
        getRouter().file("/humans.txt").classpath("/public/humans.txt").save();
        getRouter().file("/browserconfig.xml").classpath("/public/browserconfig.xml").save();
        getRouter().file("/apple-touch-icon.png").classpath("/public/apple-touch-icon.png").save();
        getRouter().file("/tile-wide.png").classpath("/public/tile-wide.png").save();
        getRouter().file("/tile.png").classpath("/public/tile.png").save();

        //==========================================
        // Add some security headers on every route.
        //==========================================
        getRouter().before().save(new AppHandler() {

            @Override
            public void handle(AppRequestContext context) {
                getSpincastFilters().addSecurityHeaders(context);
            }
        });
    }

    /**
     * Displays a message when the application is started.
     */
    protected void displayStartedMessage() {
        System.out.println();
        System.out.println("==========================================");
        System.out.print("Spincast quick start application started on ");

        if(getConfig().getHttpServerPort() > 0) {
            System.out.print("port " + getConfig().getHttpServerPort());
        }
        if(getConfig().getHttpsServerPort() > 0) {
            if(getConfig().getHttpServerPort() > 0) {
                System.out.print(" and ");
            }
            System.out.print("port " + getConfig().getHttpsServerPort() + " (HTTPS)");
        }
        System.out.println();
        System.out.println("Try hitting the index page or \"/greet/[YourName]\"...");
        System.out.println("==========================================");
    }

}

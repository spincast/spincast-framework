package org.spincast.quickstart;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.IHandler;
import org.spincast.core.server.IServer;
import org.spincast.quickstart.config.IAppConfig;
import org.spincast.quickstart.controller.IAppController;
import org.spincast.quickstart.exchange.IAppHandler;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.quickstart.exchange.IAppRouter;
import org.spincast.quickstart.guice.AppModule;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * The main class of the application. Everything starts with the
 * classic <code>main(...)</code> method.
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
    private final IServer server;
    private final IAppConfig appConfig;
    private final IAppRouter router;
    private final IAppController appController;
    private final ISpincastFilters<IAppRequestContext> spincastFilters;

    /**
     * The application constructor which Guice will call
     * with the required dependencies.
     */
    @Inject
    public App(IServer server,
               IAppConfig config,
               IAppRouter router,
               IAppController appController,
               ISpincastFilters<IAppRequestContext> spincastFilters) {
        this.server = server;
        this.appConfig = config;
        this.router = router;
        this.appController = appController;
        this.spincastFilters = spincastFilters;
    }

    protected IAppConfig getConfig() {
        return this.appConfig;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected IAppRouter getRouter() {
        return this.router;
    }

    protected IAppController getAppController() {
        return this.appController;
    }

    protected ISpincastFilters<IAppRequestContext> getSpincastFilters() {
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
        // Note that with Java 8, we could simply use 
        // a method handler:
        //
        // getRouter().GET("/", getAppController()::indexPage);
        //
        // Or a Lambda:
        //
        // getRouter().GET("/").save(context -> getAppController().indexPage(context));
        //
        //==========================================
        getRouter().GET("/").save(new IAppHandler() {

            @Override
            public void handle(IAppRequestContext context) {
                getAppController().indexPage(context);
            }
        });

        //==========================================
        // Another GET route. 
        // Note that here we use 
        // "IHandler<IAppRequestContext>" instead of 
        // "IAppHandler" for the handler type: 
        // they are equivalent.
        //==========================================
        getRouter().GET("/greet/${name}").save(new IHandler<IAppRequestContext>() {

            @Override
            public void handle(IAppRequestContext context) {
                // Call a method we added on our
                // custom request context type...
                context.customGreetingMethod();
            }
        });

        //==========================================
        // A POST route. 
        // This route in used in Spincast documentation
        // to demonstrate how to write a test.
        //==========================================
        getRouter().POST("/sum").save(new IAppHandler() {

            @Override
            public void handle(IAppRequestContext context) {
                getAppController().sumRoute(context);
            }
        });

        //==========================================
        // "Not Found" handler
        //==========================================
        getRouter().notFound(new IAppHandler() {

            @Override
            public void handle(IAppRequestContext context) {
                getAppController().notFound(context);
            }
        });

        //==========================================
        // Exceptions handler
        //==========================================
        getRouter().exception(new IAppHandler() {

            @Override
            public void handle(IAppRequestContext context) {
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
        getRouter().before(new IAppHandler() {

            @Override
            public void handle(IAppRequestContext context) {
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

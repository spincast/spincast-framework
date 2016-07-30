package org.spincast.website;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.website.controllers.AdminController;
import org.spincast.website.controllers.DemosTutorialsController;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.controllers.FeedController;
import org.spincast.website.controllers.MainPagesController;
import org.spincast.website.controllers.WebsocketsDemoEchoAllController;
import org.spincast.website.exchange.IAppRequestContext;
import org.spincast.website.exchange.IAppRouter;
import org.spincast.website.guice.AppModule;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

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

        App website = guice.getInstance(App.class);
        website.start();

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
    private final MainPagesController appController;
    private final ErrorController errorController;
    private final FeedController feedController;
    private final AdminController adminController;
    private final DemosTutorialsController demosTutorialsControllerController;
    private final ISpincastFilters<IAppRequestContext> spincastFilters;
    private final WebsocketsDemoEchoAllController websocketsDemoEchoAllController;

    @Inject
    public App(IServer server,
               IAppConfig config,
               IAppRouter router,
               MainPagesController appController,
               ErrorController errorController,
               FeedController feedController,
               AdminController adminController,
               DemosTutorialsController demosTutorialsControllerController,
               ISpincastFilters<IAppRequestContext> spincastFilters,
               WebsocketsDemoEchoAllController websocketsDemoEchoAllController) {
        this.server = server;
        this.appConfig = config;
        this.router = router;
        this.appController = appController;
        this.errorController = errorController;
        this.feedController = feedController;
        this.adminController = adminController;
        this.demosTutorialsControllerController = demosTutorialsControllerController;
        this.spincastFilters = spincastFilters;
        this.websocketsDemoEchoAllController = websocketsDemoEchoAllController;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected IAppConfig getConfig() {
        return this.appConfig;
    }

    protected IAppRouter getRouter() {
        return this.router;
    }

    protected MainPagesController getAppController() {
        return this.appController;
    }

    protected ErrorController getErrorController() {
        return this.errorController;
    }

    protected FeedController getFeedController() {
        return this.feedController;
    }

    protected AdminController getAdminController() {
        return this.adminController;
    }

    protected DemosTutorialsController getDemosTutorialsController() {
        return this.demosTutorialsControllerController;
    }

    protected ISpincastFilters<IAppRequestContext> getSpincastFilters() {
        return this.spincastFilters;
    }

    protected WebsocketsDemoEchoAllController getWebsocketsDemoEchoAllController() {
        return this.websocketsDemoEchoAllController;
    }

    /**
     * Starts the application!
     */
    public void start() {
        configureLogback();
        addRoutes();
        getServer().start();
        displayStartedMessage();
    }

    /**
     * Configure Logback programatically. This is
     * the easiest way to have different logging
     * behavior depending on the environment and
     * using injected config!
     */
    protected void configureLogback() {

        try {

            String logbackFilePath = "conf/logback.prod.xml";
            if(getConfig().isDebugEnabled()) {
                logbackFilePath = "conf/logback.debug.xml";
            }

            InputStream logbackFileIn = this.getClass().getClassLoader().getResourceAsStream(logbackFilePath);
            if(logbackFileIn == null) {
                throw new RuntimeException("Logback file not found on the classpath : " + logbackFilePath);
            }

            LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackFileIn);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void displayStartedMessage() {

        System.out.println();
        System.out.println("====================================================");

        if(getConfig().getHttpServerPort() > 0) {
            System.out.println("Spincast HTTP website started on port " + getConfig().getHttpServerPort());
        }
        if(getConfig().getHttpsServerPort() > 0) {
            System.out.println("Spincast HTTPS website started on port " + getConfig().getHttpsServerPort());
        }

        System.out.println("Environment : " + getConfig().getEnvironmentName());
        System.out.println("Debug enabled? " + getConfig().isDebugEnabled());
        System.out.println("====================================================");
        System.out.println();
    }

    /**
     * The application's routes
     */
    protected void addRoutes() {

        IAppRouter router = getRouter();

        MainPagesController appCtl = getAppController();
        DemosTutorialsController demoCtl = getDemosTutorialsController();

        //==========================================
        // Public resources
        //==========================================
        router.dir("/public").classpath("/public").save();
        router.file("/favicon.ico").classpath("/public/favicon.ico").save();
        router.file("/robots.txt").classpath("/public/robots.txt").save();
        router.file("/humans.txt").classpath("/public/humans.txt").save();
        router.file("/browserconfig.xml").classpath("/public/browserconfig.xml").save();
        router.file("/apple-touch-icon.png").classpath("/public/apple-touch-icon.png").save();
        router.file("/tile-wide.png").classpath("/public/tile-wide.png").save();
        router.file("/tile.png").classpath("/public/tile.png").save();

        //==========================================
        // Add some security headers.
        //==========================================
        router.before(getSpincastFilters()::addSecurityHeaders);

        //==========================================
        // Not Found (404) handler
        //==========================================
        router.notFound(getErrorController()::notFoundHandler);

        //==========================================
        // Exception handler
        //==========================================
        router.exception(getErrorController()::exceptionHandler);

        //==========================================
        // News Feed, as a dynamic resouce
        //==========================================
        router.file("/rss").pathRelative("/feed/rss.xml").save(getFeedController()::rss);

        //==========================================
        // Admin - protected area
        //==========================================
        router.httpAuth("/admin", AppConstants.HTTP_AUTH_REALM_NAME_ADMIN);
        router.GET("/admin").noCache().save(getAdminController()::index);
        router.GET("/admin/news").save(getAdminController()::news);

        //==========================================
        // Static Pages
        //==========================================
        router.file("/").pathRelative("/pages/index.html").save(appCtl::index);
        router.file("/presentation").pathRelative("/pages/presentation.html").save(appCtl::presentation);
        router.file("/news").pathRelative("/pages/news.html").save(appCtl::news);
        router.file("/news").pathRelative("/pages/news.html").save(appCtl::news);
        router.file("/news/${newsId:<N>}").pathRelative("/pages/news/${newsId:<N>}.html").save(appCtl::newsEntry);
        router.file("/documentation").pathRelative("/pages/documentation.html").save(appCtl::documentation);
        router.file("/download").pathRelative("/pages/download.html").save(appCtl::download);
        router.file("/plugins").pathRelative("/pages/plugins.html").save(appCtl::plugins);
        router.file("/plugins/${pluginName}").pathRelative("/pages/plugins/${pluginName}.html").save(appCtl::plugin);
        router.file("/community").pathRelative("/pages/community.html").save(appCtl::community);
        router.file("/about").pathRelative("/pages/about.html").save(appCtl::about);
        router.file("/more").pathRelative("/pages/more.html").save(appCtl::about);

        //==========================================
        // Demos/Tutorials
        //==========================================
        router.redirect("/demos-tutorials").temporarily().to("/demos-tutorials/hello-world");

        router.file("/demos-tutorials/hello-world")
              .pathRelative("/pages/demos-tutorials/hello-world.html")
              .save(demoCtl::helloWorld);

        router.file("/demos-tutorials/full-website")
              .pathRelative("/pages/demos-tutorials/full-website.html")
              .save(demoCtl::fullWebsite);

        router.file("/demos-tutorials/todo-list")
              .pathRelative("/pages/demos-tutorials/todo-list.html")
              .save(demoCtl::todoList);

        router.file("/demos-tutorials/http-authentication")
              .pathRelative("/pages/demos-tutorials/http-authentication.html")
              .save(demoCtl::httpAuthentication);
        router.httpAuth("/demos-tutorials/http-authentication/protected", AppConstants.HTTP_AUTH_REALM_NAME_EXAMPLE);
        router.redirect("/protected_example").to("/demos-tutorials/http-authentication/protected");
        router.file("/demos-tutorials/http-authentication/protected")
              .pathRelative("/pages/demos-tutorials/http-authentication/protected.html")
              .save(demoCtl::httpAuthenticationProtectedPage);

        router.file("/demos-tutorials/websockets")
              .pathRelative("/pages/demos-tutorials/websockets.html")
              .save(demoCtl::webSockets);
        router.websocket("/demos-tutorials/websockets/echo-all-endpoint").save(getWebsocketsDemoEchoAllController());
        router.redirect("/showcase/websockets/echo-all").to("/demos-tutorials/websockets");
        router.redirect("/showcase/websockets/echo-all-endpoint").to("/demos-tutorials/websockets/echo-all-endpoint");
    }
}

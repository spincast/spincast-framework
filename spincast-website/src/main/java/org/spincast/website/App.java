package org.spincast.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.dateformatter.SpincastDateFormatterPlugin;
import org.spincast.plugins.logbackutils.SpincastLogbackUtilsPlugin;
import org.spincast.website.controllers.AdminController;
import org.spincast.website.controllers.ErrorController;
import org.spincast.website.controllers.FeedController;
import org.spincast.website.controllers.MainPagesController;
import org.spincast.website.controllers.WebsocketsDemoEchoAllController;
import org.spincast.website.controllers.demos.DemoFormAuthController;
import org.spincast.website.controllers.demos.DemoHtmlFormsDynamicFieldsController;
import org.spincast.website.controllers.demos.DemoHtmlFormsFileUploadController;
import org.spincast.website.controllers.demos.DemoHtmlFormsMultipleFieldsController;
import org.spincast.website.controllers.demos.DemoHtmlFormsSingleFieldController;
import org.spincast.website.controllers.demos.DemosTutorialsController;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.exchange.AppRequestContextDefault;
import org.spincast.website.exchange.AppRouter;
import org.spincast.website.guice.AppModule;

import com.google.inject.Inject;

public class App {

    protected static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * The entry point for the application.
     */
    public static void main(String[] args) {

        Spincast.configure()
                .module(new AppModule())
                .plugin(new SpincastDateFormatterPlugin())
                .plugin(new SpincastLogbackUtilsPlugin())
                .requestContextImplementationClass(AppRequestContextDefault.class)
                .init(args);
    }

    //==========================================
    // The application
    //==========================================

    private final Server server;
    private final AppConfig appConfig;
    private final AppRouter router;
    private final MainPagesController appController;
    private final ErrorController errorController;
    private final FeedController feedController;
    private final AdminController adminController;
    private final DemosTutorialsController demosTutorialsControllerController;
    private final DemoFormAuthController demoFormAuthController;
    private final DemoHtmlFormsSingleFieldController demoHtmlFormsSingleFieldController;
    private final DemoHtmlFormsMultipleFieldsController demoHtmlFormsMultipleFieldsController;
    private final DemoHtmlFormsDynamicFieldsController demoHtmlFormsDynamicFieldsController;
    private final DemoHtmlFormsFileUploadController demoHtmlFormsFileUploadController;

    private final SpincastFilters<AppRequestContext> spincastFilters;
    private final WebsocketsDemoEchoAllController websocketsDemoEchoAllController;

    @Inject
    public App(Server server,
               AppConfig config,
               AppRouter router,
               MainPagesController appController,
               ErrorController errorController,
               FeedController feedController,
               AdminController adminController,
               DemosTutorialsController demosTutorialsControllerController,
               DemoFormAuthController demoFormAuthController,
               DemoHtmlFormsSingleFieldController demoHtmlFormsSingleFieldController,
               DemoHtmlFormsMultipleFieldsController demoHtmlFormsMultipleFieldsController,
               DemoHtmlFormsDynamicFieldsController demoHtmlFormsDynamicFieldsController,
               SpincastFilters<AppRequestContext> spincastFilters,
               WebsocketsDemoEchoAllController websocketsDemoEchoAllController,
               DemoHtmlFormsFileUploadController demoHtmlFormsFileUploadController) {
        this.server = server;
        this.appConfig = config;
        this.router = router;
        this.appController = appController;
        this.errorController = errorController;
        this.feedController = feedController;
        this.adminController = adminController;
        this.demosTutorialsControllerController = demosTutorialsControllerController;
        this.demoFormAuthController = demoFormAuthController;
        this.demoHtmlFormsSingleFieldController = demoHtmlFormsSingleFieldController;
        this.demoHtmlFormsMultipleFieldsController = demoHtmlFormsMultipleFieldsController;
        this.demoHtmlFormsDynamicFieldsController = demoHtmlFormsDynamicFieldsController;
        this.spincastFilters = spincastFilters;
        this.websocketsDemoEchoAllController = websocketsDemoEchoAllController;
        this.demoHtmlFormsFileUploadController = demoHtmlFormsFileUploadController;
    }

    protected Server getServer() {
        return this.server;
    }

    protected AppConfig getConfig() {
        return this.appConfig;
    }

    protected AppRouter getRouter() {
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

    protected DemoFormAuthController getDemoFormAuthController() {
        return this.demoFormAuthController;
    }

    protected DemoHtmlFormsSingleFieldController getDemoHtmlFormsSingleFieldController() {
        return this.demoHtmlFormsSingleFieldController;
    }

    protected DemoHtmlFormsMultipleFieldsController getDemoHtmlFormsMultipleFieldsController() {
        return this.demoHtmlFormsMultipleFieldsController;
    }

    protected DemoHtmlFormsDynamicFieldsController getDemoHtmlFormsDynamicFieldsController() {
        return this.demoHtmlFormsDynamicFieldsController;
    }

    protected SpincastFilters<AppRequestContext> getSpincastFilters() {
        return this.spincastFilters;
    }

    protected WebsocketsDemoEchoAllController getWebsocketsDemoEchoAllController() {
        return this.websocketsDemoEchoAllController;
    }

    protected DemoHtmlFormsFileUploadController getDemoHtmlFormsFileUploadController() {
        return this.demoHtmlFormsFileUploadController;
    }

    /**
     * Starts the application!
     */
    @Inject
    public void start() {
        addRoutes();
        getServer().start();
        displayStartedMessage();
    }

    protected void displayStartedMessage() {

        System.out.println();
        System.out.println("====================================================");

        if (getConfig().getHttpServerPort() > 0) {
            System.out.println("Spincast HTTP website started on port " + getConfig().getHttpServerPort());
        }
        if (getConfig().getHttpsServerPort() > 0) {
            System.out.println("Spincast HTTPS website started on port " + getConfig().getHttpsServerPort());
        }

        System.out.println("Environment : " + getConfig().getEnvironmentName());
        System.out.println("Debug enabled? " + getConfig().isDevelopmentMode());
        System.out.println("====================================================");
        System.out.println();
    }

    /**
     * The application's routes
     */
    protected void addRoutes() {

        AppRouter router = getRouter();

        MainPagesController appCtl = getAppController();
        DemosTutorialsController demoCtl = getDemosTutorialsController();

        //==========================================
        // Public resources
        //==========================================
        router.dir("/public").classpath("/public").handle();
        router.file("/favicon.ico").classpath("/public/favicon.ico").handle();
        router.file("/robots.txt").classpath("/public/robots.txt").handle();
        router.file("/humans.txt").classpath("/public/humans.txt").handle();
        router.file("/browserconfig.xml").classpath("/public/browserconfig.xml").handle();
        router.file("/apple-touch-icon.png").classpath("/public/apple-touch-icon.png").handle();
        router.file("/tile-wide.png").classpath("/public/tile-wide.png").handle();
        router.file("/tile.png").classpath("/public/tile.png").handle();

        //==========================================
        // Add some security headers.
        //==========================================
        router.ALL().pos(-10).handle(getSpincastFilters()::addSecurityHeaders);

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
        router.file("/rss").pathRelative("/feed/rss.xml").handle(getFeedController()::rss);

        //==========================================
        // Admin - protected area
        //==========================================
        router.httpAuth("/admin", AppConstants.HTTP_AUTH_REALM_NAME_ADMIN);
        router.GET("/admin").noCache().handle(getAdminController()::index);
        router.GET("/admin/news").handle(getAdminController()::news);

        //==========================================
        // Static Pages
        //==========================================
        router.file("/").pathRelative("/pages/index.html").handle(appCtl::index);

        // Can't be a static resource since it accepts a "page"
        // querystring parameter which changes the content.
        router.GET("/news").handle(appCtl::news);

        // For now, the relative date is server-server generated as
        // an example,fot can't be static.
        //router.file("/news/${newsId:<N>}").pathRelative("/pages/news/${newsId:<N>}.html").handle(appCtl::newsEntry);
        router.GET("/news/${newsId:<N>}").handle(appCtl::newsEntry);

        router.file("/documentation").pathRelative("/pages/documentation.html").handle(appCtl::documentation);
        router.file("/download").pathRelative("/pages/download.html").handle(appCtl::download);
        router.file("/plugins").pathRelative("/pages/plugins.html").handle(appCtl::plugins);
        router.file("/plugins/${pluginName}").pathRelative("/pages/plugins/${pluginName}.html").handle(appCtl::plugin);
        router.file("/community").pathRelative("/pages/community.html").handle(appCtl::community);
        router.file("/about").pathRelative("/pages/about.html").handle(appCtl::about);
        router.file("/more").pathRelative("/pages/more.html").handle(appCtl::about);

        //==========================================
        // Demos/Tutorials
        //==========================================
        router.redirect("/demos-tutorials").temporarily().to("/demos-tutorials/hello-world");

        router.redirect("/demos-tutorials/hello-world").to("/demos-tutorials/hello-world/quick");
        router.GET("/demos-tutorials/hello-world/quick").handle(demoCtl::helloWorldQuick);
        router.GET("/demos-tutorials/hello-world/better").handle(demoCtl::helloWorldBetter);
        router.GET("/demos-tutorials/hello-world/super").handle(demoCtl::helloWorldSuper);

        router.file("/demos-tutorials/real-apps")
              .pathRelative("/pages/demos-tutorials/real-apps.html")
              .handle(demoCtl::realApps);

        router.file("/demos-tutorials/http-authentication")
              .pathRelative("/pages/demos-tutorials/http-authentication.html")
              .handle(demoCtl::httpAuthentication);
        router.httpAuth("/demos-tutorials/http-authentication/protected", AppConstants.HTTP_AUTH_REALM_NAME_EXAMPLE);
        router.redirect("/protected_example").to("/demos-tutorials/http-authentication/protected");
        router.file("/demos-tutorials/http-authentication/protected")
              .pathRelative("/pages/demos-tutorials/http-authentication/protected.html")
              .handle(demoCtl::httpAuthenticationProtectedPage);

        router.file("/demos-tutorials/websockets")
              .pathRelative("/pages/demos-tutorials/websockets.html")
              .handle(demoCtl::webSockets);
        router.websocket("/demos-tutorials/websockets/echo-all-endpoint").handle(getWebsocketsDemoEchoAllController());
        router.redirect("/showcase/websockets/echo-all").to("/demos-tutorials/websockets");
        router.redirect("/showcase/websockets/echo-all-endpoint").to("/demos-tutorials/websockets/echo-all-endpoint");

        //router.GET("/demos-tutorials/form-authentication").handle(getDemoFormAuthController()::index);
        //router.POST("/demos-tutorials/form-authentication/login").handle(getDemoFormAuthController()::login);
        //router.POST("/demos-tutorials/form-authentication/register").handle(getDemoFormAuthController()::login);

        router.redirect("/demos-tutorials/html-forms").to("/demos-tutorials/html-forms/single-field");

        router.GET("/demos-tutorials/html-forms/single-field")
              .handle(getDemoHtmlFormsSingleFieldController()::singleField);
        router.POST("/demos-tutorials/html-forms/single-field")
              .handle(getDemoHtmlFormsSingleFieldController()::singleFieldSubmit);

        router.GET("/demos-tutorials/html-forms/multiple-fields")
              .handle(getDemoHtmlFormsMultipleFieldsController()::multipleFields);
        router.POST("/demos-tutorials/html-forms/multiple-fields")
              .handle(getDemoHtmlFormsMultipleFieldsController()::multipleFieldsSubmit);

        router.GET("/demos-tutorials/html-forms/dynamic-fields")
              .handle(getDemoHtmlFormsDynamicFieldsController()::dynamicFields);
        router.POST("/demos-tutorials/html-forms/dynamic-fields")
              .handle(getDemoHtmlFormsDynamicFieldsController()::dynamicFieldsSubmit);

        router.GET("/demos-tutorials/html-forms/file-upload")
              .handle(getDemoHtmlFormsFileUploadController()::fileUpload);
        router.POST("/demos-tutorials/html-forms/file-upload")
              .handle(getDemoHtmlFormsFileUploadController()::fileUploadSubmit);

    }
}

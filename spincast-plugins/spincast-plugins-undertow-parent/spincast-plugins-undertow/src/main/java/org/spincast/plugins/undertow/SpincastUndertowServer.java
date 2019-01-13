package org.spincast.plugins.undertow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookieSameSite;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.server.Server;
import org.spincast.core.server.ServerUtils;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.server.UploadedFileDefault;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.ssl.SSLContextFactory;
import org.spincast.core.websocket.WebsocketEndpointHandler;
import org.spincast.core.websocket.WebsocketEndpointManager;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.commonjava.mimeparse.MIMEParse;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

/**
 * Server implementation for Undertow.
 */
public class SpincastUndertowServer implements Server {

    protected final Logger logger = LoggerFactory.getLogger(SpincastUndertowServer.class);

    public static final String UNDERTOW_EXCEPTION_CODE_REQUEST_TOO_LARGE = "UT000020";

    private final WebsocketEndpointFactory spincastWebsocketEndpointFactory;
    private final SpincastUtils spincastUtils;
    private final SpincastConfig config;
    private final SpincastUndertowConfig spincastUndertowConfig;
    private final FrontController frontController;
    private final CookieFactory cookieFactory;
    private final CorsHandlerFactory corsHandlerFactory;
    private final GzipCheckerHandlerFactory gzipCheckerHandlerFactory;
    private final SkipResourceOnQueryStringHandlerFactory skipResourceOnQueryStringHandlerFactory;
    private final SpincastResourceHandlerFactory spincastResourceHandlerFactory;
    private final CacheBusterRemovalHandlerFactory cacheBusterRemovalHandlerFactory;
    private final FileClassPathResourceManagerFactory fileClassPathResourceManagerFactory;
    private final SpincastHttpAuthIdentityManagerFactory spincastHttpAuthIdentityManagerFactory;
    private final SSLContextFactory sslContextFactory;
    private final ServerUtils serverUtils;

    private Undertow undertowServer;
    private IoCallback doNothingCallback = null;
    private IoCallback closeExchangeCallback = null;

    private final Map<String, StaticResource<?>> staticResourcesServedByUrlPath = new HashMap<String, StaticResource<?>>();

    private final Map<String, String> httpAuthActiveRealms = new HashMap<String, String>();

    private final Map<String, SpincastHttpAuthIdentityManager> httpAuthIdentityManagersByRealmName =
            new HashMap<String, SpincastHttpAuthIdentityManager>();

    //==========================================
    // Websocket endpoints, with there id as the key.
    //==========================================
    private final Map<String, WebsocketEndpoint> websocketEndpointsMap =
            new ConcurrentHashMap<String, WebsocketEndpoint>();

    private HttpHandler spincastFrontControllerHandler;
    private PathHandler staticResourcesPathHandler;
    private PathHandler httpAuthenticationHandler;
    private CacheBusterRemovalHandler cacheBusterRemovalHandler;

    private FormParserFactory formParserFactory;

    private final Map<String, Object> websocketEndpointCreationLocks = new ConcurrentHashMap<String, Object>();
    private final Object websocketEndpointLockCreationLock = new Object();

    /**
     * Constructor
     */
    @Inject
    public SpincastUndertowServer(SpincastConfig config,
                                  SpincastUndertowConfig spincastUndertowConfig,
                                  FrontController frontController,
                                  SpincastUtils spincastUtils,
                                  CookieFactory cookieFactory,
                                  CorsHandlerFactory corsHandlerFactory,
                                  GzipCheckerHandlerFactory gzipCheckerHandlerFactory,
                                  SkipResourceOnQueryStringHandlerFactory skipResourceOnQueryStringHandlerFactory,
                                  SpincastResourceHandlerFactory spincastResourceHandlerFactory,
                                  CacheBusterRemovalHandlerFactory cacheBusterRemovalHandlerFactory,
                                  FileClassPathResourceManagerFactory fileClassPathResourceManagerFactory,
                                  SpincastHttpAuthIdentityManagerFactory spincastHttpAuthIdentityManagerFactory,
                                  WebsocketEndpointFactory spincastWebsocketEndpointFactory,
                                  SSLContextFactory sslContextFactory,
                                  ServerUtils serverUtils) {
        this.config = config;
        this.spincastUndertowConfig = spincastUndertowConfig;
        this.frontController = frontController;
        this.spincastUtils = spincastUtils;
        this.cookieFactory = cookieFactory;
        this.corsHandlerFactory = corsHandlerFactory;
        this.gzipCheckerHandlerFactory = gzipCheckerHandlerFactory;
        this.skipResourceOnQueryStringHandlerFactory = skipResourceOnQueryStringHandlerFactory;
        this.spincastResourceHandlerFactory = spincastResourceHandlerFactory;
        this.cacheBusterRemovalHandlerFactory = cacheBusterRemovalHandlerFactory;
        this.fileClassPathResourceManagerFactory = fileClassPathResourceManagerFactory;
        this.spincastHttpAuthIdentityManagerFactory = spincastHttpAuthIdentityManagerFactory;
        this.spincastWebsocketEndpointFactory = spincastWebsocketEndpointFactory;
        this.sslContextFactory = sslContextFactory;
        this.serverUtils = serverUtils;
    }

    protected SpincastConfig getConfig() {
        return this.config;
    }

    protected SpincastUndertowConfig getSpincastUndertowConfig() {
        return this.spincastUndertowConfig;
    }

    protected FrontController getFrontController() {
        return this.frontController;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected CorsHandlerFactory getCorsHandlerFactory() {
        return this.corsHandlerFactory;
    }

    protected GzipCheckerHandlerFactory getGzipCheckerHandlerFactory() {
        return this.gzipCheckerHandlerFactory;
    }

    protected SkipResourceOnQueryStringHandlerFactory getSkipResourceOnQueryStringHandlerFactory() {
        return this.skipResourceOnQueryStringHandlerFactory;
    }

    protected SpincastResourceHandlerFactory getSpincastResourceHandlerFactory() {
        return this.spincastResourceHandlerFactory;
    }

    protected CacheBusterRemovalHandlerFactory getCacheBusterRemovalHandlerFactory() {
        return this.cacheBusterRemovalHandlerFactory;
    }

    protected FileClassPathResourceManagerFactory getFileClassPathResourceManagerFactory() {
        return this.fileClassPathResourceManagerFactory;
    }

    protected SpincastHttpAuthIdentityManagerFactory getSpincastHttpAuthIdentityManagerFactory() {
        return this.spincastHttpAuthIdentityManagerFactory;
    }

    protected WebsocketEndpointFactory getSpincastWebsocketEndpointFactory() {
        return this.spincastWebsocketEndpointFactory;
    }

    protected Map<String, StaticResource<?>> getStaticResourcesServedByUrlPath() {
        return this.staticResourcesServedByUrlPath;
    }

    protected Map<String, SpincastHttpAuthIdentityManager> getHttpAuthIdentityManagersByRealmName() {
        return this.httpAuthIdentityManagersByRealmName;
    }

    protected Map<String, WebsocketEndpoint> getWebsocketEndpointsMap() {
        return this.websocketEndpointsMap;
    }

    protected Map<String, String> getHttpAuthActiveRealms() {
        return this.httpAuthActiveRealms;
    }

    protected SSLContextFactory getSslContextFactory() {
        return this.sslContextFactory;
    }

    protected ServerUtils getServerUtils() {
        return this.serverUtils;
    }

    @Override
    public Map<String, String> getHttpAuthenticationRealms() {
        return Collections.unmodifiableMap(getHttpAuthActiveRealms());
    }

    protected FormParserFactory getFormParserFactory() {
        if (this.formParserFactory == null) {

            io.undertow.server.handlers.form.FormParserFactory.Builder builder = FormParserFactory.builder(false);

            builder.addParsers(new FormEncodedDataDefinition(),
                               new MultiPartParserDefinition(createUndertowTempDir()));

            this.formParserFactory = builder.build();
        }
        return this.formParserFactory;
    }

    protected Path createUndertowTempDir() {
        File undertowWritableDir = new File(getConfig().getTempDir().getAbsolutePath() + "/undertow");
        if (!undertowWritableDir.isDirectory()) {
            boolean result = undertowWritableDir.mkdirs();
            if (!result) {
                throw new RuntimeException("Unable to create the Undertow temp dir : " +
                                           undertowWritableDir.getAbsolutePath());
            }
        }

        return undertowWritableDir.toPath();
    }

    @Override
    public boolean isRunning() {
        return this.undertowServer != null;
    }

    @Override
    public synchronized void start() {

        if (this.undertowServer != null) {
            this.logger.warn("Server already started.");
            return;
        }

        this.undertowServer = getServerBuilder().build();

        int serverStartTryNbr = getServerStartTryNbr();
        for (int i = 0; i < serverStartTryNbr; i++) {

            try {
                this.undertowServer.start();
                break;
            } catch (Exception ex) {

                if ((ex instanceof BindException) || (ex.getCause() != null && ex.getCause() instanceof BindException)) {
                    this.logger.warn("BindException while trying to start the server. Try " + i + " of " + serverStartTryNbr +
                                     "...");

                    if (i < serverStartTryNbr) {
                        try {
                            Thread.sleep(getStartServerSleepMilliseconds());
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                }
                this.undertowServer = null;
                throw ex;
            }
        }

        if (getConfig().getHttpServerPort() > 0) {
            this.logger.info("HTTP server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpServerPort());
        }
        if (getConfig().getHttpsServerPort() > 0) {
            this.logger.info("HTTPS server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpsServerPort());
        }
    }

    protected int getServerStartTryNbr() {
        return 10;
    }

    protected long getStartServerSleepMilliseconds() {
        return 500;
    }

    protected Builder getServerBuilder() {

        try {
            String serverHost = getConfig().getServerHost();
            int httpServerPort = getConfig().getHttpServerPort();
            int httpsServerPort = getConfig().getHttpsServerPort();

            if (httpServerPort <= 0 && httpsServerPort <= 0) {
                throw new RuntimeException("At least one of the HTTP or HTTPS port must " +
                                           "be greater than 0 to start the server....");
            }

            Builder builder = Undertow.builder().setHandler(getFinalHandler());

            if (httpServerPort > 0) {
                addHttpListener(builder, serverHost, httpServerPort);
            }
            if (httpsServerPort > 0) {
                addHttpsListener(builder, serverHost, httpsServerPort);
            }

            builder = addBuilderOptions(builder);

            return builder;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void addHttpListener(Builder builder, String serverHost, int httpServerPort) {
        builder = builder.addHttpListener(httpServerPort, serverHost);
    }

    protected void addHttpsListener(Builder builder, String serverHost, int httpsServerPort) {

        try {

            SSLContext sslContext = getSslContextFactory().createSSLContext(getConfig().getHttpsKeyStorePath(),
                                                                            getConfig().getHttpsKeyStoreType(),
                                                                            getConfig().getHttpsKeyStoreStorePass(),
                                                                            getConfig().getHttpsKeyStoreKeyPass());

            builder = builder.addHttpsListener(httpsServerPort, serverHost, sslContext);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected Builder addBuilderOptions(Builder builder) {
        builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, getConfig().getServerMaxRequestBodyBytes());
        return builder;
    }

    /**
     * The very first handler considered by Undertow.
     */
    protected HttpHandler getFinalHandler() {
        return getCacheBusterRemovalHandler();
    }

    /**
     * Handler to remove cache busters from the request's URL.
     */
    protected CacheBusterRemovalHandler getCacheBusterRemovalHandler() {
        if (this.cacheBusterRemovalHandler == null) {
            this.cacheBusterRemovalHandler = getCacheBusterRemovalHandlerFactory().create(getHttpAuthenticationHandler());
        }
        return this.cacheBusterRemovalHandler;
    }

    /**
     * Handler to check for HTTP authentication requirement.
     */
    protected PathHandler getHttpAuthenticationHandler() {
        if (this.httpAuthenticationHandler == null) {
            this.httpAuthenticationHandler = new FullPathMatchingPathHandler(getHttpAuthHandlerNextHandler());
        }
        return this.httpAuthenticationHandler;
    }

    protected HttpHandler getHttpAuthHandlerNextHandler() {
        return getStaticResourcesPathHandler();
    }

    @Override
    public void createHttpAuthenticationRealm(String pathPrefix, String realmName) {

        if (getHttpAuthActiveRealms().containsKey(realmName)) {
            throw new RuntimeException("A HTTP authentication realm named '" + realmName + "' " +
                                       "already exists for path: " + pathPrefix);
        }

        SpincastHttpAuthIdentityManager identityManager = getOrCreateHttpAuthIdentityManagersByRealmName(realmName);

        HttpHandler handler = new AuthenticationCallHandler(getHttpAuthHandlerNextHandler());
        handler = new AuthenticationConstraintHandler(handler);
        final List<AuthenticationMechanism> mechanisms =
                Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism(getRealmNameToDisplay(pathPrefix,
                                                                                                                          realmName)));
        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE,
                                             identityManager,
                                             handler);

        getHttpAuthIdentityManagersByRealmName().put(realmName, identityManager);

        getHttpAuthenticationHandler().addPrefixPath(pathPrefix, handler);
        getHttpAuthActiveRealms().put(realmName, pathPrefix);

    }

    /**
     * The realm name to display.
     */
    protected String getRealmNameToDisplay(String pathPrefix, String realmName) {
        return realmName;
    }

    protected SpincastHttpAuthIdentityManager getOrCreateHttpAuthIdentityManagersByRealmName(String realmName) {
        SpincastHttpAuthIdentityManager identityManager = getHttpAuthIdentityManagersByRealmName().get(realmName);
        if (identityManager == null) {
            identityManager = getSpincastHttpAuthIdentityManagerFactory().create();
            getHttpAuthIdentityManagersByRealmName().put(realmName, identityManager);
        }
        return identityManager;
    }

    @Override
    public void addHttpAuthentication(String realmName,
                                      String username,
                                      String password) {

        //==========================================
        // Adds the new username/password to the
        // identity manager of this realm.
        //==========================================
        SpincastHttpAuthIdentityManager identityManager = getOrCreateHttpAuthIdentityManagersByRealmName(realmName);
        identityManager.addUser(username, password);
    }

    @Override
    public void removeHttpAuthentication(String username, String realmName) {
        SpincastHttpAuthIdentityManager identityManager = getHttpAuthIdentityManagersByRealmName().get(realmName);
        if (identityManager != null) {
            identityManager.removeUser(username);
        }
    }

    @Override
    public void removeHttpAuthentication(String username) {
        for (SpincastHttpAuthIdentityManager identityManager : getHttpAuthIdentityManagersByRealmName().values()) {
            identityManager.removeUser(username);
        }
    }

    protected HttpHandler getSpincastFrontControllerHandler() {
        if (this.spincastFrontControllerHandler == null) {
            this.spincastFrontControllerHandler = new HttpHandler() {

                /**
                 * If we enter here, it means the request is not for a static resource
                 * so we target Spincast's front controller.
                 */
                @Override
                public void handleRequest(HttpServerExchange exchange) throws Exception {

                    // @see http://undertow.io/undertow-docs/undertow-docs-1.3.0/index.html#dispatch-code
                    if (exchange.isInIoThread()) {
                        exchange.dispatch(this);
                        return;
                    }

                    getFrontController().handle(exchange);
                }
            };
        }
        return this.spincastFrontControllerHandler;
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public synchronized void stop(boolean sendClosingMessageToPeers) {
        if (this.undertowServer != null) {

            //==========================================
            // If there are some active Websocket endpoints,
            // we try to send a "close" event to the peers before
            // actually closing the server.
            //==========================================
            try {
                if (sendClosingMessageToPeers) {
                    sendWebsocketEnpointsClosedWhenServerStops();
                }
            } finally {
                try {
                    this.undertowServer.stop();
                    this.undertowServer = null;
                } catch (Exception ex) {
                    this.logger.error("Error stopping the Undertow server :\n" + SpincastStatics.getStackTrace(ex));
                }
            }
        }
    }

    protected int getSecondsToWaitForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer() {
        return getSpincastUndertowConfig().getSecondsToWaitForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer();
    }

    protected int getMilliSecondsIncrementWhenWaitingForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer() {
        return getSpincastUndertowConfig().getMilliSecondsIncrementWhenWaitingForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer();
    }

    protected void sendWebsocketEnpointsClosedWhenServerStops() {

        Collection<WebsocketEndpoint> websocketEndpointsMap = getWebsocketEndpointsMap().values();
        List<WebsocketEndpoint> websocketEndpoints = new ArrayList<WebsocketEndpoint>(websocketEndpointsMap);

        Set<String> unclosedEndpoints = new HashSet<String>(getWebsocketEndpointsMap().keySet());

        this.logger.debug("We wait for those endpoints to be finished closing : " +
                          Arrays.toString(unclosedEndpoints.toArray()));

        for (WebsocketEndpoint websocketEndpoint : websocketEndpoints) {
            try {
                websocketEndpoint.closeEndpoint(true);
            } catch (Exception ex) {
                this.logger.warn("Error closing Websocket '" + websocketEndpoint.getEndpointId() + "': " +
                                 ex.getMessage());
            }
        }

        //==========================================
        // Wait for endpoints to be closed...
        //==========================================
        int millisecondsWaited = 0;
        int millisecondsToWait =
                1000 * getSecondsToWaitForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer();
        int incrementsMilliseconds =
                getMilliSecondsIncrementWhenWaitingForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer();

        outer : while (millisecondsWaited < millisecondsToWait) {

            try {
                for (WebsocketEndpoint websocketEndpoint : websocketEndpoints) {

                    try {
                        if (unclosedEndpoints.contains(websocketEndpoint.getEndpointId()) &&
                            websocketEndpoint.isClosed()) {
                            this.logger.debug("Endpoint '" + websocketEndpoint.getEndpointId() +
                                              "' finished closing!");
                            unclosedEndpoints.remove(websocketEndpoint.getEndpointId());
                            if (unclosedEndpoints.size() == 0) {
                                this.logger.debug("All endpoints finished closing!");
                                break outer;
                            }
                        }
                    } catch (Exception ex) {
                        this.logger.warn("Error closing Websocket '" + websocketEndpoint.getEndpointId() + "': " +
                                         ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                this.logger.warn("Error while checking if all endpoints are closed : " + ex.getMessage());
            }

            this.logger.debug("Some endpoints are not finished closing. Remaining : " +
                              Arrays.toString(unclosedEndpoints.toArray()));

            try {
                Thread.sleep(incrementsMilliseconds);
            } catch (InterruptedException e) {
            }
            millisecondsWaited += incrementsMilliseconds;

            if (millisecondsWaited >= millisecondsToWait) {
                this.logger.debug("Some endpoints are still not finished closing, even after waiting for " +
                                  millisecondsWaited + " milliseconds. We'll stop the server as is. Remaining : " +
                                  Arrays.toString(unclosedEndpoints.toArray()));
            }
        }
    }

    protected PathHandler getStaticResourcesPathHandler() {
        if (this.staticResourcesPathHandler == null) {

            //==========================================
            // If no static resource path match, we
            // call the framework.
            //==========================================
            this.staticResourcesPathHandler = new FullPathMatchingPathHandler(getSpincastFrontControllerHandler());
        }
        return this.staticResourcesPathHandler;
    }

    @Override
    public void addStaticResourceToServe(final StaticResource<?> staticResource) {

        Map<String, StaticResource<?>> staticResourcesServedByUrlPath = getStaticResourcesServedByUrlPath();

        StaticResourceType staticResourceType = staticResource.getStaticResourceType();

        //==========================================
        // We remove the existing entry, if any.
        //==========================================
        if (staticResourcesServedByUrlPath.containsKey(staticResource.getUrlPath())) {
            getStaticResourcesPathHandler().removeExactPath(staticResource.getUrlPath());
            getStaticResourcesPathHandler().removePrefixPath(staticResource.getUrlPath());
        }
        staticResourcesServedByUrlPath.put(staticResource.getUrlPath(), staticResource);

        if (staticResourceType == StaticResourceType.FILE) {
            File file = new File(staticResource.getResourcePath());
            if (!file.isFile() && !staticResource.isCanBeGenerated()) {
                throw new RuntimeException("The file doesn't exist and can't be generated so it can't be served : " +
                                           staticResource.getResourcePath());
            }

            //==========================================
            // If the resource can be generated and is not found 
            // we call the framework instead of returning 404.
            //==========================================
            HttpHandler next = staticResource.isCanBeGenerated() ? getSpincastFrontControllerHandler()
                                                                 : ResponseCodeHandler.HANDLE_404;

            SpincastResourceHandler resourceHandler =
                    getSpincastResourceHandlerFactory().create(new FileResourceManager(file, 1024),
                                                               staticResource,
                                                               next);

            GzipCheckerHandler gzipCheckerHandler =
                    getGzipCheckerHandlerFactory().create(resourceHandler, file.getAbsolutePath());
            CorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            //==========================================
            // If the resource can be generated, it will
            // always be if a queryString is present (except
            // if "isIgnoreQueryString" is true).
            //==========================================
            HttpHandler firstHandler = corsHandler;
            if (staticResource.isCanBeGenerated() && !staticResource.isIgnoreQueryString()) {
                firstHandler = getSkipResourceOnQueryStringHandlerFactory().create(corsHandler, next);
            }

            getStaticResourcesPathHandler().addExactPath(staticResource.getUrlPath(), firstHandler);

        } else if (staticResourceType == StaticResourceType.FILE_FROM_CLASSPATH) {
            String classpathPath = staticResource.getResourcePath();
            if (classpathPath == null) {
                classpathPath = "";
            } else if (classpathPath.startsWith("/")) {
                classpathPath = classpathPath.substring(1);
            }

            URL resource = getClass().getClassLoader().getResource(classpathPath);
            if (resource == null) {
                throw new RuntimeException("The classpath file doesn't exist so it can't be served : " + classpathPath);
            }

            FileClassPathResourceManager fileClassPathResourceManager =
                    getFileClassPathResourceManagerFactory().create(classpathPath);

            SpincastResourceHandler resourceHandler =
                    getSpincastResourceHandlerFactory().create(fileClassPathResourceManager,
                                                               staticResource);

            GzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, classpathPath);
            CorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addExactPath(staticResource.getUrlPath(), corsHandler);

        } else if (staticResourceType == StaticResourceType.DIRECTORY) {
            File dir = new File(staticResource.getResourcePath());
            if (!dir.isDirectory() && !staticResource.isCanBeGenerated()) {
                throw new RuntimeException("The directory doesn't exist and can't be generated so it can't be served : " +
                                           staticResource.getResourcePath());
            }

            //==========================================
            // If the resource can be generated and is not found 
            // we call the framework instead of returning 404.
            //==========================================
            HttpHandler next = staticResource.isCanBeGenerated() ? getSpincastFrontControllerHandler()
                                                                 : ResponseCodeHandler.HANDLE_404;

            SpincastResourceHandler resourceHandler =
                    getSpincastResourceHandlerFactory().create(new FileResourceManager(dir, 1024),
                                                               staticResource,
                                                               next);

            GzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, null);
            CorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            //==========================================
            // If the resource can be generated, it will
            // always be if a queryString is present (except
            // if "isIgnoreQueryString" is true).
            //==========================================
            HttpHandler firstHandler = corsHandler;
            if (staticResource.isCanBeGenerated() && !staticResource.isIgnoreQueryString()) {
                firstHandler = getSkipResourceOnQueryStringHandlerFactory().create(corsHandler, next);
            }

            getStaticResourcesPathHandler().addPrefixPath(staticResource.getUrlPath(), firstHandler);

        } else if (staticResourceType == StaticResourceType.DIRECTORY_FROM_CLASSPATH) {

            String classpathPath = staticResource.getResourcePath();
            if (classpathPath == null) {
                classpathPath = "";
            } else if (classpathPath.startsWith("/")) {
                classpathPath = classpathPath.substring(1);
            }

            URL resource = getClass().getClassLoader().getResource(classpathPath);
            if (resource == null) {
                throw new RuntimeException("The classpath directory doesn't exist so it can't be used to serve static resources : " +
                                           classpathPath);
            }

            ClassPathResourceManager classPathResourceManager =
                    new ClassPathResourceManager(SpincastUndertowServer.class.getClassLoader(), classpathPath);

            SpincastResourceHandler resourceHandler =
                    getSpincastResourceHandlerFactory().create(classPathResourceManager,
                                                               staticResource);

            GzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, null);
            CorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addPrefixPath(staticResource.getUrlPath(), corsHandler);

        } else {
            throw new RuntimeException("Unamanaged static resource stype : " + staticResourceType);
        }
    }

    @Override
    public void removeStaticResourcesServed(StaticResourceType staticResourceType, String urlPath) {

        if (this.staticResourcesServedByUrlPath.containsKey(urlPath)) {
            removeStaticResource(staticResourceType, urlPath);
        }
    }

    @Override
    public void removeAllStaticResourcesServed() {

        for (Entry<String, StaticResource<?>> entry : getStaticResourcesServedByUrlPath().entrySet()) {

            String urlPath = entry.getKey();
            StaticResource<?> staticResource = entry.getValue();

            removeStaticResource(staticResource.getStaticResourceType(), urlPath);
        }
    }

    protected void removeStaticResource(StaticResourceType staticResourceType, String urlPath) {
        if (staticResourceType == StaticResourceType.FILE ||
            staticResourceType == StaticResourceType.FILE_FROM_CLASSPATH) {
            getStaticResourcesPathHandler().removeExactPath(urlPath);
        } else if (staticResourceType == StaticResourceType.DIRECTORY ||
                   staticResourceType == StaticResourceType.DIRECTORY_FROM_CLASSPATH) {
            getStaticResourcesPathHandler().removePrefixPath(urlPath);
        } else {
            throw new RuntimeException("Unamanaged static resource stype : " + staticResourceType);
        }
    }

    @Override
    public StaticResource<?> getStaticResourceServed(String urlPath) {
        return getStaticResourcesServedByUrlPath().get(urlPath);
    }

    @Override
    public Set<StaticResource<?>> getStaticResourcesServed() {
        return new HashSet<StaticResource<?>>(getStaticResourcesServedByUrlPath().values());
    }

    @Override
    public HttpMethod getHttpMethod(Object exchange) {
        HttpString httpString = ((HttpServerExchange)exchange).getRequestMethod();
        return HttpMethod.fromStringValue(httpString.toString());
    }

    protected HttpServerExchange castExchange(Object exchange) {
        return (HttpServerExchange)exchange;
    }

    @Override
    public ContentTypeDefaults getContentTypeBestMatch(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        ContentTypeDefaults type = ContentTypeDefaults.TEXT;
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders != null) {

            String requestedWith = requestHeaders.getFirst("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                return ContentTypeDefaults.JSON;
            }
            String accept = requestHeaders.getFirst("Accept");
            if (accept != null) {
                String bestMatch = MIMEParse.bestMatch(ContentTypeDefaults.getAllContentTypesVariations(), accept);
                if (!StringUtils.isBlank(bestMatch)) {
                    type = ContentTypeDefaults.fromString(bestMatch);
                    if (type == null) {
                        this.logger.error("Not supposed : " + bestMatch);
                        type = ContentTypeDefaults.TEXT;
                    }
                }
            }
        }
        return type;
    }

    @Override
    public String getFullUrlProxied(Object exchangeObj) {
        return getFullUrlProxied(exchangeObj, false);
    }

    @Override
    public String getFullUrlProxied(Object exchangeObj, boolean keepCacheBusters) {
        try {
            HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);
            String queryString = exchange.getQueryString();
            if (StringUtils.isBlank(queryString)) {
                queryString = "";
            } else {
                queryString = "?" + queryString;
            }

            //==========================================
            // Cache buster are removed by the CacheBusterRemovalHandler. 
            // To return the original URL, potentially containing cache
            // busters, we should call 
            // CacheBusterRemovalHandler#getOrigninalRequestUrlWithPotentialCacheBusters(...)
            //==========================================
            if (keepCacheBusters) {
                return getCacheBusterRemovalHandler().getOrigninalRequestUrlWithPotentialCacheBusters(exchange) + queryString;
            } else {
                return exchange.getRequestURL() + queryString;
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String getFullUrlOriginal(Object exchangeObj) {
        return getFullUrlOriginal(exchangeObj, false);
    }

    @Override
    public String getFullUrlOriginal(Object exchangeObj, boolean keepCacheBusters) {
        try {

            String fullUrl = getFullUrlProxied(exchangeObj, keepCacheBusters);

            HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

            //==========================================
            // If we are behind a reverse-proxy, the original
            // scheme/host/port can be different.
            //==========================================
            HeaderValues protoHeader = exchange.getRequestHeaders().get(HttpHeaders.X_FORWARDED_PROTO);
            HeaderValues hostHeader = exchange.getRequestHeaders().get(HttpHeadersExtra.X_FORWARDED_HOST);
            HeaderValues portHeader = exchange.getRequestHeaders().get(HttpHeadersExtra.X_FORWARDED_PORT);

            if (protoHeader != null || hostHeader != null || portHeader != null) {

                URL proxiedUrl = new URL(fullUrl);
                StringBuilder builder = new StringBuilder();

                if (protoHeader != null) {
                    builder.append(protoHeader.getFirst());
                } else {
                    builder.append(proxiedUrl.getProtocol());
                }
                builder.append("://");

                if (hostHeader != null) {
                    builder.append(hostHeader.getFirst());
                } else {
                    builder.append(proxiedUrl.getHost());
                }

                if (portHeader != null) {
                    builder.append(":").append(portHeader.getFirst());
                } else {
                    builder.append(proxiedUrl.getPort() > -1 ? ":" + proxiedUrl.getPort() : "");
                }

                builder.append(proxiedUrl.getPath());
                String queryString = proxiedUrl.getQuery();
                if (!StringUtils.isBlank(queryString)) {
                    builder.append("?").append(queryString);
                }
                fullUrl = builder.toString();
            }

            return fullUrl;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void setResponseHeader(Object exchangeObj, String name, List<String> values) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);
        HeaderMap responseHeaderMap = exchange.getResponseHeaders();
        responseHeaderMap.putAll(new HttpString(name), values);
    }

    @Override
    public void setResponseHeaders(Object exchange, Map<String, List<String>> headers) {

        HeaderMap responseHeaderMap = ((HttpServerExchange)exchange).getResponseHeaders();
        responseHeaderMap.clear();

        if (headers == null || headers.size() == 0) {
            return;
        }

        for (Entry<String, List<String>> entry : headers.entrySet()) {
            responseHeaderMap.putAll(new HttpString(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public Map<String, List<String>> getResponseHeaders(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<String>> headers = new HashMap<String, List<String>>();

        HeaderMap responseHeaders = exchange.getResponseHeaders();
        if (responseHeaders != null) {
            for (HeaderValues responseHeader : responseHeaders) {
                HttpString headerNameObj = responseHeader.getHeaderName();
                if (headerNameObj == null) {
                    continue;
                }

                List<String> values = new ArrayList<String>();
                for (String value : responseHeader) {
                    values.add(value);
                }
                headers.put(headerNameObj.toString(), values);
            }
        }

        return headers;
    }

    @Override
    public void removeResponseHeader(Object exchange, String name) {
        HeaderMap responseHeaderMap = ((HttpServerExchange)exchange).getResponseHeaders();
        responseHeaderMap.remove(new HttpString(name));
    }

    @Override
    public void setResponseStatusCode(Object exchange, int statusCode) {
        ((HttpServerExchange)exchange).setStatusCode(statusCode);
    }

    protected IoCallback getDoNothingCallback() {
        if (this.doNothingCallback == null) {
            this.doNothingCallback = new IoCallback() {

                @Override
                public void onComplete(final HttpServerExchange exchange, final Sender sender) {
                    // Do nothing
                    System.out.println();
                }

                @Override
                public void onException(final HttpServerExchange exchange, final Sender sender,
                                        final IOException exception) {
                    // Throw the exception
                    throw new RuntimeException(exception);
                }
            };
        }
        return this.doNothingCallback;
    }

    protected IoCallback getCloseExchangeCallback() {
        if (this.closeExchangeCallback == null) {
            this.closeExchangeCallback = new IoCallback() {

                @Override
                public void onComplete(final HttpServerExchange exchange, final Sender sender) {
                    // End the exchange
                    sender.close();
                    end(exchange);
                }

                @Override
                public void onException(final HttpServerExchange exchange, final Sender sender,
                                        final IOException exception) {
                    // Throw the exception
                    throw new RuntimeException(exception);
                }
            };
        }
        return this.closeExchangeCallback;
    }

    @Override
    public void flushBytes(Object exchange, byte[] bytes, boolean end) {

        Sender responseSender = ((HttpServerExchange)exchange).getResponseSender();

        //==========================================
        // Use the default do-nothing callback or the one
        // that will close the exchange?
        //==========================================
        IoCallback callback = end ? getCloseExchangeCallback() : getDoNothingCallback();

        responseSender.send(ByteBuffer.wrap(bytes), callback);
    }

    @Override
    public void end(Object exchange) {

        try {
            ((HttpServerExchange)exchange).endExchange();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public boolean isResponseClosed(Object exchange) {
        return ((HttpServerExchange)exchange).isResponseComplete();
    }

    @Override
    public boolean isResponseHeadersSent(Object exchange) {
        return ((HttpServerExchange)exchange).isResponseStarted();
    }

    @Override
    public String getRequestScheme(Object exchange) {
        return ((HttpServerExchange)exchange).getRequestScheme();
    }

    @Override
    public void addCookies(Object exchange, Map<String, Cookie> cookies) {

        if (cookies == null) {
            return;
        }

        Map<String, io.undertow.server.handlers.Cookie> undertowResponseCookiesMap =
                ((HttpServerExchange)exchange).getResponseCookies();

        for (Cookie cookie : cookies.values()) {

            String name = cookie.getName();
            String value = cookie.getValue();
            try {
                // Try to set "bœuf" as the cookie name or value without
                // url encoding them!
                if (name != null) {
                    name = URLEncoder.encode(name, getCookieEncoding());
                }
                if (value != null) {
                    value = URLEncoder.encode(value, getCookieEncoding());
                }
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }

            io.undertow.server.handlers.Cookie undertowCookie = new CookieImpl(name);
            undertowCookie.setValue(value);
            undertowCookie.setDiscard(cookie.isDiscard());
            undertowCookie.setDomain(cookie.getDomain());
            undertowCookie.setExpires(cookie.getExpires());
            undertowCookie.setHttpOnly(cookie.isHttpOnly());
            CookieSameSite sameSite = cookie.getSameSite();
            if (sameSite != null) {
                undertowCookie.setSameSite(true);
                undertowCookie.setSameSiteMode(sameSite.toString());
            }
            undertowCookie.setPath(cookie.getPath());
            undertowCookie.setSecure(cookie.isSecure());
            undertowCookie.setVersion(cookie.getVersion());

            undertowResponseCookiesMap.put(name, undertowCookie);
        }
    }

    @Override
    public Map<String, String> getCookies(Object exchange) {

        Map<String, String> cookies = new HashMap<String, String>();

        //==========================================
        // Get current cookies from the request
        //==========================================
        Map<String, io.undertow.server.handlers.Cookie> undertowRequestCookies =
                ((HttpServerExchange)exchange).getRequestCookies();
        if (undertowRequestCookies != null) {
            for (io.undertow.server.handlers.Cookie undertowCookie : undertowRequestCookies.values()) {

                String name = undertowCookie.getName();
                String value = undertowCookie.getValue();
                try {
                    if (name != null) {
                        name = URLDecoder.decode(name, getCookieEncoding());
                    }
                    if (value != null) {
                        value = URLDecoder.decode(value, getCookieEncoding());
                    }
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }

                cookies.put(name, value);
            }
        }

        return cookies;
    }

    protected String getCookieEncoding() {
        return "UTF-8";
    }

    @Override
    public Map<String, List<String>> getQueryStringParams(Object exchange) {

        Map<String, List<String>> queryStringParams = new HashMap<String, List<String>>();

        Map<String, Deque<String>> queryParameters = ((HttpServerExchange)exchange).getQueryParameters();
        if (queryParameters != null) {
            for (Entry<String, Deque<String>> entry : queryParameters.entrySet()) {
                List<String> list = new LinkedList<String>(entry.getValue());
                queryStringParams.put(entry.getKey(), list);
            }
        }

        return queryStringParams;
    }

    @Override
    public InputStream getRawInputStream(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        exchange.startBlocking();

        return exchange.getInputStream();
    }

    /**
     * May return NULL.
     */
    protected FormData getFormData(HttpServerExchange exchange) {
        try {
            FormDataParser formDataParser = getFormParserFactory().createParser(exchange);
            if (formDataParser == null) {
                return null;
            }
            formDataParser.setCharacterEncoding(getSpincastUndertowConfig().getHtmlFormEncoding());

            if (!exchange.isBlocking()) {
                exchange.startBlocking();
            }

            FormData formData = formDataParser.parseBlocking();
            return formData;
        } catch (Exception ex) {

            if (ex.getCause() instanceof NoSuchFileException) {
                createUndertowTempDir();
            }

            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, List<String>> getFormData(Object exchangeObj) {

        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<String>> postParams = new HashMap<String, List<String>>();

        FormData formData = getFormData(exchange);
        if (formData != null) {

            for (String key : formData) {

                Deque<FormValue> values = formData.get(key);
                if (values != null) {

                    List<String> finalValues = new ArrayList<String>();
                    for (FormValue formValue : values) {
                        boolean isFile = formValue.isFileItem();
                        if (isFile) {
                            continue;
                        }
                        String value = formValue.getValue();
                        if (value != null) {
                            finalValues.add(value);
                        }
                    }

                    postParams.put(key, finalValues);
                }
            }
        }

        return postParams;
    }

    @Override
    public Map<String, List<UploadedFile>> getUploadedFiles(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<UploadedFile>> uploadedFiles = new HashMap<String, List<UploadedFile>>();

        FormData formData = getFormData(exchange);
        if (formData != null) {

            for (String key : formData) {

                Deque<FormValue> values = formData.get(key);
                if (values != null) {

                    List<UploadedFile> finalFiles = new ArrayList<UploadedFile>();
                    for (FormValue formValue : values) {
                        boolean isFile = formValue.isFileItem();
                        if (!isFile) {
                            continue;
                        }
                        File file = formValue.getFileItem().getFile().toFile();
                        if (file != null) {
                            String fileName = formValue.getFileName();
                            if (StringUtils.isBlank(fileName)) {
                                fileName = UUID.randomUUID().toString();
                            }

                            UploadedFile uploadedFile = new UploadedFileDefault(file, fileName);
                            finalFiles.add(uploadedFile);
                        }
                    }

                    uploadedFiles.put(key, finalFiles);
                }
            }
        }

        return uploadedFiles;
    }

    @Override
    public boolean forceRequestSizeValidation(Object exchangeObj) {

        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        if (exchange.isRequestComplete()) {
            return true;
        }

        try {
            ByteBuffer b = ByteBuffer.allocate(200);
            exchange.getRequestChannel().read(b);
        } catch (Exception ex) {

            String message = ex.getMessage();
            if (message != null && message.contains(UNDERTOW_EXCEPTION_CODE_REQUEST_TOO_LARGE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The names of the headers are <em>case insensitive</em>.
     */
    @Override
    public Map<String, List<String>> getRequestHeaders(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        //==========================================
        // Map that guarantees case insensitivity...
        //==========================================
        TreeMap<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

        HeaderMap requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders != null) {
            for (HeaderValues requestHeader : requestHeaders) {
                HttpString headerNameObj = requestHeader.getHeaderName();
                if (headerNameObj == null) {
                    continue;
                }

                List<String> values = new ArrayList<String>();
                for (String value : requestHeader) {
                    values.add(value);
                }
                headers.put(headerNameObj.toString(), values);
            }
        }

        return headers;
    }

    /**
     * Gets the creation/close lock for a specific Websocket endpoint.
     */
    protected Object getWebsocketEndpointCreationLock(String endpointId) {
        Object lock = this.websocketEndpointCreationLocks.get(endpointId);
        if (lock == null) {
            synchronized (this.websocketEndpointLockCreationLock) {
                lock = this.websocketEndpointCreationLocks.get(endpointId);
                if (lock == null) {
                    lock = new Object();
                    this.websocketEndpointCreationLocks.put(endpointId, lock);
                }
            }
        }
        return lock;
    }

    @Override
    public WebsocketEndpointManager websocketCreateEndpoint(final String endpointId,
                                                            final WebsocketEndpointHandler appEndpointHandler) {

        Object lock = getWebsocketEndpointCreationLock(endpointId);
        synchronized (lock) {
            WebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
            if (websocketEndpoint != null) {
                throw new RuntimeException("The endpoint '" + endpointId + "' already exists.");
            }

            //==========================================
            // We wrap the app endpoint handler to add
            // extra event listeners.
            //==========================================
            WebsocketEndpointHandler undertowEndpointHandler =
                    createUndertowWebsocketEndpointHandler(endpointId, appEndpointHandler);

            websocketEndpoint = getSpincastWebsocketEndpointFactory().create(endpointId, undertowEndpointHandler);
            getWebsocketEndpointsMap().put(endpointId, websocketEndpoint);

            return websocketEndpoint;
        }
    }

    protected WebsocketEndpointHandler createUndertowWebsocketEndpointHandler(final String endpointId,
                                                                              final WebsocketEndpointHandler appHandler) {
        return new WebsocketEndpointHandler() {

            @Override
            public void onPeerMessage(String peerId, byte[] message) {
                appHandler.onPeerMessage(peerId, message);
            }

            @Override
            public void onPeerMessage(String peerId, String message) {
                appHandler.onPeerMessage(peerId, message);
            }

            @Override
            public void onPeerConnected(String peerId) {
                appHandler.onPeerConnected(peerId);
            }

            @Override
            public void onPeerClosed(String peerId) {
                appHandler.onPeerClosed(peerId);
            }

            @Override
            public void onEndpointClosed() {

                //==========================================
                // We remove the endpoint from our local Map.
                //==========================================
                getWebsocketEndpointsMap().remove(endpointId);

                appHandler.onEndpointClosed();
            }
        };
    }

    @Override
    public void websocketCloseEndpoint(String endpointId) {
        websocketCloseEndpoint(endpointId,
                               getSpincastUndertowConfig().getWebsocketDefaultClosingCode(),
                               getSpincastUndertowConfig().getWebsocketDefaultClosingReason());
    }

    @Override
    public void websocketCloseEndpoint(String endpointId, int closingCode, String closingReason) {

        Object lock = getWebsocketEndpointCreationLock(endpointId);
        synchronized (lock) {
            WebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
            if (websocketEndpoint == null) {
                this.logger.warn("No Websocket endpoint with id '" + endpointId + "' exists...");
                return;
            }

            //==========================================
            // This will close the endpoint and call the 
            // "onEndpointClosed" event for which we have
            // a listener: it's this listener that will remove the
            // endpoint from the local Map.
            //==========================================
            websocketEndpoint.closeEndpoint(closingCode, closingReason);
        }
    }

    @Override
    public void websocketConnection(Object exchangeObj,
                                    final String endpointId,
                                    final String peerId) {

        if (StringUtils.isBlank(endpointId)) {
            throw new RuntimeException("The endpoint id can't be empty.");
        }

        if (StringUtils.isBlank(peerId)) {
            throw new RuntimeException("The peer id can't be empty.");
        }

        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        WebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
        if (websocketEndpoint == null) {
            throw new RuntimeException("The Websocket endpoint '" + endpointId + "' doesn't exist.");
        }

        if (websocketEndpoint.getPeersIds().contains(peerId)) {
            throw new RuntimeException("The Websocket endpoint '" + endpointId + "' is already used by a peer with " +
                                       "id '" + peerId + "'! Close the existing peer if you want to reuse this id.");
        }

        websocketEndpoint.handleConnectionRequest(exchange, peerId);
    }

    @Override
    public List<WebsocketEndpointManager> getWebsocketEndpointManagers() {
        return new ArrayList<WebsocketEndpointManager>(getWebsocketEndpointsMap().values());
    }

    @Override
    public WebsocketEndpointManager getWebsocketEndpointManager(String endpointId) {
        return getWebsocketEndpointsMap().get(endpointId);
    }

    @Override
    public String getIp(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        List<String> xForwardedForHeaders = getRequestHeaders(exchangeObj).get(HttpHeaders.X_FORWARDED_FOR);
        if (xForwardedForHeaders != null && xForwardedForHeaders.size() > 0) {
            String xForwardedForHeader = xForwardedForHeaders.get(0);

            try {
                return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
            } catch (Exception ex) {
                // ok
            }
        }

        return new StringTokenizer(exchange.getSourceAddress().getAddress().toString(), "/").nextToken().trim();
    }

}

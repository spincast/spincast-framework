package org.spincast.plugins.undertow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.controllers.IFrontController;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.ssl.ISSLContextFactory;
import org.spincast.core.websocket.IWebsocketEndpointHandler;
import org.spincast.core.websocket.IWebsocketEndpointManager;
import org.spincast.plugins.undertow.config.ISpincastUndertowConfig;
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
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

/**
 * Server implementation for Undertow.
 */
public class SpincastUndertowServer implements IServer {

    protected final Logger logger = LoggerFactory.getLogger(SpincastUndertowServer.class);

    public static final String UNDERTOW_EXCEPTION_CODE_REQUEST_TOO_LARGE = "UT000020";

    private final IWebsocketEndpointFactory spincastWebsocketEndpointFactory;
    private final ISpincastUtils spincastUtils;
    private final ISpincastConfig config;
    private final ISpincastUndertowConfig spincastUndertowConfig;
    private final IFrontController frontController;
    private final ICookieFactory cookieFactory;
    private final ICorsHandlerFactory corsHandlerFactory;
    private final IGzipCheckerHandlerFactory gzipCheckerHandlerFactory;
    private final IFileClassPathResourceManagerFactory fileClassPathResourceManagerFactory;
    private final ISpincastHttpAuthIdentityManagerFactory spincastHttpAuthIdentityManagerFactory;
    private final ISSLContextFactory sslContextFactory;

    private Undertow undertowServer;
    private IoCallback doNothingCallback = null;
    private IoCallback closeExchangeCallback = null;

    private final Map<String, IStaticResource<?>> staticResourcesServedByUrlPath = new HashMap<String, IStaticResource<?>>();

    private final Map<String, String> httpAuthActiveRealms = new HashMap<String, String>();

    private final Map<String, ISpincastHttpAuthIdentityManager> httpAuthIdentityManagersByRealmName =
            new HashMap<String, ISpincastHttpAuthIdentityManager>();

    //==========================================
    // Websocket endpoints, with there id as the key.
    //==========================================
    private final Map<String, IWebsocketEndpoint> websocketEndpointsMap =
            new ConcurrentHashMap<String, IWebsocketEndpoint>();

    private HttpHandler spincastFrontControllerHandler;
    private PathHandler staticResourcesPathHandler;
    private PathHandler httpAuthenticationHandler;

    private FormParserFactory formParserFactory;

    private final Map<String, Object> websocketEndpointCreationLocks = new ConcurrentHashMap<String, Object>();
    private final Object websocketEndpointLockCreationLock = new Object();

    /**
     * Constructor
     */
    @Inject
    public SpincastUndertowServer(ISpincastConfig config,
                                  ISpincastUndertowConfig spincastUndertowConfig,
                                  IFrontController frontController,
                                  ISpincastUtils spincastUtils,
                                  ICookieFactory cookieFactory,
                                  ICorsHandlerFactory corsHandlerFactory,
                                  IGzipCheckerHandlerFactory gzipCheckerHandlerFactory,
                                  IFileClassPathResourceManagerFactory fileClassPathResourceManagerFactory,
                                  ISpincastHttpAuthIdentityManagerFactory spincastHttpAuthIdentityManagerFactory,
                                  IWebsocketEndpointFactory spincastWebsocketEndpointFactory,
                                  ISSLContextFactory sslContextFactory) {
        this.config = config;
        this.spincastUndertowConfig = spincastUndertowConfig;
        this.frontController = frontController;
        this.spincastUtils = spincastUtils;
        this.cookieFactory = cookieFactory;
        this.corsHandlerFactory = corsHandlerFactory;
        this.gzipCheckerHandlerFactory = gzipCheckerHandlerFactory;
        this.fileClassPathResourceManagerFactory = fileClassPathResourceManagerFactory;
        this.spincastHttpAuthIdentityManagerFactory = spincastHttpAuthIdentityManagerFactory;
        this.spincastWebsocketEndpointFactory = spincastWebsocketEndpointFactory;
        this.sslContextFactory = sslContextFactory;
    }

    protected ISpincastConfig getConfig() {
        return this.config;
    }

    protected ISpincastUndertowConfig getSpincastUndertowConfig() {
        return this.spincastUndertowConfig;
    }

    protected IFrontController getFrontController() {
        return this.frontController;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ICookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected ICorsHandlerFactory getCorsHandlerFactory() {
        return this.corsHandlerFactory;
    }

    protected IGzipCheckerHandlerFactory getGzipCheckerHandlerFactory() {
        return this.gzipCheckerHandlerFactory;
    }

    protected IFileClassPathResourceManagerFactory getFileClassPathResourceManagerFactory() {
        return this.fileClassPathResourceManagerFactory;
    }

    protected ISpincastHttpAuthIdentityManagerFactory getSpincastHttpAuthIdentityManagerFactory() {
        return this.spincastHttpAuthIdentityManagerFactory;
    }

    protected IWebsocketEndpointFactory getSpincastWebsocketEndpointFactory() {
        return this.spincastWebsocketEndpointFactory;
    }

    protected Map<String, IStaticResource<?>> getStaticResourcesServedByUrlPath() {
        return this.staticResourcesServedByUrlPath;
    }

    protected Map<String, ISpincastHttpAuthIdentityManager> getHttpAuthIdentityManagersByRealmName() {
        return this.httpAuthIdentityManagersByRealmName;
    }

    protected Map<String, IWebsocketEndpoint> getWebsocketEndpointsMap() {
        return this.websocketEndpointsMap;
    }

    protected Map<String, String> getHttpAuthActiveRealms() {
        return this.httpAuthActiveRealms;
    }

    protected ISSLContextFactory getSslContextFactory() {
        return this.sslContextFactory;
    }

    @Override
    public Map<String, String> getHttpAuthenticationRealms() {
        return Collections.unmodifiableMap(getHttpAuthActiveRealms());
    }

    protected FormParserFactory getFormParserFactory() {
        if(this.formParserFactory == null) {
            this.formParserFactory = FormParserFactory.builder().build();
        }
        return this.formParserFactory;
    }

    @Override
    public synchronized void start() {

        if(this.undertowServer != null) {
            this.logger.warn("Server already started.");
            return;
        }

        this.undertowServer = getServerBuilder().build();

        int serverStartTryNbr = getServerStartTryNbr();
        for(int i = 0; i < serverStartTryNbr; i++) {

            try {
                this.undertowServer.start();
                break;
            } catch(Exception ex) {
                if(ex instanceof BindException || ex.getCause() != null && ex.getCause() instanceof BindException) {
                    this.logger.warn("BindException while trying to start the server. Try " + i + " of " + serverStartTryNbr +
                                     "...");
                    if(i == (serverStartTryNbr - 1)) {
                        try {
                            Thread.sleep(getStartServerSleepMilliseconds());
                        } catch(InterruptedException e) {
                        }
                    }
                    continue;
                }
                this.undertowServer = null;
                throw ex;
            }
        }

        if(getConfig().getHttpServerPort() > 0) {
            this.logger.info("HTTP server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpServerPort());
        }
        if(getConfig().getHttpsServerPort() > 0) {
            this.logger.info("HTTPS server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpsServerPort());
        }
    }

    protected int getServerStartTryNbr() {
        return 5;
    }

    protected long getStartServerSleepMilliseconds() {
        return 500;
    }

    protected Builder getServerBuilder() {

        try {
            String serverHost = getConfig().getServerHost();
            int httpServerPort = getConfig().getHttpServerPort();
            int httpsServerPort = getConfig().getHttpsServerPort();

            if(httpServerPort <= 0 && httpsServerPort <= 0) {
                throw new RuntimeException("At least one of the HTTP or HTTPS port must " +
                                           "be greater than 0 to start the server....");
            }

            Builder builder = Undertow.builder().setHandler(getFinalHandler());

            if(httpServerPort > 0) {
                addHttpListener(builder, serverHost, httpServerPort);
            }
            if(httpsServerPort > 0) {
                addHttpsListener(builder, serverHost, httpsServerPort);
            }

            builder = addBuilderOptions(builder);

            return builder;
        } catch(Exception ex) {
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
                                                                            getConfig().getHttpsKeyStoreKeypass());

            builder = builder.addHttpsListener(httpsServerPort, serverHost, sslContext);

        } catch(Exception ex) {
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
        return getHttpAuthenticationHandler();
    }

    /**
     * Handler to check for HTTP authentication requirement.
     */
    protected PathHandler getHttpAuthenticationHandler() {
        if(this.httpAuthenticationHandler == null) {
            this.httpAuthenticationHandler = new PathHandler(getHttpAuthHandlerNextHandler());
        }
        return this.httpAuthenticationHandler;
    }

    protected HttpHandler getHttpAuthHandlerNextHandler() {
        return getStaticResourcesPathHandler();
    }

    @Override
    public void createHttpAuthenticationRealm(String pathPrefix, String realmName) {

        if(getHttpAuthActiveRealms().containsKey(realmName)) {
            throw new RuntimeException("A HTTP authentication realm named '" + realmName + "' " +
                                       "already exists for path: " + pathPrefix);
        }

        ISpincastHttpAuthIdentityManager identityManager = getOrCreateHttpAuthIdentityManagersByRealmName(realmName);

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

    protected ISpincastHttpAuthIdentityManager getOrCreateHttpAuthIdentityManagersByRealmName(String realmName) {
        ISpincastHttpAuthIdentityManager identityManager = getHttpAuthIdentityManagersByRealmName().get(realmName);
        if(identityManager == null) {
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
        ISpincastHttpAuthIdentityManager identityManager = getOrCreateHttpAuthIdentityManagersByRealmName(realmName);
        identityManager.addUser(username, password);
    }

    @Override
    public void removeHttpAuthentication(String username, String realmName) {
        ISpincastHttpAuthIdentityManager identityManager = getHttpAuthIdentityManagersByRealmName().get(realmName);
        if(identityManager != null) {
            identityManager.removeUser(username);
        }
    }

    @Override
    public void removeHttpAuthentication(String username) {
        for(ISpincastHttpAuthIdentityManager identityManager : getHttpAuthIdentityManagersByRealmName().values()) {
            identityManager.removeUser(username);
        }
    }

    protected HttpHandler getSpincastFrontControllerHandler() {
        if(this.spincastFrontControllerHandler == null) {
            this.spincastFrontControllerHandler = new HttpHandler() {

                /**
                 * If we enter here, it means the request is not for a static resource
                 * so we target Spincast's front controller.
                 */
                @Override
                public void handleRequest(HttpServerExchange exchange) throws Exception {

                    // @see http://undertow.io/undertow-docs/undertow-docs-1.3.0/index.html#dispatch-code
                    if(exchange.isInIoThread()) {
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
    public synchronized void stop() {

        if(this.undertowServer != null) {

            //==========================================
            // If there are some active Websocket endpoints,
            // we try to send a "close" event to the peers before
            // closing the server.
            //==========================================
            try {
                sendWebsocketEnpointsClosedWhenServerStops();
            } finally {
                try {
                    this.undertowServer.stop();
                    this.undertowServer = null;
                } catch(Exception ex) {
                    this.logger.error("Error stopping the Undertow server :\n" + SpincastStatics.getStackTrace(ex));
                }
            }
        }
    }

    protected void sendWebsocketEnpointsClosedWhenServerStops() {
        Collection<IWebsocketEndpoint> websocketEndpointsMap = getWebsocketEndpointsMap().values();
        List<IWebsocketEndpoint> websocketEndpoints = new ArrayList<IWebsocketEndpoint>(websocketEndpointsMap);
        for(IWebsocketEndpoint websocketEndpoint : websocketEndpoints) {
            try {
                websocketEndpoint.closeEndpoint();
            } catch(Exception ex) {
                this.logger.warn("Error closing Websocket '" + websocketEndpoint.getEndpointId() + "': " +
                                 ex.getMessage());
            }
        }
    }

    protected PathHandler getStaticResourcesPathHandler() {
        if(this.staticResourcesPathHandler == null) {

            //==========================================
            // If no static resource path match, we
            // call the framework.
            //==========================================
            this.staticResourcesPathHandler = new PathHandler(getSpincastFrontControllerHandler());
        }
        return this.staticResourcesPathHandler;
    }

    @Override
    public void addStaticResourceToServe(final IStaticResource<?> staticResource) {

        Map<String, IStaticResource<?>> staticResourcesServedByUrlPath = getStaticResourcesServedByUrlPath();

        StaticResourceType staticResourceType = staticResource.getStaticResourceType();

        //==========================================
        // We remove the existing entry, if any.
        //==========================================
        if(staticResourcesServedByUrlPath.containsKey(staticResource.getUrlPath())) {
            getStaticResourcesPathHandler().removeExactPath(staticResource.getUrlPath());
            getStaticResourcesPathHandler().removePrefixPath(staticResource.getUrlPath());
        }
        staticResourcesServedByUrlPath.put(staticResource.getUrlPath(), staticResource);

        if(staticResourceType == StaticResourceType.FILE) {
            File file = new File(staticResource.getResourcePath());
            if(!file.isFile() && !staticResource.isCanBeGenerated()) {
                throw new RuntimeException("The file doesn't exist and can't be generated so it can't be served : " +
                                           staticResource.getResourcePath());
            }

            //==========================================
            // If the resource can be generated and is not found 
            // we call the framework instead of returning 404.
            //==========================================
            HttpHandler next = staticResource.isCanBeGenerated() ? getSpincastFrontControllerHandler()
                                                                 : ResponseCodeHandler.HANDLE_404;

            ResourceHandler resourceHandler = new ResourceHandler(new FileResourceManager(file, 1024), next);
            IGzipCheckerHandler gzipCheckerHandler =
                    getGzipCheckerHandlerFactory().create(resourceHandler, file.getAbsolutePath());
            ICorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addExactPath(staticResource.getUrlPath(), corsHandler);

        } else if(staticResourceType == StaticResourceType.FILE_FROM_CLASSPATH) {
            String classpathPath = staticResource.getResourcePath();
            if(classpathPath == null) {
                classpathPath = "";
            } else if(classpathPath.startsWith("/")) {
                classpathPath = classpathPath.substring(1);
            }

            ResourceHandler resourceHandler = new ResourceHandler(getFileClassPathResourceManagerFactory().create(classpathPath));
            IGzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, classpathPath);
            ICorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addExactPath(staticResource.getUrlPath(), corsHandler);

        } else if(staticResourceType == StaticResourceType.DIRECTORY) {
            File dir = new File(staticResource.getResourcePath());
            if(!dir.isDirectory() && !staticResource.isCanBeGenerated()) {
                throw new RuntimeException("The directory doesn't exist and can't be generated so it can't be served : " +
                                           staticResource.getResourcePath());
            }

            //==========================================
            // If the resource can be generated and is not found 
            // we call the framework instead of returning 404.
            //==========================================
            HttpHandler next = staticResource.isCanBeGenerated() ? getSpincastFrontControllerHandler()
                                                                 : ResponseCodeHandler.HANDLE_404;

            ResourceHandler resourceHandler = new ResourceHandler(new FileResourceManager(dir, 1024), next);
            IGzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, null);
            ICorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addPrefixPath(staticResource.getUrlPath(), corsHandler);

        } else if(staticResourceType == StaticResourceType.DIRECTORY_FROM_CLASSPATH) {
            String classpathPath = staticResource.getResourcePath();
            if(classpathPath == null) {
                classpathPath = "";
            } else if(classpathPath.startsWith("/")) {
                classpathPath = classpathPath.substring(1);
            }

            ResourceHandler resourceHandler =
                    new ResourceHandler(new ClassPathResourceManager(SpincastUndertowServer.class.getClassLoader(),
                                                                     classpathPath));
            IGzipCheckerHandler gzipCheckerHandler = getGzipCheckerHandlerFactory().create(resourceHandler, null);
            ICorsHandler corsHandler = getCorsHandlerFactory().create(gzipCheckerHandler, staticResource.getCorsConfig());

            getStaticResourcesPathHandler().addPrefixPath(staticResource.getUrlPath(), corsHandler);

        } else {
            throw new RuntimeException("Unamanaged static resource stype : " + staticResourceType);
        }
    }

    @Override
    public void removeStaticResourcesServed(StaticResourceType staticResourceType, String urlPath) {

        if(this.staticResourcesServedByUrlPath.containsKey(urlPath)) {
            removeStaticResource(staticResourceType, urlPath);
        }
    }

    @Override
    public void removeAllStaticResourcesServed() {

        for(Entry<String, IStaticResource<?>> entry : getStaticResourcesServedByUrlPath().entrySet()) {

            String urlPath = entry.getKey();
            IStaticResource<?> staticResource = entry.getValue();

            removeStaticResource(staticResource.getStaticResourceType(), urlPath);
        }
    }

    protected void removeStaticResource(StaticResourceType staticResourceType, String urlPath) {
        if(staticResourceType == StaticResourceType.FILE ||
           staticResourceType == StaticResourceType.FILE_FROM_CLASSPATH) {
            getStaticResourcesPathHandler().removeExactPath(urlPath);
        } else if(staticResourceType == StaticResourceType.DIRECTORY ||
                  staticResourceType == StaticResourceType.DIRECTORY_FROM_CLASSPATH) {
            getStaticResourcesPathHandler().removePrefixPath(urlPath);
        } else {
            throw new RuntimeException("Unamanaged static resource stype : " + staticResourceType);
        }
    }

    @Override
    public IStaticResource<?> getStaticResourceServed(String urlPath) {
        return getStaticResourcesServedByUrlPath().get(urlPath);
    }

    @Override
    public Set<IStaticResource<?>> getStaticResourcesServed() {
        return new HashSet<IStaticResource<?>>(getStaticResourcesServedByUrlPath().values());
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
        if(requestHeaders != null) {

            String requestedWith = requestHeaders.getFirst("X-Requested-With");
            if("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                return ContentTypeDefaults.JSON;
            }
            String accept = requestHeaders.getFirst("Accept");
            if(accept != null) {
                String bestMatch = MIMEParse.bestMatch(ContentTypeDefaults.getAllContentTypesVariations(), accept);
                if(!StringUtils.isBlank(bestMatch)) {
                    type = ContentTypeDefaults.fromString(bestMatch);
                    if(type == null) {
                        this.logger.error("Not supposed : " + bestMatch);
                        type = ContentTypeDefaults.TEXT;
                    }
                }
            }
        }
        return type;
    }

    @Override
    public String getFullUrl(Object exchangeObj) {

        try {
            HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);
            String queryString = exchange.getQueryString();
            if(StringUtils.isBlank(queryString)) {
                queryString = "";
            } else {
                queryString = "?" + queryString;
            }
            
            
            String requestURL = exchange.getRequestURL();
            
            //==========================================
            // If we are behind a reverse-proxy, the original
            // scheme can be different.
            //==========================================
            HeaderValues protoHeader = exchange.getRequestHeaders().get(HttpHeaders.X_FORWARDED_PROTO);
            if(protoHeader != null && protoHeader.getFirst() != null) {
                String protoHeaderStr = protoHeader.getFirst().toLowerCase();
                
                int pos = requestURL.indexOf(":");
                if(pos < 0) {
                    throw new RuntimeException("':' not found: " + requestURL);  
                } 
                
                if(!(requestURL.substring(0, pos).toLowerCase().equals(protoHeaderStr))) {
                    requestURL = protoHeaderStr + requestURL.substring(pos);
                }
            }
            
            return requestURL + queryString;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void setResponseHeader(Object exchangeObj, String name, List<String> values) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);
        HeaderMap responseHeaderMap = ((HttpServerExchange)exchange).getResponseHeaders();
        responseHeaderMap.putAll(new HttpString(name), values);
    }

    @Override
    public void setResponseHeaders(Object exchange, Map<String, List<String>> headers) {

        HeaderMap responseHeaderMap = ((HttpServerExchange)exchange).getResponseHeaders();
        responseHeaderMap.clear();

        if(headers == null || headers.size() == 0) {
            return;
        }

        for(Entry<String, List<String>> entry : headers.entrySet()) {
            responseHeaderMap.putAll(new HttpString(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public Map<String, List<String>> getResponseHeaders(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<String>> headers = new HashMap<String, List<String>>();

        HeaderMap responseHeaders = exchange.getResponseHeaders();
        if(responseHeaders != null) {
            for(HeaderValues responseHeader : responseHeaders) {
                HttpString headerNameObj = responseHeader.getHeaderName();
                if(headerNameObj == null) {
                    continue;
                }

                List<String> values = new ArrayList<String>();
                for(String value : responseHeader) {
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
        ((HttpServerExchange)exchange).setResponseCode(statusCode);
    }

    protected IoCallback getDoNothingCallback() {
        if(this.doNothingCallback == null) {
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
        if(this.closeExchangeCallback == null) {
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
        } catch(Exception ex) {
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
    public void addCookies(Object exchange, Map<String, ICookie> cookies) {

        if(cookies == null) {
            return;
        }

        Map<String, Cookie> undertowResponseCookiesMap = ((HttpServerExchange)exchange).getResponseCookies();

        for(ICookie cookie : cookies.values()) {

            String name = cookie.getName();
            String value = cookie.getValue();
            try {
                // Try to set "bœuf" as the cookie name or value without
                // url encoding them!
                if(name != null) {
                    name = URLEncoder.encode(name, getCookieEncoding());
                }
                if(value != null) {
                    value = URLEncoder.encode(value, getCookieEncoding());
                }
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }

            Cookie undertowCookie = new CookieImpl(name);
            undertowCookie.setValue(value);
            undertowCookie.setDiscard(cookie.isDiscard());
            undertowCookie.setDomain(cookie.getDomain());
            undertowCookie.setExpires(cookie.getExpires());
            undertowCookie.setHttpOnly(cookie.isHttpOnly());
            undertowCookie.setPath(cookie.getPath());
            undertowCookie.setSecure(cookie.isSecure());
            undertowCookie.setVersion(cookie.getVersion());

            undertowResponseCookiesMap.put(name, undertowCookie);
        }
    }

    @Override
    public Map<String, ICookie> getCookies(Object exchange) {

        Map<String, ICookie> cookies = new HashMap<String, ICookie>();

        //==========================================
        // Get current cookies from the request
        //==========================================
        Map<String, Cookie> undertowRequestCookies = ((HttpServerExchange)exchange).getRequestCookies();
        if(undertowRequestCookies != null) {
            for(Cookie undertowCookie : undertowRequestCookies.values()) {

                String name = undertowCookie.getName();
                String value = undertowCookie.getValue();
                try {
                    if(name != null) {
                        name = URLDecoder.decode(name, getCookieEncoding());
                    }
                    if(value != null) {
                        value = URLDecoder.decode(value, getCookieEncoding());
                    }
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }

                ICookie spincastCookie = getCookieFactory().createCookie(name,
                                                                         value,
                                                                         undertowCookie.getPath(),
                                                                         undertowCookie.getDomain(),
                                                                         undertowCookie.getExpires(),
                                                                         undertowCookie.isSecure(),
                                                                         undertowCookie.isHttpOnly(),
                                                                         undertowCookie.isDiscard(),
                                                                         undertowCookie.getVersion());
                cookies.put(spincastCookie.getName(), spincastCookie);
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
        if(queryParameters != null) {
            for(Entry<String, Deque<String>> entry : queryParameters.entrySet()) {
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
            if(formDataParser == null) {
                return null;
            }

            if(!exchange.isBlocking()) {
                exchange.startBlocking();
            }

            FormData formData = formDataParser.parseBlocking();
            return formData;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, List<String>> getFormDatas(Object exchangeObj) {

        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<String>> postParams = new HashMap<String, List<String>>();

        FormData formData = getFormData(exchange);
        if(formData != null) {

            for(String key : formData) {

                Deque<FormValue> values = formData.get(key);
                if(values != null) {

                    List<String> finalValues = new ArrayList<String>();
                    for(FormValue formValue : values) {
                        if(formValue.isFile()) {
                            continue;
                        }
                        String value = formValue.getValue();
                        if(value != null) {
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
    public Map<String, List<File>> getUploadedFiles(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<File>> uploadedFiles = new HashMap<String, List<File>>();

        FormData formData = getFormData(exchange);
        if(formData != null) {

            for(String key : formData) {

                Deque<FormValue> values = formData.get(key);
                if(values != null) {

                    List<File> finalFiles = new ArrayList<File>();
                    for(FormValue formValue : values) {
                        if(!formValue.isFile()) {
                            continue;
                        }
                        File file = formValue.getFile();
                        if(file != null) {
                            finalFiles.add(file);
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

        if(exchange.isRequestComplete()) {
            return true;
        }

        try {
            ByteBuffer b = ByteBuffer.allocate(200);
            exchange.getRequestChannel().read(b);
        } catch(Exception ex) {

            String message = ex.getMessage();
            if(message != null && message.contains(UNDERTOW_EXCEPTION_CODE_REQUEST_TOO_LARGE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, List<String>> getRequestHeaders(Object exchangeObj) {
        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        Map<String, List<String>> headers = new HashMap<String, List<String>>();

        HeaderMap requestHeaders = exchange.getRequestHeaders();
        if(requestHeaders != null) {
            for(HeaderValues requestHeader : requestHeaders) {
                HttpString headerNameObj = requestHeader.getHeaderName();
                if(headerNameObj == null) {
                    continue;
                }

                //==========================================
                // All header names must be lowercased to respect the
                // IServer interface.
                //==========================================
                String headerNameLowercased = headerNameObj.toString().toLowerCase();

                List<String> values = new ArrayList<String>();
                for(String value : requestHeader) {
                    values.add(value);
                }
                headers.put(headerNameLowercased, values);
            }
        }

        return headers;
    }

    /**
     * Gets the creation/close lock for a specific Websocket endpoint.
     */
    protected Object getWebsocketEndpointCreationLock(String endpointId) {
        Object lock = this.websocketEndpointCreationLocks.get(endpointId);
        if(lock == null) {
            synchronized(this.websocketEndpointLockCreationLock) {
                lock = this.websocketEndpointCreationLocks.get(endpointId);
                if(lock == null) {
                    lock = new Object();
                    this.websocketEndpointCreationLocks.put(endpointId, lock);
                }
            }
        }
        return lock;
    }

    @Override
    public IWebsocketEndpointManager websocketCreateEndpoint(final String endpointId,
                                                             final IWebsocketEndpointHandler appEndpointHandler) {

        Object lock = getWebsocketEndpointCreationLock(endpointId);
        synchronized(lock) {
            IWebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
            if(websocketEndpoint != null) {
                throw new RuntimeException("The endpoint '" + endpointId + "' already exists.");
            }

            //==========================================
            // We wrap the app endpoint handler to add
            // extra event listeners.
            //==========================================
            IWebsocketEndpointHandler undertowEndpointHandler =
                    createUndertowWebsocketEndpointHandler(endpointId, appEndpointHandler);

            websocketEndpoint = getSpincastWebsocketEndpointFactory().create(endpointId, undertowEndpointHandler);
            getWebsocketEndpointsMap().put(endpointId, websocketEndpoint);

            return websocketEndpoint;
        }
    }

    protected IWebsocketEndpointHandler createUndertowWebsocketEndpointHandler(final String endpointId,
                                                                               final IWebsocketEndpointHandler appHandler) {
        return new IWebsocketEndpointHandler() {

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
        synchronized(lock) {
            IWebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
            if(websocketEndpoint == null) {
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

        if(StringUtils.isBlank(endpointId)) {
            throw new RuntimeException("The endpoint id can't be empty.");
        }

        if(StringUtils.isBlank(peerId)) {
            throw new RuntimeException("The peer id can't be empty.");
        }

        HttpServerExchange exchange = ((HttpServerExchange)exchangeObj);

        IWebsocketEndpoint websocketEndpoint = getWebsocketEndpointsMap().get(endpointId);
        if(websocketEndpoint == null) {
            throw new RuntimeException("The Websocket endpoint '" + endpointId + "' doesn't exist.");
        }

        if(websocketEndpoint.getPeersIds().contains(peerId)) {
            throw new RuntimeException("The Websocket endpoint '" + endpointId + "' is already used by a peer with " +
                                       "id '" + peerId + "'! Close the existing peer if you want to reuse this id.");
        }

        websocketEndpoint.handleConnectionRequest(exchange, peerId);
    }

    @Override
    public List<IWebsocketEndpointManager> getWebsocketEndpointManagers() {
        return new ArrayList<IWebsocketEndpointManager>(getWebsocketEndpointsMap().values());
    }

    @Override
    public IWebsocketEndpointManager getWebsocketEndpointManager(String endpointId) {
        return getWebsocketEndpointsMap().get(endpointId);
    }

}

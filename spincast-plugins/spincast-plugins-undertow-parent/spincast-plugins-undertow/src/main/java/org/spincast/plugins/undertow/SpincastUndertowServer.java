package org.spincast.plugins.undertow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.commonjava.mimeparse.MIMEParse;

import com.google.inject.Inject;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
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

    private final ISpincastUtils spincastUtils;
    private final ISpincastConfig config;
    private final IFrontController frontController;
    private final ICookieFactory cookieFactory;
    private final ISSLContextManager sslContextManager;
    private final ICorsHandlerFactory corsHandlerFactory;
    private final IGzipCheckerHandlerFactory gzipCheckerHandlerFactory;
    private final IFileClassPathResourceManagerFactory fileClassPathResourceManagerFactory;

    private Undertow undertowServer;
    private IoCallback doNothingCallback = null;
    private IoCallback closeExchangeCallback = null;

    private final Map<String, IStaticResource<?>> staticResourcesServedByUrlPath = new HashMap<String, IStaticResource<?>>();

    private HttpHandler spincastFrontControllerHandler;
    private PathHandler staticResourcesPathHandler;
    private FormParserFactory formParserFactory;

    /**
     * Constructor
     */
    @Inject
    public SpincastUndertowServer(ISpincastConfig config,
                                  IFrontController frontController,
                                  ISpincastUtils spincastUtils,
                                  ICookieFactory cookieFactory,
                                  ISSLContextManager sslContextManager,
                                  ICorsHandlerFactory corsHandlerFactory,
                                  IGzipCheckerHandlerFactory gzipCheckerHandlerFactory,
                                  IFileClassPathResourceManagerFactory fileClassPathResourceManagerFactory) {
        this.config = config;
        this.frontController = frontController;
        this.spincastUtils = spincastUtils;
        this.cookieFactory = cookieFactory;
        this.sslContextManager = sslContextManager;
        this.corsHandlerFactory = corsHandlerFactory;
        this.gzipCheckerHandlerFactory = gzipCheckerHandlerFactory;
        this.fileClassPathResourceManagerFactory = fileClassPathResourceManagerFactory;
    }

    protected ISpincastConfig getConfig() {
        return this.config;
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

    protected ISSLContextManager getSslContextManager() {
        return this.sslContextManager;
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

    protected Map<String, IStaticResource<?>> getStaticResourcesServedByUrlPath() {
        return this.staticResourcesServedByUrlPath;
    }

    protected FormParserFactory getFormParserFactory() {
        if(this.formParserFactory == null) {
            this.formParserFactory = FormParserFactory.builder().build();
        }
        return this.formParserFactory;
    }

    @Override
    public void start() {

        this.undertowServer = getServerBuilder().build();
        this.undertowServer.start();

        if(getConfig().getHttpServerPort() > 0) {
            this.logger.info("HTTP server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpServerPort());
        }
        if(getConfig().getHttpsServerPort() > 0) {
            this.logger.info("HTTPS server started on host/ip \"" + getConfig().getServerHost() + "\", port " +
                             getConfig().getHttpsServerPort());
        }
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

            SSLContext sslContext = getSslContextManager().getSSLContext();
            builder = builder.addHttpsListener(httpsServerPort, serverHost, sslContext);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected Builder addBuilderOptions(Builder builder) {
        builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, getConfig().getServerMaxRequestBodyBytes());
        return builder;
    }

    protected HttpHandler getFinalHandler() {
        return getStaticResourcesPathHandler();
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
    public void stop() {

        if(this.undertowServer != null) {

            try {
                this.undertowServer.stop();
            } catch(Exception ex) {
                this.logger.warn("Error stopping the Undertow server :\n" + SpincastStatics.getStackTrace(ex));
            }
        }
    }

    protected PathHandler getStaticResourcesPathHandler() {
        if(this.staticResourcesPathHandler == null) {

            //==========================================
            // If no static resource path match, our main
            // Spincast front controller handler will be used!
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

            return exchange.getRequestURL() + queryString;
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

}

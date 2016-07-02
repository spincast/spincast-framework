package org.spincast.testing.core;

import java.lang.reflect.Type;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IRouter;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.plugins.httpclient.builders.IConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.IDeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.IGetRequestBuilder;
import org.spincast.plugins.httpclient.builders.IHeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.IOptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPostRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPutRequestBuilder;
import org.spincast.plugins.httpclient.builders.ITraceRequestBuilder;
import org.spincast.plugins.httpclient.websocket.IHttpClient;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketPluginGuiceModule;
import org.spincast.plugins.httpclient.websocket.builders.IWebsocketRequestBuilder;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * Base class for tests that need the HTTP/WebSocket server
 * to be started.
 * 
 * <p>
 * This requires a "IServer" to be bound in the Guice 
 * context : it will automatically be stopped after the
 * test class is ran.
 * </p>
 * <p>
 * Note that this class doesn't start the server by itself because 
 * this lets the opportunity to test an application by using its true
 * bootstraping process, which usually starts a server itself!
 * </p>
 * <p>
 * All client data (such as cookies) are cleared before each test.
 * </p>
 */
public abstract class SpincastIntegrationTestBase<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                                 extends SpincastTestBase {

    @Inject
    private IHttpClient httpClient;

    @Inject
    private IServer server;

    @Inject
    private IRouter<R, W> router;

    @Inject
    private ICookieFactory cookieFactory;

    /**
     * We make sure the Spincast HTTP Client with WebSocket
     * support is bound since this class provides methods that
     * use it.
     */
    protected Module getDefaultOverridingModule(Type requestContextType, Type websocketContextType) {
        return new SpincastHttpClientWithWebsocketPluginGuiceModule(requestContextType, websocketContextType);
    }

    @Override
    public void afterClass() {
        super.afterClass();

        stopServer();
    }

    protected void stopServer() {
        if(getServer() != null) {
            getServer().stop();
        }
    }

    protected IHttpClient getHttpClient() {
        return this.httpClient;
    }

    protected IRouter<R, W> getRouter() {
        return this.router;
    }

    protected ICookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected IServer getServer() {
        return this.server;
    }

    /**
     * Creates an URL to the started HTTP server.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected String createTestUrl(String path) {
        return createTestUrl(path, false, false);
    }

    /**
     * Creates an URL to the started HTTP server.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     * 
     * @param https if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected String createTestUrl(String path, boolean https) {
        return createTestUrl(path, false, https);
    }

    /**
     * Creates an URL to the started HTTP server.
     * 
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected String createTestUrl(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        if(isFullUrl) {
            return pathOrUrl;
        }

        if(StringUtils.isBlank(pathOrUrl)) {
            pathOrUrl = "/";
        } else if(!pathOrUrl.startsWith("/")) {
            pathOrUrl = "/" + pathOrUrl;
        }

        return "http" + (isHttps ? "s" : "") + "://" + getSpincastConfig().getServerHost() + ":" +
               (isHttps ? getSpincastConfig().getHttpsServerPort() : getSpincastConfig().getHttpServerPort()) + pathOrUrl;
    }

    protected IWebsocketRequestBuilder websocket(String path) {
        return websocket(path, false, false);
    }

    protected IWebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl) {
        return websocket(pathOrUrl, isFullUrl, false);
    }

    protected IWebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        IWebsocketRequestBuilder builder = getHttpClient().websocket(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        return builder;
    }

    /**
     * Starts an Http Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IGetRequestBuilder GET(String path) {
        return GET(path, false, false);
    }

    /**
     * Starts an Http Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IGetRequestBuilder GET(String pathOrUrl, boolean isFullUrl) {
        return GET(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IGetRequestBuilder GET(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        IGetRequestBuilder client = getHttpClient().GET(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * By default, for the tests, by disable the
     * SSL certificate errors.
     */
    protected boolean isDisableSllCetificateErrors() {
        return true;
    }

    /**
     * Starts an Http Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IPostRequestBuilder POST(String path) {
        return POST(path, false, false);
    }

    /**
     * Starts an Http Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IPostRequestBuilder POST(String pathOrUrl, boolean isFullUrl) {
        return POST(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IPostRequestBuilder POST(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IPostRequestBuilder client = getHttpClient().POST(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IPutRequestBuilder PUT(String path) {
        return PUT(path, false, false);
    }

    /**
     * Starts an Http Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IPutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl) {
        return PUT(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IPutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IPutRequestBuilder client = getHttpClient().PUT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IDeleteRequestBuilder DELETE(String path) {
        return DELETE(path, false, false);
    }

    /**
     * Starts an Http Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IDeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl) {
        return DELETE(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IDeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IDeleteRequestBuilder client = getHttpClient().DELETE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IOptionsRequestBuilder OPTIONS(String path) {
        return OPTIONS(path, false, false);
    }

    /**
     * Starts an Http Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IOptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl) {
        return OPTIONS(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IOptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IOptionsRequestBuilder client = getHttpClient().OPTIONS(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected ITraceRequestBuilder TRACE(String path) {
        return TRACE(path, false, false);
    }

    /**
     * Starts an Http Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected ITraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl) {
        return TRACE(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected ITraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        ITraceRequestBuilder client = getHttpClient().TRACE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IConnectRequestBuilder CONNECT(String path) {
        return CONNECT(path, false, false);
    }

    /**
     * Starts an Http Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl) {
        return CONNECT(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IConnectRequestBuilder client = getHttpClient().CONNECT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IPatchRequestBuilder PATCH(String path) {
        return PATCH(path, false, false);
    }

    /**
     * Starts an Http Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IPatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl) {
        return PATCH(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IPatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IPatchRequestBuilder client = getHttpClient().PATCH(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Http Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected IHeadRequestBuilder HEAD(String path) {
        return HEAD(path, false, false);
    }

    /**
     * Starts an Http Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected IHeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl) {
        return HEAD(pathOrUrl, isFullUrl, false);
    }

    /**
     * Starts an Http Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected IHeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        IHeadRequestBuilder client = getHttpClient().HEAD(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if(isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

}

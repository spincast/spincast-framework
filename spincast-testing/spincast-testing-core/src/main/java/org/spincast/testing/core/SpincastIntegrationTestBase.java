package org.spincast.testing.core;

import java.lang.reflect.Type;

import org.junit.Before;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.routing.IRouter;
import org.spincast.core.server.IServer;
import org.spincast.plugins.httpclient.IHttpClient;
import org.spincast.plugins.httpclient.SpincastHttpClientPluginGuiceModule;
import org.spincast.plugins.httpclient.builders.IConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.IDeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.IGetRequestBuilder;
import org.spincast.plugins.httpclient.builders.IHeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.IOptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPostRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPutRequestBuilder;
import org.spincast.plugins.httpclient.builders.ITraceRequestBuilder;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * Base class for Spincast integration tests. 
 * 
 * <p>
 * This requires a "IServer" to be bound in the Guice 
 * context : it will be stopped after a test class is ran.
 * </p>
 * 
 * It doesn't start the server automatically because most
 * integration tests will have the server started an application 
 * (a <code>main(...)</code> method).
 * 
 * All client data (such as cookies) are cleared before each test.
 * 
 */
public abstract class SpincastIntegrationTestBase<R extends IRequestContext<?>> extends SpincastGuiceBasedTestBase {

    @Inject
    private IHttpClient httpClient;

    @Inject
    private IServer server;

    @Inject
    private IRouter<R> router;

    @Inject
    private ICookieFactory cookieFactory;

    @Override
    protected Injector extendGuiceInjector(final Injector baseInjector) {

        //==========================================
        // We extend the base Guice injector to add
        // the Spincast Http Client module.
        //==========================================
        Injector childInjector = baseInjector.createChildInjector(new AbstractModule() {

            @Override
            protected void configure() {

                Type requestContextType = baseInjector.getInstance(Key.get(Type.class, RequestContextType.class));

                //==========================================
                // Install the Spincast Http Client Module,
                // if not already bound.
                //==========================================
                Binding<IHttpClient> existingBinding =
                        baseInjector.getExistingBinding(Key.get(IHttpClient.class));
                if(existingBinding == null) {
                    install(new SpincastHttpClientPluginGuiceModule(requestContextType));
                }
            }
        });

        return childInjector;
    }

    @Override
    public void afterClass() {
        super.afterClass();

        stopServer();
    }

    @Before
    public void before() {
        // nothing for now here  
    }

    protected void stopServer() {
        if(getServer() != null) {
            getServer().stop();
        }
    }

    protected IHttpClient getHttpClient() {
        return this.httpClient;
    }

    protected IRouter<R> getRouter() {
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
     * @param https if <true>true</true>, "https:" will be used
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
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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
     * @param path the relative path to be appended to the
     * base test URL.
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
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param https if <true>true</true>, "https:" will be used
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

package org.spincast.testing.core;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;
import org.spincast.plugins.httpclient.websocket.HttpClient;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketPlugin;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;

import com.google.inject.Inject;

/**
 * Base class for integration test classes that need 
 * the HTTP/WebSocket server to be started.
 * 
 * <p>
 * This requires a "Server" to be bound in the Guice 
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
public abstract class IntegrationTestBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                         extends SpincastTestBase {

    @Inject
    private HttpClient httpClient;

    @Inject
    private Server server;

    @Inject
    private Router<R, W> router;

    @Inject
    private CookieFactory cookieFactory;

    @Override
    public void afterClass() {
        super.afterClass();
        stopServer();
    }

    protected void stopServer() {
        if (getServer() != null) {
            //==========================================
            // No "closing" message to the peers by default
            // for the tests.
            //==========================================
            getServer().stop(false);
        }
    }

    /**
     * The extra plugins added by the Guice Tweaker.
     * 
     * For integration testing, we need the Spincast HTTP 
     * Client plugin!
     */
    @Override
    protected List<SpincastPlugin> getGuiceTweakerExtraPlugins() {

        List<SpincastPlugin> plugins = super.getGuiceTweakerExtraPlugins();

        plugins.add(new SpincastHttpClientWithWebsocketPlugin());

        return plugins;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected Server getServer() {
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

        if (isFullUrl) {
            return pathOrUrl;
        }

        if (StringUtils.isBlank(pathOrUrl)) {
            pathOrUrl = "/";
        } else if (!pathOrUrl.startsWith("/")) {
            pathOrUrl = "/" + pathOrUrl;
        }

        return "http" + (isHttps ? "s" : "") + "://" + getSpincastConfig().getServerHost() + ":" +
               (isHttps ? getSpincastConfig().getHttpsServerPort() : getSpincastConfig().getHttpServerPort()) + pathOrUrl;
    }

    protected WebsocketRequestBuilder websocket(String path) {
        return websocket(path, false, false);
    }

    protected WebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl) {
        return websocket(pathOrUrl, isFullUrl, false);
    }

    protected WebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        WebsocketRequestBuilder builder = getHttpClient().websocket(createTestUrl(pathOrUrl, isFullUrl, isHttps));
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
    protected GetRequestBuilder GET(String path) {
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
    protected GetRequestBuilder GET(String pathOrUrl, boolean isFullUrl) {
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
    protected GetRequestBuilder GET(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        GetRequestBuilder client = getHttpClient().GET(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected PostRequestBuilder POST(String path) {
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
    protected PostRequestBuilder POST(String pathOrUrl, boolean isFullUrl) {
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
    protected PostRequestBuilder POST(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PostRequestBuilder client = getHttpClient().POST(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected PutRequestBuilder PUT(String path) {
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
    protected PutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl) {
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
    protected PutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PutRequestBuilder client = getHttpClient().PUT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected DeleteRequestBuilder DELETE(String path) {
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
    protected DeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl) {
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
    protected DeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        DeleteRequestBuilder client = getHttpClient().DELETE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected OptionsRequestBuilder OPTIONS(String path) {
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
    protected OptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl) {
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
    protected OptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        OptionsRequestBuilder client = getHttpClient().OPTIONS(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected TraceRequestBuilder TRACE(String path) {
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
    protected TraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl) {
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
    protected TraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        TraceRequestBuilder client = getHttpClient().TRACE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected ConnectRequestBuilder CONNECT(String path) {
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
    protected ConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl) {
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
    protected ConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        ConnectRequestBuilder client = getHttpClient().CONNECT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected PatchRequestBuilder PATCH(String path) {
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
    protected PatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl) {
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
    protected PatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PatchRequestBuilder client = getHttpClient().PATCH(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
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
    protected HeadRequestBuilder HEAD(String path) {
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
    protected HeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl) {
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
    protected HeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        HeadRequestBuilder client = getHttpClient().HEAD(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Format a date so it can be used in a HTTP header.
     */
    protected String formatDate(Date date) {
        Objects.requireNonNull(date, "The date can't be NULL");
        return DateUtils.formatDate(date);
    }

    /**
     * Parse a date from a HTTP header to a Date object.
     */
    protected Date parseDate(String dateHeaderValue) {
        Objects.requireNonNull(dateHeaderValue, "The dateHeaderValue can't be NULL");
        return DateUtils.parseDate(dateHeaderValue);
    }

}

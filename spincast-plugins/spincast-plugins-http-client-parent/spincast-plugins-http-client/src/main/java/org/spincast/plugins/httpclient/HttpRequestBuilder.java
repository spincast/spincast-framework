package org.spincast.plugins.httpclient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.spincast.core.cookies.Cookie;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.shaded.org.apache.http.impl.client.HttpClientBuilder;

/**
 * Base for all Http/Websocket request builders.
 */
public interface HttpRequestBuilder<T extends HttpRequestBuilder<?>> {

    /**
     * Adds a value to the specified header. Existing values will
     * be kept.
     */
    public T addHeaderValue(String key, String value);

    /**
     * Adds some values to the specified header. Existing values will
     * be kept.
     */
    public T addHeaderValues(String key, List<String> values);

    /**
     * Sets the headers. Existing headers will be overwritten.
     */
    public T setHeaders(Map<String, List<String>> headers);

    /**
     * Sets the values of the specified header. Existing values 
     * of this header will be overwritten.
     */
    public T setHeaderValues(String key, List<String> values);

    /**
     * Adds an <code>Accept</code> head for <code>Json</code>.
     */
    public T addJsonAcceptHeader();

    /**
     * Adds an <code>Accept</code> head for <code>XML</code>.
     */
    public T addXMLAcceptHeader();

    /**
     * Adds an <code>Accept</code> head for <code>HTML</code>.
     */
    public T addHTMLAcceptHeader();

    /**
     * Adds an <code>Accept</code> head for <code>plain text</code>.
     */
    public T addPlainTextAcceptHeader();

    /**
     * Adds a cookie.
     */
    public T addCookie(String name, String value);

    /**
     * Adds a cookie.
     */
    public T addCookie(Cookie cookie);

    /**
     * Adds some cookies.
     */
    public T addCookies(Collection<Cookie> cookies);

    /**
     * Sets a custom <code>RequestConfig</code> to use. If not provided,
     * a default one will be used.
     */
    public T setRequestConfig(RequestConfig requestConfig);

    /**
     * Sets a specific <code>HttpClientBuilder</code> to use. If not provided,
     * a default one will be used.
     */
    public T setHttpClientBuilder(HttpClientBuilder httpClientBuilder);

    /**
     * Disables <code>SSL</code> certificates errors (such as self-signed 
     * certificate errors).
     * 
     * <code>SSL</code> certificate errors are not disabled by default.
     * 
     * Be sure you know what you are doing if you disable this! It may lead to
     * some security concerns.
     */
    public T disableSslCertificateErrors();

    /**
     * Set HTTP authentication credentials.
     */
    public T setHttpAuthCredentials(String username, String password);

    /**
     * Sends the request and gets the HTTP response.
     */
    public HttpResponse send();

}
package org.spincast.plugins.httpclient.builders;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.httpclient.HttpRequestBuilder;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.Header;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.auth.AuthScope;
import org.spincast.shaded.org.apache.http.auth.UsernamePasswordCredentials;
import org.spincast.shaded.org.apache.http.client.CookieStore;
import org.spincast.shaded.org.apache.http.client.CredentialsProvider;
import org.spincast.shaded.org.apache.http.client.HttpClient;
import org.spincast.shaded.org.apache.http.client.config.CookieSpecs;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.shaded.org.apache.http.client.entity.DecompressingEntity;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.spincast.shaded.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.spincast.shaded.org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.spincast.shaded.org.apache.http.impl.client.BasicCookieStore;
import org.spincast.shaded.org.apache.http.impl.client.BasicCredentialsProvider;
import org.spincast.shaded.org.apache.http.impl.client.HttpClientBuilder;
import org.spincast.shaded.org.apache.http.impl.cookie.BasicClientCookie;
import org.spincast.shaded.org.apache.http.protocol.HttpRequestExecutor;
import org.spincast.shaded.org.apache.http.ssl.SSLContexts;
import org.spincast.shaded.org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;

/**
 * Http client builders base class.
 */
public abstract class HttpRequestBuilderBase<T extends HttpRequestBuilder<?>> implements HttpRequestBuilder<T> {

    private final CookieFactory cookieFactory;
    private final HttpResponseFactory spincastHttpResponseFactory;
    private final SpincastHttpClientUtils spincastHttpClientUtils;
    private final SpincastHttpClientConfig spincastHttpClientConfig;

    private String url;
    private HttpClient httpClient;
    private Map<String, List<String>> headers;
    private RequestConfig requestConfig;

    private CookieStore cookieStore;
    private HttpClientBuilder httpClientBuilder;
    private boolean disableSslCertificateErrors = false;
    private boolean disableRedirectHandling = false;

    private String httpAuthUsername;
    private String httpAuthPassword;

    /**
     * Constructor
     */
    public HttpRequestBuilderBase(String url,
                                  CookieFactory cookieFactory,
                                  HttpResponseFactory spincastHttpResponseFactory,
                                  SpincastHttpClientUtils spincastHttpClientUtils,
                                  SpincastHttpClientConfig spincastHttpClientConfig) {
        this.url = url;
        this.cookieFactory = cookieFactory;
        this.spincastHttpResponseFactory = spincastHttpResponseFactory;
        this.spincastHttpClientUtils = spincastHttpClientUtils;
        this.spincastHttpClientConfig = spincastHttpClientConfig;
    }

    protected HttpResponseFactory getSpincastHttpResponseFactory() {
        return this.spincastHttpResponseFactory;
    }

    protected boolean isDisableSslCertificateErrors() {
        return this.disableSslCertificateErrors;
    }

    protected boolean isDisableRedirectHandling() {
        return this.disableRedirectHandling;
    }

    protected SpincastHttpClientUtils getSpincastHttpClientUtils() {
        return this.spincastHttpClientUtils;
    }

    protected SpincastHttpClientConfig getSpincastHttpClientConfig() {
        return this.spincastHttpClientConfig;
    }

    /**
     * Creates the HTTPClient with a cookies store.
     */
    protected HttpClient createHttpClient() {
        HttpClientBuilder httpClientBuilder = getHttpClientBuilder();
        return httpClientBuilder.build();
    }

    protected HttpClientBuilder getHttpClientBuilder() {

        //==========================================
        // No specific HttpClientBuilder to use?
        // We create a default one.
        //==========================================
        if (this.httpClientBuilder == null) {
            this.httpClientBuilder = createHttpClientBuilder();
        }

        this.httpClientBuilder.setRequestExecutor(getHttpRequestExecutor());

        //==========================================
        // Add the cookie Store
        //==========================================
        this.httpClientBuilder.setDefaultCookieStore(getCookieStore());

        //==========================================
        // Disable SSL certificate errors?
        //==========================================
        if (isDisableSslCertificateErrors()) {
            try {
                SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
                SSLConnectionSocketFactory sslsf =
                        new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, new NoopHostnameVerifier());
                this.httpClientBuilder.setSSLSocketFactory(sslsf);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        //==========================================
        // Disable automatic redirects?
        //==========================================
        if (isDisableRedirectHandling()) {
            this.httpClientBuilder.disableRedirectHandling();
        }

        //==========================================
        // Http authentication credentials?
        //==========================================
        if (!StringUtils.isBlank(getHttpAuthUsername())) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                                               new UsernamePasswordCredentials(getHttpAuthUsername(), getHttpAuthPassword()));

            this.httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        return this.httpClientBuilder;
    }

    protected HttpRequestExecutor getHttpRequestExecutor() {

        //==========================================
        // We use a custom HttpRequestExecutor to manage
        // Websocket upgrade responses.
        //==========================================
        return new SpincastHttpRequestExecutor();
    }

    protected HttpClientBuilder createHttpClientBuilder() {
        return HttpClientBuilder.create();
    }

    @Override
    public T setHttpClientBuilder(HttpClientBuilder httpClientBuilder) {

        this.httpClientBuilder = httpClientBuilder;

        //==========================================
        // Some magic is required here, sadly... :-(
        //
        // We want to know if a Cookie Store has been
        // defined on the custom HttpClientBuilder, so we 
        // do not overwrite it with a default one.
        //
        // HttpClientBuilder#setDefaultCookieStore(...) is final so we
        // can't override it and there is no getCookieStore() method.
        //
        // At least, we have a test to make sure this reflection
        // works: HttpClientTest#customCookieStore()
        //==========================================
        if (httpClientBuilder != null) {
            try {

                Field cookieStoreField = httpClientBuilder.getClass().getDeclaredField("cookieStore");
                cookieStoreField.setAccessible(true);

                CookieStore cookieStore = (CookieStore)cookieStoreField.get(httpClientBuilder);
                if (cookieStore != null) {
                    setCookieStore(cookieStore);
                }

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T disableSslCertificateErrors() {

        this.disableSslCertificateErrors = true;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T disableRedirectHandling() {
        this.disableRedirectHandling = true;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    protected void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    protected HttpClient getHttpClient() {
        if (this.httpClient == null) {
            this.httpClient = createHttpClient();
        }
        return this.httpClient;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected String getUrl() {
        return this.url;
    }

    protected CookieStore getCookieStore() {

        //==========================================
        // If no custom Cookie Store has been specified,
        // we use a default one.
        //==========================================
        if (this.cookieStore == null) {
            this.cookieStore = new BasicCookieStore();
        }
        return this.cookieStore;
    }

    @Override
    public T setCookie(String name, String value) {
        return setCookie(name, value, true);
    }

    @Override
    public T setCookie(String name, String value, boolean secure) {
        Objects.requireNonNull(name, "The name can't be NULL");

        Cookie cookie = getCookieFactory().createCookie(name, value);
        cookie.setSecure(secure);
        return setCookie(cookie);
    }

    @Override
    public T setCookie(Cookie cookie) {
        Objects.requireNonNull(cookie, "The cookie can't be NULL");

        return setCookies(Lists.newArrayList(cookie));
    }

    @Override
    public T setCookies(Collection<Cookie> cookies) {

        if (cookies != null) {

            for (Cookie cookie : cookies) {
                if (cookie != null) {
                    org.spincast.shaded.org.apache.http.cookie.Cookie apacheCookie = convertToApacheCookie(cookie);
                    getCookieStore().addCookie(apacheCookie);
                }
            }
        }

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    /**
     * Converts a Spincast cookie to an Apache one.
     */
    protected org.spincast.shaded.org.apache.http.cookie.Cookie convertToApacheCookie(Cookie cookie) {

        if (cookie == null) {
            return null;
        }

        BasicClientCookie apacheCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
        apacheCookie.setDomain(cookie.getDomain());
        apacheCookie.setPath(cookie.getPath());
        apacheCookie.setVersion(cookie.getVersion());
        apacheCookie.setSecure(cookie.isSecure());
        apacheCookie.setExpiryDate(cookie.getExpires());

        return apacheCookie;
    }

    @Override
    public T setHeaders(Map<String, List<String>> headers) {

        this.headers = headers;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T addHeaderValue(String key, String value) {
        return addHeaderValues(key, Lists.newArrayList(value));
    }

    @Override
    public T addHeaderValues(String key, List<String> values) {

        Objects.requireNonNull(key, "The key can't be NULL");

        List<String> currentValues = getHeaders().get(key);
        if (currentValues == null) {
            currentValues = new ArrayList<String>();
            getHeaders().put(key, currentValues);
        }
        currentValues.addAll(values);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setHeaderValues(String key, List<String> values) {

        Objects.requireNonNull(key, "The key can't be NULL");

        getHeaders().put(key, values);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    protected Map<String, List<String>> getHeaders() {
        if (this.headers == null) {
            this.headers = new HashMap<String, List<String>>();
        }
        return this.headers;
    }

    @Override
    public T addJsonAcceptHeader() {
        return addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.JSON.getMainVariation());
    }

    @Override
    public T addXMLAcceptHeader() {
        return addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation());
    }

    @Override
    public T addHTMLAcceptHeader() {
        return addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.HTML.getMainVariation());
    }

    @Override
    public T addPlainTextAcceptHeader() {
        return addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.TEXT.getMainVariation());
    }

    @Override
    public T setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    protected RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    protected String getCookieEncoding() {
        return "UTF-8";
    }

    @Override
    public T setHttpAuthCredentials(String username, String password) {

        this.httpAuthUsername = username;
        this.httpAuthPassword = password;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    protected String getHttpAuthUsername() {
        return this.httpAuthUsername;
    }

    protected String getHttpAuthPassword() {
        return this.httpAuthPassword;
    }

    /**
     * @return the raw, unparsed HttpClient's HttpResponse
     */
    protected org.spincast.shaded.org.apache.http.HttpResponse sendGetRawResponse() {
        try {

            RequestConfig requestConfig = getRequestConfig();
            if (requestConfig == null) {
                requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                                             .setCookieSpec(CookieSpecs.STANDARD)
                                             .build();
            }

            String url = getUrl();

            HttpRequestBase request = createMethodSpecificHttpRequest(url);

            if (requestConfig != null) {
                request.setConfig(requestConfig);
            }

            for (Entry<String, List<String>> entry : getHeaders().entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    request.addHeader(key, value);
                }
            }

            return getHttpClient().execute(request);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * @return A object containg the parsed information of the response.
     */
    @Override
    public HttpResponse send() {

        try {
            //==========================================
            // Sends the request and get the raw response.
            //==========================================
            org.spincast.shaded.org.apache.http.HttpResponse response = sendGetRawResponse();

            //==========================================
            // Then parses the respnse and create the 
            // SpincastTestHttpResponse to return.
            //==========================================
            try {
                byte[] content = null;
                int status = response.getStatusLine().getStatusCode();

                String contentType = null;
                Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
                if (contentTypeHeader != null) {
                    contentType = contentTypeHeader.getValue();
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    content = EntityUtils.toByteArray(entity);
                }

                boolean wasZipped = (entity instanceof DecompressingEntity);

                Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
                Header[] allHeaders = response.getAllHeaders();
                for (Header header : allHeaders) {

                    List<String> vals = responseHeaders.get(header.getName());
                    if (vals == null) {
                        vals = new ArrayList<String>();
                        responseHeaders.put(header.getName(), vals);
                    }
                    vals.add(header.getValue());
                }

                Map<String, Cookie> cookies = new HashMap<String, Cookie>();
                CookieStore cookieStore = getCookieStore();
                if (cookieStore != null) {
                    List<org.spincast.shaded.org.apache.http.cookie.Cookie> responseCookies = cookieStore.getCookies();
                    for (org.spincast.shaded.org.apache.http.cookie.Cookie responseCookie : responseCookies) {

                        String name = responseCookie.getName();
                        String value = responseCookie.getValue();
                        if (value != null) {
                            try {
                                // Try to set this as the cookie name or value without
                                // this encoding : "bœuf".
                                name = URLDecoder.decode(name, getCookieEncoding());
                                value = URLDecoder.decode(value, getCookieEncoding());

                            } catch (Exception ex) {
                                throw SpincastStatics.runtimize(ex);
                            }
                        }

                        Cookie cookie = getCookieFactory().createCookie(name,
                                                                        value,
                                                                        responseCookie.getPath(),
                                                                        responseCookie.getDomain(),
                                                                        responseCookie.getExpiryDate(),
                                                                        responseCookie.isSecure(),
                                                                        false, // "httpOnly" not supported in current Apache HttpClient version
                                                                        null, // "sameSite" not supported in current Apache HttpClient version
                                                                        !responseCookie.isPersistent(),
                                                                        responseCookie.getVersion());
                        cookies.put(cookie.getName(), cookie);
                    }
                }

                return getSpincastHttpResponseFactory().create(status, contentType, content, responseHeaders, cookies, wasZipped);

            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Creates the HttpRequestBase depending on the HTTP method.
     */
    protected abstract HttpRequestBase createMethodSpecificHttpRequest(String url);

}

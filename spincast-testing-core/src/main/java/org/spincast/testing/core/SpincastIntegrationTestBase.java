package org.spincast.testing.core;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.junit.Before;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IRouter;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.Header;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpResponse;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.CookieStore;
import org.spincast.shaded.org.apache.http.client.HttpClient;
import org.spincast.shaded.org.apache.http.client.config.CookieSpecs;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.shaded.org.apache.http.client.entity.UrlEncodedFormEntity;
import org.spincast.shaded.org.apache.http.client.methods.HttpDelete;
import org.spincast.shaded.org.apache.http.client.methods.HttpGet;
import org.spincast.shaded.org.apache.http.client.methods.HttpHead;
import org.spincast.shaded.org.apache.http.client.methods.HttpOptions;
import org.spincast.shaded.org.apache.http.client.methods.HttpPost;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpTrace;
import org.spincast.shaded.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.spincast.shaded.org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.spincast.shaded.org.apache.http.cookie.Cookie;
import org.spincast.shaded.org.apache.http.entity.StringEntity;
import org.spincast.shaded.org.apache.http.impl.client.BasicCookieStore;
import org.spincast.shaded.org.apache.http.impl.client.HttpClientBuilder;
import org.spincast.shaded.org.apache.http.ssl.SSLContexts;
import org.spincast.shaded.org.apache.http.util.EntityUtils;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

import com.google.inject.Inject;

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
    private IServer server;

    @Inject
    private IRouter<R> router;

    @Inject
    private ICookieFactory cookieFactory;

    private CookieStore cookieStore;

    @Override
    public void afterClass() {
        super.afterClass();

        stopServer();
    }

    /**
     * Ran before each test.
     */
    @Before
    public void before() {
        clearHttpClientData();
    }

    protected void clearHttpClientData() {
        getCookieStore().clear();
    }

    protected void stopServer() {
        if(getServer() != null) {
            getServer().stop();
        }
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

    protected CookieStore getCookieStore() {
        if(this.cookieStore == null) {
            this.cookieStore = new BasicCookieStore();
        }
        return this.cookieStore;
    }

    protected String getCookieEncoding() {
        return "UTF-8";
    }

    /**
     * Creates the HTTPClient with a cookies store.
     */
    protected HttpClient getHttpClient() {

        try {

            //==========================================
            // Use out cookies store
            //==========================================
            HttpClientBuilder httpClientBuilder =
                    HttpClientBuilder.create().setDefaultCookieStore(getCookieStore());

            //==========================================
            // Accept self-signed certificats
            //==========================================
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, new NoopHostnameVerifier());
            httpClientBuilder.setSSLSocketFactory(sslsf);

            return httpClientBuilder.build();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Creates an URL to test the HTTP server.
     */
    protected String createTestUrl(String path) {
        return createTestUrl(path, false);
    }

    /**
     * Creates an URL to test the HTTP server.
     */
    protected String createTestUrl(String path, boolean https) {

        if(StringUtils.isBlank(path)) {
            path = "/";
        } else if(!path.startsWith("/")) {
            path = "/" + path;
        }

        return "http" + (https ? "s" : "") + "://" + getSpincastConfig().getServerHost() + ":" +
               (https ? getSpincastConfig().getHttpsServerPort() : getSpincastConfig().getHttpServerPort()) + path;
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path) throws Exception {
        return get(path, null, null);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, boolean https) throws Exception {
        return get(path, null, null, https);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, Map<String, String> headers) throws Exception {
        return get(path, headers, null);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, Map<String, String> headers, boolean https) throws Exception {
        return get(path, headers, null, https);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, RequestConfig requestConfig) throws Exception {
        return get(path, null, requestConfig);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, RequestConfig requestConfig, boolean https) throws Exception {
        return get(path, null, requestConfig, https);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, Map<String, String> headers,
                                           RequestConfig requestConfig) throws Exception {
        return get(path, headers, requestConfig, false);
    }

    /**
     * Launches a GET request, using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse get(String path, Map<String, String> headers,
                                           RequestConfig requestConfig, boolean https) throws Exception {
        return getWithUrl(createTestUrl(path, https), headers, requestConfig);
    }

    /**
     * Launches a GET request, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse getWithUrl(String url) throws Exception {
        return getWithUrl(url, null, null);
    }

    /**
     * Launches a GET request, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse getWithUrl(String url, RequestConfig requestConfig) throws Exception {
        return getWithUrl(url, null, requestConfig);
    }

    /**
     * Launches a GET request, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse getWithUrl(String url, Map<String, String> headers,
                                                  RequestConfig requestConfig) throws Exception {
        return methodWithUrl(HttpMethod.GET, url, headers, requestConfig);
    }

    /**
     * Launches a GET request, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse getWithUrl(String url, Map<String, String> headers) throws Exception {
        return methodWithUrl(HttpMethod.GET, url, headers, null);
    }

    /**
     * Launches a GET request, using the given full URL and returns the
     * raw, unparsed HttpClient's HttpResponse.
     * 
     */
    protected HttpResponse getRawResponse(String url) {
        return getRawResponse(url, null, null);
    }

    /**
     * Launches a GET request, using the given full URL and returns the
     * raw, unparsed HttpClient's HttpResponse.
     * 
     */
    protected HttpResponse getRawResponse(String url, Map<String, String> headers) {
        return getRawResponse(url, headers, null);
    }

    /**
     * Launches a GET request, using the given full URL and returns the
     * raw, unparsed HttpClient's HttpResponse.
     * 
     */
    protected HttpResponse getRawResponse(String url, RequestConfig requestConfig) {
        return getRawResponse(url, null, requestConfig);
    }

    /**
     * Launches a GET request, using the given full URL and returns the
     * raw, unparsed HttpClient's HttpResponse.
     * 
     */
    protected HttpResponse getRawResponse(String url, Map<String, String> headers, RequestConfig requestConfig) {
        return requestRaw(HttpMethod.GET, url, headers, requestConfig, null, null, null);
    }

    /**
     * Launches a POST request using the given path (will be appended to the base test URL).
     * 
     */
    protected SpincastTestHttpResponse postWithParams(String path, List<NameValuePair> params) throws Exception {
        return methodWithUrl(HttpMethod.POST, createTestUrl(path), null, null, params);
    }

    /**
     * Launches a POST request using the given path (will be appended to the base test URL)
     * and the specified headers.
     * 
     */
    protected SpincastTestHttpResponse postWithHeaders(String path, Map<String, String> headers) throws Exception {
        return methodWithUrl(HttpMethod.POST, createTestUrl(path), headers, null, null);
    }

    /**
     * Launches a POST request using the given path (will be appended to the base test URL) and
     * by sending the "str" String as the entity with the specified "contentType" content-type. 
     * 
     */
    protected SpincastTestHttpResponse postStringEntity(String path, String str, String contentType) throws Exception {
        return request(HttpMethod.POST, createTestUrl(path), null, null, null, str, contentType);
    }

    /**
     * Launches a POST request using the given path (will be appended to the base test URL) and
     * by sending the "json" Json string and the Json content-type.
     * 
     */
    protected SpincastTestHttpResponse postJson(String path, String json) throws Exception {
        return request(HttpMethod.POST,
                       createTestUrl(path),
                       null,
                       null,
                       null,
                       json,
                       ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
    }

    /**
     * Launches a request of a custom HTTP method, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse methodWithUrl(HttpMethod method,
                                                     String url,
                                                     Map<String, String> headers,
                                                     RequestConfig requestConfig) throws Exception {
        return methodWithUrl(method, url, headers, requestConfig, null);
    }

    /**
     * Launches a request of a custom HTTP method, using the given full URL.
     * 
     */
    protected SpincastTestHttpResponse methodWithUrl(HttpMethod method,
                                                     String url,
                                                     Map<String, String> headers,
                                                     RequestConfig requestConfig,
                                                     List<NameValuePair> params) throws Exception {
        return request(method, url, headers, requestConfig, params, null, null);
    }

    /**
     * Launches a request of a custom HTTP method, using the given full URL.
     * 
     * @return A object containg tyhe parsed information of the response.
     * 
     */
    protected SpincastTestHttpResponse request(HttpMethod method,
                                               String url,
                                               Map<String, String> headers,
                                               RequestConfig requestConfig,
                                               List<NameValuePair> params,
                                               String payload,
                                               String contentTypeIn) throws Exception {

        //==========================================
        // Sends the request and get the raw response.
        //==========================================
        HttpResponse response = requestRaw(method,
                                           url,
                                           headers,
                                           requestConfig,
                                           params,
                                           payload,
                                           contentTypeIn);

        //==========================================
        // Then parses the respnse and create the 
        // SpincastTestHttpResponse to return.
        //==========================================
        try {
            String content = null;
            int status = response.getStatusLine().getStatusCode();

            String contentType = null;
            Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if(contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
            }

            HttpEntity entity = response.getEntity();
            if(entity != null) {
                content = EntityUtils.toString(entity, "UTF-8");
            }

            Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
            Header[] allHeaders = response.getAllHeaders();
            for(Header header : allHeaders) {

                List<String> vals = responseHeaders.get(header.getName());
                if(vals == null) {
                    vals = new ArrayList<String>();
                    responseHeaders.put(header.getName(), vals);
                }
                vals.add(header.getValue());
            }

            Map<String, ICookie> cookies = new HashMap<String, ICookie>();
            CookieStore cookieStore = getCookieStore();
            List<Cookie> responseCookies = cookieStore.getCookies();
            for(Cookie responseCookie : responseCookies) {

                String name = responseCookie.getName();
                String value = responseCookie.getValue();
                if(value != null) {
                    try {
                        // Try to set this as the cookie name or value without
                        // this encoding : "bœuf".
                        name = URLDecoder.decode(name, getCookieEncoding());
                        value = URLDecoder.decode(value, getCookieEncoding());

                    } catch(Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }

                ICookie cookie = getCookieFactory().createCookie(name,
                                                                 value,
                                                                 responseCookie.getPath(),
                                                                 responseCookie.getDomain(),
                                                                 responseCookie.getExpiryDate(),
                                                                 responseCookie.isSecure(),
                                                                 false, // "httpOnly" not supported in current HttpClient version!
                                                                 !responseCookie.isPersistent(),
                                                                 responseCookie.getVersion());
                cookies.put(cookie.getName(), cookie);
            }

            return new SpincastTestHttpResponse(status, contentType, content, responseHeaders, cookies);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    /**
     * Launches a request of a custom HTTP method, using the given full URL.
     * 
     * Don't forget to close the HttpResponse's inputstream when
     * you're done!
     * 
     * @return the raw, unparsed HttpClient's HttpResponse
     * 
     */
    protected HttpResponse requestRaw(HttpMethod method,
                                      String url,
                                      Map<String, String> headers,
                                      RequestConfig requestConfig,
                                      List<NameValuePair> params,
                                      String str,
                                      String contentType) {

        try {

            if(headers == null) {
                headers = new HashMap<String, String>();
            }

            if(requestConfig == null) {
                requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                                             .setCookieSpec(CookieSpecs.STANDARD)
                                             .build();
            }

            HttpRequestBase request;
            if(method == HttpMethod.GET) {
                request = new HttpGet(url);
            } else if(method == HttpMethod.POST) {
                request = new HttpPost(url);
                if(params != null) {
                    ((HttpPost)request).setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                } else if(str != null) {
                    StringEntity input = new StringEntity(str, "UTF-8");
                    input.setContentType(contentType);
                    ((HttpPost)request).setEntity(input);
                }
            } else if(method == HttpMethod.HEAD) {
                request = new HttpHead(url);
            } else if(method == HttpMethod.DELETE) {
                request = new HttpDelete(url);
            } else if(method == HttpMethod.OPTIONS) {
                request = new HttpOptions(url);
            } else if(method == HttpMethod.TRACE) {
                request = new HttpTrace(url);
            } else {
                throw new RuntimeException("Not implemented : " + method);
            }

            if(requestConfig != null) {
                request.setConfig(requestConfig);
            }

            for(Entry<String, String> header : headers.entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }

            return getHttpClient().execute(request);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}

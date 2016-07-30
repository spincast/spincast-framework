package org.spincast.core.filters;

import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;

/**
 * Some filters provided by Spincast.
 */
public interface ISpincastFilters<R extends IRequestContext<?>> {

    /**
     * Used by Spincast to save a "dynamic resource" once it is
     * generated.
     * 
     * @return true if the resource was succesfully saved on disk.
     */
    public boolean saveGeneratedResource(R context, String pathForGeneratedResource);

    /**
     * Adds some recommended security headers.
     * 
     * @see https://www.owasp.org/index.php/List_of_useful_HTTP_headers
     */
    public void addSecurityHeaders(R context);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * This overload allows all origins, allows cookies,
     * allows all HTTP methods, all headers will be allowed to
     * be sent by the browser, but no extra headers will
     * be available to be read by the browser.
     * </p>
     * 
     * <p>
     * By default, only those headers are available to be read :
     * <ul>
     * <li>Cache-Control</li>
     * <li>Content-Language</li>
     * <li>Content-Type</li>
     * <li>Expires</li>
     * <li>Last-Modified</li>
     * <li>Pragma</li>
     * </ul>
     * </p>
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * <p>
     * If you want to allow everything AND to add extra headers 
     * to be read, use :
     * </p>
     * <p>
     * cors(context, 
     *      Sets.newHashSet("*"), 
     *      Sets.newHashSet("extra-header-to-read1", "extra-header-to-read2"));
     */
    public void cors(R context);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * This overload allows allows cookies,
     * allows all HTTP methods for the
     * specified origins, all headers will be allowed to
     * be sent by the browser, but no extra headers will
     * be available to be read by the browser.
     * </p>
     * 
     * <p>
     * By default, only those headers are available to be read :
     * <ul>
     * <li>Cache-Control</li>
     * <li>Content-Language</li>
     * <li>Content-Type</li>
     * <li>Expires</li>
     * <li>Last-Modified</li>
     * <li>Pragma</li>
     * </ul>
     * </p>
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * If you want to allow everything for those origins AND to 
     * add extra headers to be read, use :
     * 
     * cors(context, 
     *      allowedOrigins, 
     *      Sets.newHashSet("extra-header-to-read1", "extra-header-to-read2"));
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     will be allowed!
     */
    public void cors(R context,
                     Set<String> allowedOrigins);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * This overload allows cookies, allows all HTTP methods and
     * all headers will be allowed to be sent by the browser, for the
     * specified origins.
     * </p>
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     will be allowed!
     *     
     * @param extraHeadersAllowedToBeRead The extra headers the browser will
     *     have permission to read from the response. 
     *     
     *     By default, only those headers are available :
     *     Cache-Control
     *     Content-Language
     *     Content-Type
     *     Expires
     *     Last-Modified
     *     Pragma
     */
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * This overload allows all cookies and all HTTP methods, for the specified origins. 
     * </p>
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     will be allowed!
     *     
     * @param extraHeadersAllowedToBeRead The extra headers the browser will
     *     have permission to read from the response. 
     *     By default, only those headers are exposed :
     *     Cache-Control
     *     Content-Language
     *     Content-Type
     *     Expires
     *     Last-Modified
     *     Pragma
     *     
     * @param extraHeadersAllowedToBeSent The extra headers the
     *     browser will be allowed to send with the actual 
     *     (post preflight) request. 
     *     
     * @param allowCookies Should cookies be allowed?
     */
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * This overload allows all HTTP methods, for the specified origins. 
     * </p>
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     will be allowed!
     *     
     * @param extraHeadersAllowedToBeRead The extra headers the browser will
     *     have permission to read from the response. 
     *     By default, only those headers are exposed :
     *     Cache-Control
     *     Content-Language
     *     Content-Type
     *     Expires
     *     Last-Modified
     *     Pragma
     *     
     * @param extraHeadersAllowedToBeSent The extra headers the
     *     browser will be allowed to send with the actual 
     *     (post preflight) request. 
     *     
     * @param allowCookies Should cookies be allowed?
     */
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * <p>
     * Send a Max-Age of 24h. The Max-Age is the
     * maximum number of seconds a
     * preflight response can be cached without querying
     * again. 
     * </p>
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     are allowed!
     *     
     * @param extraHeadersAllowedToBeRead The extra headers the browser will
     *     have permission to read from the response. 
     *     By default, only those headers are available :
     *     Cache-Control
     *     Content-Language
     *     Content-Type
     *     Expires
     *     Last-Modified
     *     Pragma
     * 
     * @param extraHeadersAllowedToBeSent The extra headers the
     *     browser will be allowed to send with the actual 
     *     (post preflight) request. If one of the headers is "*", 
     *     then all headers are allowed to be sent!
     *     
     * @param allowCookies Should cookies be allowed?
     *     
     * @param allowedMethods The HTTP method allowed. "OPTIONS" will
     *        be addded if not specified, as it should always be
     *        allowed.
     */
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods);

    /**
     * Cross-Origin Resource Sharing (Cors) handling.
     * 
     * @param allowedOrigins The origins to allow ("http://api.bob.com"
     *     for example). If one of the origins is "*", then all origins
     *     are allowed!
     *     
     * @param extraHeadersAllowedToBeRead The extra headers the browser will
     *     have permission to read from the response. 
     *     By default, only those headers are available :
     *     Cache-Control
     *     Content-Language
     *     Content-Type
     *     Expires
     *     Last-Modified
     *     Pragma
     * 
     * @param extraHeadersAllowedToBeSent The extra headers the
     *     browser will be allowed to send with the actual 
     *     (post preflight) request. If one of the headers is "*", 
     *     then all headers are allowed to be sent!
     *     
     * @param allowCookies Should cookies be allowed?
     *     
     * @param allowedMethods The HTTP method allowed. "OPTIONS" will
     *        be addded if not specified, as it should always be
     *        allowed.
     *        
     * @param maxAgeInSeconds The maximum number of seconds a
     *        preflight response can be cached without querying
     *        again. If &lt;= 0, the "Access-Control-Max-Age" header
     *        won't be sent.     
     */
    public void cors(R context,
                     Set<String> allowedOrigins,
                     Set<String> extraHeadersAllowedToBeRead,
                     Set<String> extraHeadersAllowedToBeSent,
                     boolean allowCookies,
                     Set<HttpMethod> allowedMethods,
                     int maxAgeInSeconds);

    public void cache(R context);

    public void cache(R context, int seconds);

    public void cache(R context, int seconds, boolean isPrivate);

    public void cache(R context, int seconds, boolean isPrivate, Integer cdnSeconds);

    /**
     * Adds some default variables so they are available
     * by default to the templating engine (in a request scope).
     */
    public void addDefaultGlobalTemplateVariables(R context);

}

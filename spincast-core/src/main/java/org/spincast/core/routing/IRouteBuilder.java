package org.spincast.core.routing;

import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.utils.ContentTypeDefaults;

/**
 * Builder to create a route.
 */
public interface IRouteBuilder<R extends IRequestContext<?>> {

    /**
     * An id that can be used to identify the route.
     * Must be unique.
     */
    public IRouteBuilder<R> id(String id);

    /**
     * The path of the route.
     */
    public IRouteBuilder<R> path(String path);

    /**
     * The position of the handler. If "0", the handler is considered
     * as the *main* handler. Only one main handler per request
     * is run (the first one found)! The main handler is usually where
     * the body of the response is created.
     * 
     * A route with a position less than "0" is considered as a "before" filter and
     * will be run before the main handler. A route with a position greater than
     * "0" is considered as an "after" filter and will be run after the
     * main handler. All the matching before and after filters are run,
     * from the lower position to the higher. If two filters have the
     * same position, they will be run in order they have been added to
     * the router.
     * 
     * More than one position is allowed.
     * 
     * If not specified, "0" is used.
     */
    public IRouteBuilder<R> pos(int position);

    /**
     * This route will be considered during a <code>Found</code> routing process.
     */
    public IRouteBuilder<R> found();

    /**
     * This route will be considered during a <code>Not Found</code> routing process.
     */
    public IRouteBuilder<R> notFound();

    /**
     * This route will be considered during an <code>Exception</code> routing process.
     */
    public IRouteBuilder<R> exception();

    /**
     * This route will be considered for all routing types.
     */
    public IRouteBuilder<R> allRoutingTypes();

    /**
     * Adds a "before" filter which will only be applied to this particular route.
     * If more than one filter is added, they will be run in order they have been 
     * added.
     */
    public IRouteBuilder<R> before(IHandler<R> beforeFilter);

    /**
     * Adds an "after" filter which will only be applied to this particular route.
     * If more than one filter is added, they will be run in order they have been 
     * added.
     */
    public IRouteBuilder<R> after(IHandler<R> afterFilter);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public IRouteBuilder<R> acceptAsString(String... acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public IRouteBuilder<R> acceptAsString(Set<String> acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public IRouteBuilder<R> accept(ContentTypeDefaults... acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public IRouteBuilder<R> accept(Set<ContentTypeDefaults> acceptedContentTypes);

    /**
     * Adds <code>application/html</code> as an accepted <code>Content-Type</code>. 
     */
    public IRouteBuilder<R> html();

    /**
     * Adds <code>application/json</code> as an accepted <code>Content-Type</code>. 
     */
    public IRouteBuilder<R> json();

    /**
     * Adds <code>application/xml</code> as an accepted <code>Content-Type</code>. 
     */
    public IRouteBuilder<R> xml();

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R) ISpincastFilters#cors(R context)
     */
    public IRouteBuilder<R> cors();

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                                                Set&lt;String&gt; allowedOrigins)
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead)
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins,
                                 Set<String> extraHeadersAllowedToBeRead);

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent) 
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins,
                                 Set<String> extraHeadersAllowedToBeRead,
                                 Set<String> extraHeadersAllowedToBeSent);

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies) 
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins,
                                 Set<String> extraHeadersAllowedToBeRead,
                                 Set<String> extraHeadersAllowedToBeSent,
                                 boolean allowCookies);

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods) 
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins,
                                 Set<String> extraHeadersAllowedToBeRead,
                                 Set<String> extraHeadersAllowedToBeSent,
                                 boolean allowCookies,
                                 Set<HttpMethod> allowedMethods);

    /**
     * Enables Cross-Origin Resource Sharing (Cors).
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean, Set, int) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          Set&lt;HttpMethod&gt; allowedMethods,
     *                                          int maxAgeInSeconds) 
     */
    public IRouteBuilder<R> cors(Set<String> allowedOrigins,
                                 Set<String> extraHeadersAllowedToBeRead,
                                 Set<String> extraHeadersAllowedToBeSent,
                                 boolean allowCookies,
                                 Set<HttpMethod> allowedMethods,
                                 int maxAgeInSeconds);

    /**
     * Addss <code>GET</code> as a supported HTTP method. 
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> GET();

    /**
     * Adds <code>POST</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> POST();

    /**
     * Adds <code>PUT</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> PUT();

    /**
     * Adds <code>DELETE</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> DELETE();

    /**
     * Adds <code>OPTIONS</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> OPTIONS();

    /**
     * Adds <code>TRACE</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> TRACE();

    /**
     * Adds <code>HEAD</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> HEAD();

    /**
     * Adds <code>PATCH</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public IRouteBuilder<R> PATCH();

    /**
     * Adds all HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. By calling this method, all
     * methods will now be suported.
     */
    public IRouteBuilder<R> ALL();

    /**
     * Adds the specified HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. Those new ones will simply be added.
     */
    public IRouteBuilder<R> SOME(Set<HttpMethod> httpMethods);

    /**
     * Adds the specified HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>IRouter</code> object, you already specified some 
     * supported HTTP methods. Those new ones will simply be added.
     */
    public IRouteBuilder<R> SOME(HttpMethod... httpMethods);

    /**
     * Creates the route and saves it to the router.
     * If the creation of the route was not started using
     * an <code>IRouter</code> object, an exception will be
     * thrown.
     */
    public void save(IHandler<R> mainHandler);

    /**
     * Creates and returns the route without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the route 
     * to the router at the end of the build process!
     */
    public IRoute<R> create(IHandler<R> mainHandler);

}

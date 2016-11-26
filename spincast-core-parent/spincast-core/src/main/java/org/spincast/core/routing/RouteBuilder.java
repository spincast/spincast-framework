package org.spincast.core.routing;

import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.utils.ContentTypeDefaults;

/**
 * Builder to create a route.
 */
public interface RouteBuilder<R extends RequestContext<?>> {

    /**
     * An id that can be used to identify the route.
     * Must be unique.
     */
    public RouteBuilder<R> id(String id);

    /**
     * The path of the route.
     */
    public RouteBuilder<R> path(String path);

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
    public RouteBuilder<R> pos(int position);

    /**
     * This route will be considered during a <code>Found</code> routing process.
     */
    public RouteBuilder<R> found();

    /**
     * This route will be considered during a <code>Not Found</code> routing process.
     */
    public RouteBuilder<R> notFound();

    /**
     * This route will be considered during an <code>Exception</code> routing process.
     */
    public RouteBuilder<R> exception();

    /**
     * This route will be considered for all routing types.
     */
    public RouteBuilder<R> allRoutingTypes();

    /**
     * Adds a "before" filter which will only be applied to this particular route.
     * If more than one filter is added, they will be run in order they have been 
     * added.
     */
    public RouteBuilder<R> before(Handler<R> beforeFilter);

    /**
     * Adds an "after" filter which will only be applied to this particular route.
     * If more than one filter is added, they will be run in order they have been 
     * added.
     */
    public RouteBuilder<R> after(Handler<R> afterFilter);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public RouteBuilder<R> acceptAsString(String... acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public RouteBuilder<R> acceptAsString(Set<String> acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public RouteBuilder<R> accept(ContentTypeDefaults... acceptedContentTypes);

    /**
     * Sets the accepted <code>Content-Types</code>. 
     * This route will only be considered for requests specifying those
     * <code>Content-Types</code> as being accepted 
     * (using the <code>Accept</code> header).
     */
    public RouteBuilder<R> accept(Set<ContentTypeDefaults> acceptedContentTypes);

    /**
     * Adds <code>application/html</code> as an accepted <code>Content-Type</code>. 
     */
    public RouteBuilder<R> html();

    /**
     * Adds <code>application/json</code> as an accepted <code>Content-Type</code>. 
     */
    public RouteBuilder<R> json();

    /**
     * Adds <code>application/xml</code> as an accepted <code>Content-Type</code>. 
     */
    public RouteBuilder<R> xml();

    /**
     * Addss <code>GET</code> as a supported HTTP method. 
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> GET();

    /**
     * Adds <code>POST</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> POST();

    /**
     * Adds <code>PUT</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> PUT();

    /**
     * Adds <code>DELETE</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> DELETE();

    /**
     * Adds <code>OPTIONS</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> OPTIONS();

    /**
     * Adds <code>TRACE</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> TRACE();

    /**
     * Adds <code>HEAD</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> HEAD();

    /**
     * Adds <code>PATCH</code> as a supported HTTP method.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. This one will simply be added.
     */
    public RouteBuilder<R> PATCH();

    /**
     * Adds all HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. By calling this method, all
     * methods will now be suported.
     */
    public RouteBuilder<R> ALL();

    /**
     * Adds the specified HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. Those new ones will simply be added.
     */
    public RouteBuilder<R> SOME(Set<HttpMethod> httpMethods);

    /**
     * Adds the specified HTTP methods as being supported.
     * 
     * If you started the creation of the route from an 
     * <code>Router</code> object, you already specified some 
     * supported HTTP methods. Those new ones will simply be added.
     */
    public RouteBuilder<R> SOME(HttpMethod... httpMethods);

    /**
     * Creates the route and saves it to the router.
     * If the creation of the route was not started using
     * an <code>Router</code> object, an exception will be
     * thrown.
     */
    public void save(Handler<R> mainHandler);

    /**
     * Creates and returns the route without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the route 
     * to the router at the end of the build process!
     */
    public Route<R> create(Handler<R> mainHandler);

    /**
     * Automatically adds "no-cache" headers to the response.
     */
    public RouteBuilder<R> noCache();

    /**
     * Adds cache headers.
     * <p>
     * Uses the default cache configurations, provided
     * by {@link org.spincast.core.config.SpincastConfig SpincastConfig}
     * </p>
     */
    public RouteBuilder<R> cache();

    /**
     * Adds public cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     */
    public RouteBuilder<R> cache(int seconds);

    /**
     * Adds cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     * 
     * @param isPrivate should the cache be private?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     */
    public RouteBuilder<R> cache(int seconds, boolean isPrivate);

    /**
     * Adds cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     * 
     * @param isPrivate should the cache be private?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     * 
     * @param secondsCdn The number of seconds the resource associated with
     * this route should be cached by a CDN/proxy. If <code>null</code>, it
     * won't be used.
     */
    public RouteBuilder<R> cache(int seconds, boolean isPrivate, Integer secondsCdn);

    /**
     * Skip a "before" and "after" filter for this route.
     * <p>
     * This is useful when you set a global filter but want to skip
     * it one a specific route only.
     * </p>
     */
    public RouteBuilder<R> skip(String filterId);

}
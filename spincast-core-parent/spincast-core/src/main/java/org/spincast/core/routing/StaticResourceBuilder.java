package org.spincast.core.routing;

import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.hotlinking.HotlinkingManager;

/**
 * Builder to create static resources.
 */
public interface StaticResourceBuilder<R extends RequestContext<?>> {

    /**
     * This should only by called by *plugins*.
     * <p>
     * When this method is called, the resulting route for
     * the resource won't
     * be remove by default when the
     * {@link Router#removeAllRoutes()} method is used. The
     * {@link Router#removeAllRoutes(boolean)} with <code>true</code>
     * will have to be called to actually remove it.
     * <p>
     * This is useful during development, when an hotreload mecanism
     * is used to reload the Router without
     * restarting the application, when the application routes changed.
     * By default only the routes for which the 
     * {@link #isSpicastCoreRouteOrPluginRoute()}
     * method has been called would then be reloaded.
     */
    public StaticResourceBuilder<R> spicastOrPluginAddedResource();

    /**
     * The URL pointing to the resource.
     */
    public StaticResourceBuilder<R> url(String url);

    /**
     * The path to the resource, on the classpath.
     */
    public StaticResourceBuilder<R> classpath(String path);

    /**
     * The absolute path to the resource, on the file system.
     * 
     * @param absolutePath the absolute path to the resource. If this is a file
     * and not a directory, remember that it will be served as a static resource, 
     * so its extension is important for the
     * correct <code>Content-Type</code> to be sent!
     */
    public StaticResourceBuilder<R> pathAbsolute(String absolutePath);

    /**
     * The path to the resource, on the file system, relative to the
     * temp Spincast directory, as returned by
     * {@link SpincastConfig#getSpincastTempDir() SpincastConfig::getSpincastTempDir()}
     * 
     * @param relativePath the relative path to the resource. If this is a file
     * and not a directory, remember that it will be served as a static resource, 
     * so its extension is important for the
     * correct <code>Content-Type</code> to be sent!
     */
    public StaticResourceBuilder<R> pathRelative(String relativePath);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R) 
     *                                          SpincastFilters#cors(R context)   
     */
    public StaticResourceBuilder<R> cors();

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)      
     */
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead)    
     */
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent)   
     */
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead,
                                         Set<String> extraHeadersAllowedToBeSent);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies)   
     */
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead,
                                         Set<String> extraHeadersAllowedToBeSent,
                                         boolean allowCookies);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.SpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          SpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          int maxAgeInSeconds
     *                                          )   
     */
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead,
                                         Set<String> extraHeadersAllowedToBeSent,
                                         boolean allowCookies,
                                         int maxAgeInSeconds);

    /**
     * Adds public cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     */
    public StaticResourceBuilder<R> cache(int seconds);

    /**
     * Adds cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     * 
     * @param isPrivate should the cache be private?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     */
    public StaticResourceBuilder<R> cache(int seconds, boolean isPrivate);

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
    public StaticResourceBuilder<R> cache(int seconds, boolean isPrivate, Integer secondsCdn);

    /**
     * When a static resource is flagged as being
     * *hotlinking protected*, the server will 
     * validate the <code>origin</code> and <code>referer</code>
     * of the request. If those don't matche the host of the
     * application, a protection will be apply, the one provided
     * by the default {@Link HotlinkingManager}.
     */
    public StaticResourceBuilder<R> hotlinkingProtected();

    /**
     * When a static resource is flagged as being
     * *hotlinking protected*, the server will 
     * validate the <code>origin</code> and <code>referer</code>
     * of the request. If those don't matche the host of the
     * application, a protection will be apply, the one provided
     * by the specified <code>hotlinkingManager</code>.
     */
    public StaticResourceBuilder<R> hotlinkingProtected(HotlinkingManager hotlinkingManager);

    /**
     * Saves the static resource route to the router.
     * <p>
     * If the creation of the resource was not started using
     * an <code>Router</code> object, an exception will be
     * thrown.
     */
    public void handle();

    /**
     * Saves the static resource route.
     * 
     * Note that the generated resource won't be cached 
     * if there is a queryString on the request : it will 
     * always be generated. 
     * <p>
     * If the creation of the resource was not started using
     * an <code>Router</code> object, an exception will be
     * thrown.
     * 
     * @param generator If the resource is not found, the specified
     * generator will be used to generate it and
     * the result will be saved.
     */
    public void handle(Handler<R> generator);

    /**
     * Saves the static resource route.
     * 
     * <p>
     * If the creation of the resource was not started using
     * an <code>Router</code> object, an exception will be
     * thrown.
     * 
     * @param generator If the resource is not found, the specified
     * generator will be used to generate it and
     * the result will be saved.
     * 
     * @param ignoreQueryString If <code>true</code>, only one 
     * instance of the resource will be generated and cached. If 
     * <code>false</code> (the default), the resource will always be
     * generated if there is a queryString.
     */
    public void handle(Handler<R> generator, boolean ignoreQueryString);

    /**
     * Creates and returns the static resource without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the static resource 
     * to the router at the end of the build process!
     */
    public StaticResource<R> create();

}

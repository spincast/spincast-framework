package org.spincast.core.routing;

import java.util.Set;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;

/**
 * Builder to create static resources.
 */
public interface IStaticResourceBuilder<R extends IRequestContext<?>> {

    /**
     * The URL pointing to the resource.
     */
    public IStaticResourceBuilder<R> url(String url);

    /**
     * The path to the resource, on the classpath.
     */
    public IStaticResourceBuilder<R> classpath(String path);

    /**
     * The absolute path to the resource, on the file system.
     * 
     * @param absolutePath the absolute path to the resource. If this is a file
     * and not a directory, remember that it will be served as a static resource, 
     * so its extension is important for the
     * correct <code>Content-Type</code> to be sent!
     */
    public IStaticResourceBuilder<R> pathAbsolute(String absolutePath);

    /**
     * The path to the resource, on the file system, relative to the
     * temp Spincast directory, as returned by
     * {@link ISpincastConfig#getSpincastTempDir() ISpincastConfig::getSpincastTempDir()}
     * 
     * @param relativePath the relative path to the resource. If this is a file
     * and not a directory, remember that it will be served as a static resource, 
     * so its extension is important for the
     * correct <code>Content-Type</code> to be sent!
     */
    public IStaticResourceBuilder<R> pathRelative(String relativePath);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R) 
     *                                          ISpincastFilters#cors(R context)   
     */
    public IStaticResourceBuilder<R> cors();

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins)      
     */
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead)    
     */
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent)   
     */
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          Set<String> extraHeadersAllowedToBeSent);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies)   
     */
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          Set<String> extraHeadersAllowedToBeSent,
                                          boolean allowCookies);

    /**
     * Enables Cross-Origin Resource Sharing (Cors)
     * 
     * @see org.spincast.core.filters.ISpincastFilters#cors(R, Set, Set, Set, boolean) 
     *                                          ISpincastFilters#cors(R context, 
     *                                          Set&lt;String&gt; allowedOrigins,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeRead,
     *                                          Set&lt;String&gt; extraHeadersAllowedToBeSent,
     *                                          boolean allowCookies,
     *                                          int maxAgeInSeconds
     *                                          )   
     */
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
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
    public IStaticResourceBuilder<R> cache(int seconds);

    /**
     * Adds cache headers.
     * 
     * @param seconds The number of seconds the resource associated with
     * this route should be cached.
     * 
     * @param isPrivate should the cache be private?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     */
    public IStaticResourceBuilder<R> cache(int seconds, boolean isPrivate);

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
    public IStaticResourceBuilder<R> cache(int seconds, boolean isPrivate, Integer secondsCdn);

    /**
     * Saves the static resource route to the router.
     * 
     * If the creation of the resource was not started using
     * an <code>IRouter</code> object, an exception will be
     * thrown.
     */
    public void save();

    /**
     * Saves the static resource route.
     * 
     * If the creation of the resource was not started using
     * an <code>IRouter</code> object, an exception will be
     * thrown.
     * 
     * @param generator If the resource is not found, the specified
     * generator will be used to generate it and
     * the result will be saved.
     */
    public void save(IHandler<R> generator);

    /**
     * Creates and returns the static resource without adding it to
     * the router.
     * 
     * NOTE : use <code>save(...)</code> instead to save the static resource 
     * to the router at the end of the build process!
     */
    public IStaticResource<R> create();

}

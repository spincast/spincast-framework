package org.spincast.core.routing;

import java.util.Set;

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
     * The path to the resource, on the file system.
     */
    public IStaticResourceBuilder<R> fileSystem(String path);

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

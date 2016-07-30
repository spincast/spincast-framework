package org.spincast.core.routing;

/**
 * Cache configurations available for a static resource.
 */
public interface IStaticResourceCacheConfig {

    /**
     * The number of seconds to use when sending
     * caching headers. 
     * <p>
     * If &lt;=0 no caching headers will be sent.
     * </p>
     */
    public int getCacheSeconds();

    /**
     * Should <code>private</code> be used when sending
     * caching headers?
     * 
     * <a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">
     * more info...</a>
     */
    public boolean isCachePrivate();

    /**
     * The number of seconds to use to cache by CDNs.
     * <p>
     * If <code>null</code>, this option would be set.
     * </p>
     */
    public Integer getCacheSecondsCdn();
}

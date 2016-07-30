package org.spincast.core.exchange;

import java.util.Date;

/**
 * Request context add-on to work with cache headers.
 */
public interface ICacheHeadersRequestContextAddon<R extends IRequestContext<?>> {

    /**
     * Specifies the current ETag (strong) of the resource.
     * <p>
     * A strong comparison will be used to compare the request ETag to this
     * current ETag.
     * </p>
     * <p>
     * Skip, or use <code>null</code> if the resource doesn't exist.
     * </p>
     */
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag);

    /**
     * Specifies the current strong or weak ETag of the resource.
     * <p>
     * A strong comparison will be used to compare the request ETag to this
     * current ETag.
     * </p>
     * <p>
     * Skip, or use <code>null</code> if the resource doesn't exist.
     * </p>
     */
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag, boolean currentTagIsWeak);

    /**
     * Specifies the current strong or weak ETag of the resource.
     * <p>
     * Skip, or use <code>null</code> if the resource doesn't exist.
     * </p>
     * 
     * @param weakComparison should a weak comparison be used instead of
     * a strong one to compare the request ETag to the current ETag?
     */
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag, boolean currentTagIsWeak, boolean weakComparison);

    /**
     * Specifies the last modification date of the resource.
     * <p>
     * Skip, or use <code>null</code> if the resource doesn't exist.
     * </p>
     */
    public ICacheHeadersRequestContextAddon<R> lastModified(Date lastModificationDate);

    /**
     * The number of seconds the client should cache this resource
     * before requesting it again.
     */
    public ICacheHeadersRequestContextAddon<R> cache(int seconds);

    /**
     * The number of seconds the client should cache this resource
     * before requesting it again.
     * 
     * @param isPrivate should the cache be 'private'?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     */
    public ICacheHeadersRequestContextAddon<R> cache(int seconds, boolean isPrivate);

    /**
     * The number of seconds the client should cache this resource
     * before requesting it again.
     * 
     * @param isPrivate should the cache be 'private'?
     * (<a href="https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en#public-vs-private">help</a>)
     * @param secondsCdn The number of seconds the resource associated with
     * this route should be cached by a CDN/proxy. If <code>null</code>, it
     * won't be used.
     */
    public ICacheHeadersRequestContextAddon<R> cache(int seconds, boolean isPrivate, Integer secondsCdn);

    /**
     * Sends "No Cache" headers so the resource is not cached
     * at all by the client.
     */
    public ICacheHeadersRequestContextAddon<R> noCache();

    /**
     * Call this when you are done setting
     * <code>ETag</code> and/or <code>Last-Modified</code> to validate
     * them agains the headers sent by the client.
     * <p>
     * If this method returns <code>true</code>, the route handler should return immediately
     * without returning/creating/modifying/deleting the associated resource! Appropriate headers 
     * have already been set on the response.
     * </p>
     * 
     * @param resourceCurrentlyExists Does the resource currently exist?
     */
    public boolean validate(boolean resourceCurrentlyExists);
}

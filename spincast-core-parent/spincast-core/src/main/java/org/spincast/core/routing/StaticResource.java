package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;

/**
 * A static resource, from the classpath or the
 * file system. 
 * 
 * <p>
 * A static resource is served directly
 * by the HTTP server. But if the resource is not found
 * and a <code>generator</code> exists, then the server will call
 * the framework instead of returning 404. 
 * </p>
 */
public interface StaticResource<R extends RequestContext<?>> {

    /**
     * Is this a resource added by Spincast itself
     * or by a plugin? Otherwise, the resource is
     * considered as an application resource.
     */
    public boolean isSpicastOrPluginAddedResource();

    /**
     * The type of static resource.
     */
    public StaticResourceType getStaticResourceType();

    /**
     * The URL to reach this static resource.
     */
    public String getUrlPath();

    /**
     * The path of the resource on the classpath or on the
     * file system.
     */
    public String getResourcePath();

    /**
     * The generator to call to generate this resource if it
     * doesn't exist yet.
     * 
     * @return the generator or <code>null</code> if there are none.
     */
    public Handler<R> getGenerator();

    /**
     * If <code>true</code>, only one 
     * instance of the resource will be generated and cached. If 
     * <code>false</code> (the default), the resource will always be
     * generated if there is a queryString.
     */
    public boolean isIgnoreQueryString();

    /**
     * The cors configurations for the static resource.
     * 
     * If <code>null</code>, cors won't be enabled for that
     * resource.
     */
    public StaticResourceCorsConfig getCorsConfig();

    /**
     * The cache configurations for the static resource.
     * 
     * If <code>null</code>, no caching headers will be sent,
     * but the last modification-date of the resource will be
     * validated and <code>304 - Not modified</code> will be
     * returned if applicable.
     */
    public StaticResourceCacheConfig getCacheConfig();

    /**
     * Is the resource on the classpath?
     */
    public boolean isClasspath();

    /**
     * Is the resource on the file system?
     */
    public boolean isFileSytem();

    /**
     * Is the resource a file?
     */
    public boolean isFileResource();

    /**
     * Is the resource a directory?
     */
    public boolean isDirResource();

    /**
     * Can this resource be generated?
     */
    public boolean isCanBeGenerated();

}

package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;

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
public interface IStaticResource<R extends IRequestContext<?>> {

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
    public IHandler<R> getGenerator();

    /**
     * The cors configurations for the static resource.
     * 
     * If <code>null</code>, cors won't be enabled for that
     * resource.
     */
    public IStaticResourceCorsConfig getCorsConfig();

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

package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;

/**
 * Factory to create static resources builders.
 */
public interface IStaticResourceBuilderFactory<R extends IRequestContext<?>> {

    /**
     * Creates a static resources builder by specifying
     * if the resource to build is a file or a directory, but without
     * using a router.
     * 
     * The {@link org.spincast.core.routing.IStaticResourceBuilder#save() save()} method
     * will throw an expception if called. Only {@link org.spincast.core.routing.IStaticResourceBuilder#create() create()}
     * will be available.
     * 
     */
    public IStaticResourceBuilder<R> create(boolean isDir);

    /**
     * Creates a static resources builder by specifying
     * if the resource to build is a file or a directory.
     */
    public IStaticResourceBuilder<R> create(IRouter<R> router, boolean isDir);
}

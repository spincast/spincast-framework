package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Factory to create static resources builders.
 */
public interface StaticResourceBuilderFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * Creates a static resources builder by specifying
     * if the resource to build is a file or a directory, but without
     * using a router.
     * 
     * The {@link org.spincast.core.routing.StaticResourceBuilder#save() save()} method
     * will throw an expception if called. Only {@link org.spincast.core.routing.StaticResourceBuilder#create() create()}
     * will be available.
     * 
     */
    public StaticResourceBuilder<R> create(boolean isDir);

    /**
     * Creates a static resources builder by specifying
     * if the resource to build is a file or a directory.
     */
    public StaticResourceBuilder<R> create(Router<R, W> router, boolean isDir);
}

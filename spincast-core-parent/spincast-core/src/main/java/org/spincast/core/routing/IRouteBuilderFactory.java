package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;

/**
 * Factory to create a route builder.
 */
public interface IRouteBuilderFactory<R extends IRequestContext<?>> {

    /**
     * Creates a route builder without using a router.
     * The {@link org.spincast.core.routing.IRouteBuilder#save() save()} method
     * will throw an expception if called. Only {@link org.spincast.core.routing.IRouteBuilder#create() create()}
     * will be available.
     */
    public IRouteBuilder<R> create();

    /**
     * Creates a route builder using the specified router.
     */
    public IRouteBuilder<R> create(IRouter<R> router);
}

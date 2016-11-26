package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;

/**
 * A route handler. Called by the front controller to handle
 * a request, when the associated route matches.
 */
public interface Handler<R extends RequestContext<?>> {

    /**
     * Receives a <code>request context</code> object and
     * handles it.
     */
    public void handle(R context);
}

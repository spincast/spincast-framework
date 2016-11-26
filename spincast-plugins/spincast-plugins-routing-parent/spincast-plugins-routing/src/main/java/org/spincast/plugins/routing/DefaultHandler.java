package org.spincast.plugins.routing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;

/**
 * Default route handler.
 */
public interface DefaultHandler extends Handler<DefaultRequestContext> {

    @Override
    public void handle(DefaultRequestContext context);
}

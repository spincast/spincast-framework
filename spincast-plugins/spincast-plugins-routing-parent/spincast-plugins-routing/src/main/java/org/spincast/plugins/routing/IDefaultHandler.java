package org.spincast.plugins.routing;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;

/**
 * Default route handler.
 */
public interface IDefaultHandler extends IHandler<IDefaultRequestContext> {

    @Override
    public void handle(IDefaultRequestContext context);
}

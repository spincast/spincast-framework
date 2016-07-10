package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;

/**
 * Factory to create IRedirectRuleBuilders.
 */
public interface IRedirectRuleBuilderFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    public IRedirectRuleBuilder create(IRouter<R, W> router,
                                       String oldPath);
}

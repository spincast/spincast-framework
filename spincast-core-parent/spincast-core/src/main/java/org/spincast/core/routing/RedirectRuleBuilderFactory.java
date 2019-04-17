package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Factory to create RedirectRuleBuilders.
 */
public interface RedirectRuleBuilderFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    public RedirectRuleBuilder<R, W> create(Router<R, W> router,
                                            String oldPath);
}

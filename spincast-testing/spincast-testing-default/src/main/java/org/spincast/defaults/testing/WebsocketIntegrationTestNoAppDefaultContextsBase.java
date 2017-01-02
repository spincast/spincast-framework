package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;

/**
 * Base class for WebSocket tests without an existing
 * application and using the default request context and
 * the default WebSocket context.
 */
public abstract class WebsocketIntegrationTestNoAppDefaultContextsBase extends
                                                                       WebsocketIntegrationTestNoAppBase<DefaultRequestContext, DefaultWebsocketContext> {
    // nothing required
}

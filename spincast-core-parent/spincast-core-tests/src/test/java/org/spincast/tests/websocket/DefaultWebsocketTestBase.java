package org.spincast.tests.websocket;

import org.junit.Ignore;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.websocket.IDefaultWebsocketContext;

/**
 * Websocket test base using the default route context and default
 * websocket context.
 */
@Ignore
public class DefaultWebsocketTestBase extends WebsocketTestBase<IDefaultRequestContext, IDefaultWebsocketContext> {
    // nothing required
}

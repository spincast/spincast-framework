package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.testing.core.IntegrationTestAppBase;

/**
 * Base for integration test classes that use an existing
 * Application to start the Server (calling its <code>main()</code>
 * method) and uses the default <code>Request context</code> type
 * and <code>WebSocket Context</code> type.
 */
public abstract class IntegrationTestAppDefaultContextsBase extends
                                                            IntegrationTestAppBase<DefaultRequestContext, DefaultWebsocketContext> {
    // nothing required
}

package org.spincast.website.exchange;

import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;

/**
 * The only purpose of this interface is to make it
 * easier to inject the application specific router by using
 * "AppRouter" instead of "Router&lt;AppRequestContext, DefaultWebsocketContext&gt;".
 */
public interface AppRouter extends Router<AppRequestContext, DefaultWebsocketContext> {
    // nothing required
}

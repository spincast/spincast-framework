package org.spincast.quickstart.exchange;

import org.spincast.core.routing.Router;

/**
 * The only purpose of this interface is to make it
 * easier to inject the application specific Router by using
 * "AppRouter" instead of "Router&lt;AppRequestContext, AppWebsocketContext&gt;".
 */
public interface AppRouter extends Router<AppRequestContext, AppWebsocketContext> {
    // nothing required
}

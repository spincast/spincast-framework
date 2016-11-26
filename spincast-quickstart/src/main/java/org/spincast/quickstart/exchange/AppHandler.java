package org.spincast.quickstart.exchange;

import org.spincast.core.routing.Handler;

/**
 * The only purpose of this interface is to make it
 * easier to declare a Route Handler as "AppHandler"
 * instead of "Handler&lt;AppRequestContext&gt;".
 */
public interface AppHandler extends Handler<AppRequestContext> {
    // nothing required
}

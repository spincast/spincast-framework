package org.spincast.quickstart.exchange;

import org.spincast.core.routing.IRouter;

/**
 * The only purpose of this interface is to make it
 * easier to inject the application specific router by using
 * "IAppRouter" instead of "IRouter&lt;IAppRequestContext&gt;".
 */
public interface IAppRouter extends IRouter<IAppRequestContext> {
    // nothing required
}

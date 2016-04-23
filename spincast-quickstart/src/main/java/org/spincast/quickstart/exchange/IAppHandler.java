package org.spincast.quickstart.exchange;

import org.spincast.core.routing.IHandler;

/**
 * The only purpose of this interface is to make it
 * easier to declare an handler as "IAppHandler"
 * instead of "IHandler&lt;IAppRequestContext&gt;".
 */
public interface IAppHandler extends IHandler<IAppRequestContext> {
    // nothing required
}

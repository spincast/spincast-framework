package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Factory to create CacheBusterRemovalHandler
 */
public interface ICacheBusterRemovalHandlerFactory {

    public ICacheBusterRemovalHandler create(HttpHandler nextHandler);

}

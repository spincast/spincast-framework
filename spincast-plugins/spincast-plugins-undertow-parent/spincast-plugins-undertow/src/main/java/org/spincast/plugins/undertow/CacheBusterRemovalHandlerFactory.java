package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Factory to create CacheBusterRemovalHandler
 */
public interface CacheBusterRemovalHandlerFactory {

    public CacheBusterRemovalHandler create(HttpHandler nextHandler);

}

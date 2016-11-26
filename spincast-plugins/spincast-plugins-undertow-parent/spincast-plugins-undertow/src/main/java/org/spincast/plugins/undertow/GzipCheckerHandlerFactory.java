package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Factory to create GzipCheckerHandlers
 */
public interface GzipCheckerHandlerFactory {

    public GzipCheckerHandler create(HttpHandler nextHandler,
                                     String specificTargetFilePath);
}

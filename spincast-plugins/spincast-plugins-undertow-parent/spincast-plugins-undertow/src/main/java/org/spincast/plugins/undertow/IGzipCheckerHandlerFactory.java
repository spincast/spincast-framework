package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Factory to create GzipCheckerHandlers
 */
public interface IGzipCheckerHandlerFactory {

    public IGzipCheckerHandler create(HttpHandler nextHandler,
                                      String specificTargetFilePath);
}

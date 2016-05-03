package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Handler that will check if gzip compression is required for
 * the resource and, if so, will call a gzip handler before
 * calling the next handler.
 */
public interface IGzipCheckerHandler extends HttpHandler {
    // nothing more required
}

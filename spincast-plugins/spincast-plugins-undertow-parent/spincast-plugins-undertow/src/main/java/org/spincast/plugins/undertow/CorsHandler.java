package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Handler that will add cors headers, when required.
 */
public interface CorsHandler extends HttpHandler {
    // nothing more required
}

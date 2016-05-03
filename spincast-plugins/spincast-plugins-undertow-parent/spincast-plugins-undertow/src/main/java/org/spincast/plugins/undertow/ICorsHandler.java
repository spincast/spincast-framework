package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Handler that will add cors headers, when required.
 */
public interface ICorsHandler extends HttpHandler {
    // nothing more required
}

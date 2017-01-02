package org.spincast.plugins.undertow;

import io.undertow.server.HttpHandler;

/**
 * Handler that will skip the default resource handler
 * if there is at least one queryString parameter.
 */
public interface SkipResourceOnQueryStringHandler extends HttpHandler {
    // nothing more required
}

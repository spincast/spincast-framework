package org.spincast.plugins.undertow;

import io.undertow.server.handlers.resource.ResourceManager;

/**
 * Undertow's ClassPathResourceManager adds a "/" at the
 * end of the "prefix" so it can't be used to serve a specific file.
 */
public interface SpincastClassPathFileResourceManager extends ResourceManager {
    // nothing more required
}

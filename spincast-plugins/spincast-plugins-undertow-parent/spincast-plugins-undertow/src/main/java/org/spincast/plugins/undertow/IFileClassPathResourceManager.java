package org.spincast.plugins.undertow;

import io.undertow.server.handlers.resource.ResourceManager;

/**
 * Undertow 1.2.12.Final's ClassPathResourceManager adds a "/" at the
 * end of the resource path and this breaks serving a specific file.
 */
public interface IFileClassPathResourceManager extends ResourceManager {
    // nothing more required
}

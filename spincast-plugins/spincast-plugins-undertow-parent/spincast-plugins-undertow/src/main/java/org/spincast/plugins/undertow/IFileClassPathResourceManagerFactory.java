package org.spincast.plugins.undertow;

/**
 * Factory to create GzipCheckerHandlers
 */
public interface IFileClassPathResourceManagerFactory {

    public IFileClassPathResourceManager create(String path);
}

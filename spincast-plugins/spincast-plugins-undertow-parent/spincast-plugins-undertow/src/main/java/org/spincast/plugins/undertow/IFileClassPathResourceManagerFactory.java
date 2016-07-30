package org.spincast.plugins.undertow;

/**
 * Factory to create IFileClassPathResourceManager
 */
public interface IFileClassPathResourceManagerFactory {

    public IFileClassPathResourceManager create(String path);
}

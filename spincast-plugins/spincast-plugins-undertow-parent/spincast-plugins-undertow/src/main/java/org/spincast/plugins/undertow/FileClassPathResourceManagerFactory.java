package org.spincast.plugins.undertow;

/**
 * Factory to create FileClassPathResourceManager
 */
public interface FileClassPathResourceManagerFactory {

    public FileClassPathResourceManager create(String path);
}

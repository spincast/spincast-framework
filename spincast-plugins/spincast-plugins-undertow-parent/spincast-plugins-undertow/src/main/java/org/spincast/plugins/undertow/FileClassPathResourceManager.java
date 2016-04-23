package org.spincast.plugins.undertow;

import java.io.IOException;
import java.net.URL;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;

/**
 * Undertow 1.2.12.Final's ClassPathResourceManager adds a "/" at the
 * end of the resource path and this breaks serving a specific file.
 */
public class FileClassPathResourceManager implements ResourceManager {

    private final String filePath;

    public FileClassPathResourceManager(String filePath) {
        this.filePath = filePath;
    }

    protected String getFilePath() {
        return this.filePath;
    }

    @Override
    public Resource getResource(String path) throws IOException {

        final URL resource = getClass().getClassLoader().getResource(getFilePath());
        if(resource == null) {
            return null;
        } else {
            return new URLResource(resource, resource.openConnection(), getFilePath());
        }
    }

    @Override
    public boolean isResourceChangeListenerSupported() {
        return false;
    }

    @Override
    public void registerResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    @Override
    public void removeResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    @Override
    public void close() throws IOException {
    }
}

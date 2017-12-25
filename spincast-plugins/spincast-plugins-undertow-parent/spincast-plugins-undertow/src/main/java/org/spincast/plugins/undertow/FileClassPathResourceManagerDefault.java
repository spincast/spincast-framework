package org.spincast.plugins.undertow;

import java.io.IOException;
import java.net.URL;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.URLResource;

public class FileClassPathResourceManagerDefault implements FileClassPathResourceManager {

    private final String filePath;

    @AssistedInject
    public FileClassPathResourceManagerDefault(@Assisted String filePath) {
        this.filePath = filePath;
    }

    protected String getFilePath() {
        return this.filePath;
    }

    @Override
    public Resource getResource(String path) throws IOException {

        final URL resource = getClass().getClassLoader().getResource(getFilePath());
        if (resource == null) {
            return null;
        } else {
            return new URLResource(resource, getFilePath());
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

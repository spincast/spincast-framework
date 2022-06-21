package org.spincast.plugins.undertow;

import java.io.IOException;
import java.net.URL;

import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.URLResource;

public class SpincastClassPathDirResourceManagerDefault implements SpincastClassPathDirResourceManager {

    private final String rootUrl;
    private final String dirPath;

    private final SpincastUtils spincastUtils;

    @AssistedInject
    public SpincastClassPathDirResourceManagerDefault(@Assisted("rootUrl") String rootUrl,
                                                      @Assisted("dirPath") String dirPath,
                                                      SpincastUtils spincastUtils) {
        if (StringUtils.isBlank(rootUrl)) {
            rootUrl = "/";
        } else {
            rootUrl = "/" + StringUtils.stripStart(rootUrl, "/");
            if (!rootUrl.endsWith("/")) {
                rootUrl += "/";
            }
        }
        this.rootUrl = rootUrl;

        if (StringUtils.isBlank(dirPath)) {
            dirPath = "";
        } else {
            dirPath = StringUtils.stripStart(dirPath, "/");
            if (!dirPath.endsWith("/")) {
                dirPath += "/";
            }
        }
        this.dirPath = dirPath;

        this.spincastUtils = spincastUtils;
    }

    protected String getRootUrl() {
        return this.rootUrl;
    }

    protected String getDirPath() {
        return this.dirPath;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    public Resource getResource(String path) throws IOException {

        String pathToServe;
        if (!StringUtils.isBlank(path)) {
            String pathNoRoot = path;
            if (pathNoRoot.toLowerCase().startsWith(getRootUrl().toLowerCase())) {
                pathNoRoot = pathNoRoot.substring(getRootUrl().length());
            }

            if (StringUtils.isBlank(pathNoRoot) || pathNoRoot.equals("/")) {
                return null;
            }

            pathToServe = getDirPath() + StringUtils.stripStart(pathNoRoot, "/");
        } else {
            pathToServe = this.getDirPath();
        }

        final URL resourceUrl = getClass().getClassLoader().getResource(pathToServe);
        if (resourceUrl == null) {
            return null;
        } else {
            Resource resource = new URLResource(resourceUrl, getDirPath());

            // We do not support directory index files (such as "index.html")
            if (resource.isDirectory()) {
                return null;
            }

            return resource;
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

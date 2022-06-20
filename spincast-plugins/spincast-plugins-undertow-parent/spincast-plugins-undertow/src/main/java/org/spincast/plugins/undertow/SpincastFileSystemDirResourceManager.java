package org.spincast.plugins.undertow;

import java.io.File;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import io.undertow.server.handlers.resource.FileResource;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;

/**
 * Manager to serve files under a specific directory.
 * <p>
 * The directory may not exist yet.
 */
public class SpincastFileSystemDirResourceManager extends FileResourceManager {

    private String baseUrl;
    private final File dir;

    public SpincastFileSystemDirResourceManager(String baseUrl, File dir) {
        this(baseUrl, dir, 1024);
    }

    public SpincastFileSystemDirResourceManager(String baseUrl,
                                                File dir,
                                                long transferMinSize) {
        super(transferMinSize, true, false, new String[]{});
        if (StringUtils.isBlank(baseUrl)) {
            baseUrl = "/";
        }
        this.baseUrl = baseUrl;

        if (dir == null) {
            throw new RuntimeException("The directory to serve can't be null...");
        }

        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new RuntimeException("The file \"" + dir.getAbsolutePath() + "\" to serve exists and is not a directory!");
            }
        } else {
            boolean res = dir.mkdirs();
            if (!res) {
                throw new RuntimeException("Unable to create the directory to serve: " + dir.getAbsolutePath());
            }
        }
        this.dir = dir;
    }

    protected String getBaseUrl() {
        return this.baseUrl;
    }

    protected File getDir() {
        return this.dir;
    }

    @Override
    public Resource getResource(final String path) {
        try {
            // We do not support guessing a directory index file.
            if (StringUtils.isBlank(path) || path.trim().equals("/")) {
                return null;
            }

            File fileToServe;

            String pathNoRoot = path;
            if (pathNoRoot.toLowerCase().startsWith(getBaseUrl().toLowerCase())) {
                pathNoRoot = pathNoRoot.substring(getBaseUrl().length());
            }
            fileToServe = new File(this.getDir(), pathNoRoot);

            if (!fileToServe.getCanonicalPath().startsWith(this.getDir().getCanonicalPath())) {
                throw new RuntimeException("Invalid file to serve");
            }

            if (!fileToServe.exists()) {
                return null;
            }

            return new FileResource(fileToServe, this, fileToServe.getAbsolutePath());
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}

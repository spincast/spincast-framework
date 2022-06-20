package org.spincast.plugins.undertow;

import java.io.File;

import io.undertow.server.handlers.resource.FileResource;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;

/**
 * Manager to get a specific File.
 * <p>
 * The file may not exist yet.
 * <p>
 * Doesn't check if the file changes or not.
 */
public class SpincastFileSystemFileResourceManager extends FileResourceManager {

    private final File file;

    public SpincastFileSystemFileResourceManager(File file) {
        this(file, 1024);
    }

    public SpincastFileSystemFileResourceManager(File file,
                                                 long transferMinSize) {
        super(transferMinSize, true, false, new String[]{});
        if (file == null) {
            throw new RuntimeException("The file to serve can't be null...");
        }

        if (file.exists() && file.isDirectory()) {
            throw new RuntimeException("The file \"" + file.getAbsolutePath() + "\" to serve exists but is a directory!");
        }

        this.file = file;
    }

    protected File getFile() {
        return this.file;
    }

    @Override
    public Resource getResource(final String path) {
        //==========================================
        // No need to use the specified "path" param,
        // we target a specific file.
        //==========================================

        if (this.getFile() == null || !this.getFile().exists()) {
            return null;
        }

        return new FileResource(this.getFile(), this, this.getFile().getAbsolutePath());
    }
}

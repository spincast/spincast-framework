package org.spincast.plugins.httpclient;

/**
 * Information on a file to upload.
 */
public class FileToUpload {

    private final String path;
    private final boolean isClasspathPath;
    private final String name;

    public FileToUpload(String path, boolean isClasspathPath, String name) {
        this.path = path;
        this.isClasspathPath = isClasspathPath;
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isClasspathPath() {
        return this.isClasspathPath;
    }

    public String getName() {
        return this.name;
    }

}

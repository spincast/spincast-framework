package org.spincast.core.utils;

/**
 * Represents a file that can be taken from
 * the file system or from the classpath.
 */
public class ResourceInfo {

    private final String path;
    private final boolean classpathResource;

    public ResourceInfo(String path, boolean classpathResource) {
        this.path = path;
        this.classpathResource = classpathResource;
    }

    /**
     * The path to the resource.
     * <p>
     * If {@link #isClasspathResource()} is <code>true</code>,
     * this should be a classpath path (starting with a "/" or not, this
     * makes no difference). Otherwise it must be
     * the <em>absolute path</em> to the resource on the file system.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Is it a resource on the classpath or on the
     * file system?
     */
    public boolean isClasspathResource() {
        return this.classpathResource;
    }

}

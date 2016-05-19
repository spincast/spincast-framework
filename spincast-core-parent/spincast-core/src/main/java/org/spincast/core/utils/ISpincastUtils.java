package org.spincast.core.utils;

import java.io.File;
import java.util.Locale;

/**
 * Spincast utilities.
 */
public interface ISpincastUtils {

    /**
     * Zips a directory.
     * 
     * @param targetZipFile the target .zip file. If the parent directories don't
     * exist, tries to create them.
     * 
     * @param If <code>true</code>, the directory itself will be included in the
     * zip file, otherwise only its content will be.
     */
    public void zipDirectory(File directoryToZip, File targetZipFile, boolean includeDirItself);

    /**
     * Extracts a .zip file to the specified directory.
     * 
     * @param targetDir The target directory. If it doesn't exist, tried to
     * create it (and its parents, if required).
     */
    public void zipExtract(File zipFile, File targetDir);

    /**
     * Gets the <code>mime type</code> from a path, using its extension.
     * 
     * @return the <code>mime type</code> or <code>null</code> if it can't be
     * decided.
     */
    public String getMimeTypeFromPath(String path);

    /**
     * Gets the <code>mime type</code> from the extension.
     * 
     * @return the <code>mime type</code> or <code>null</code> if it can't be
     * decided.
     */
    public String getMimeTypeFromExtension(String extension);

    /**
     * Gets the best Locale to use given a "Accept-Language" HTTP header.
     * 
     * @return the best Locale to use or <code>null</code> 
     * if the given header can't be parsed.
     */
    public Locale getLocaleBestMatchFromAcceptLanguageHeader(String acceptLanguageHeader);

    /**
     * Should the specified <code>Content-Type</code> be gzipped?
     */
    public boolean isContentTypeToSkipGziping(String contentType);

    /**
     * Returns the working directory: the directory
     * in which the executable .jar is located.
     * 
     * @return the working directory or <code>null</code> if the application is 
     * running inside an IDE.
     */
    public File getAppJarDirectory();

    /**
     * Gets the current Spincast version.
     */
    public String getSpincastCurrentVersion();

}

package org.spincast.core.utils;

import java.io.File;
import java.util.Locale;

/**
 * Spincast utilities.
 */
public interface ISpincastUtils {

    /**
     * Should the specified <code>Content-Type</code> be gzipped?
     */
    public boolean isContentTypeToSkipGziping(String contentType);

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
     * Returns the working directory: the directory
     * in which the executable .jar is located.
     * 
     * @return the working directory or <code>null</code> if the application is 
     * running inside an IDE.
     */
    public File getAppJarDirectory();

    /**
     * Gets the current Spincast stable version
     */
    public String getSpincastCurrentVersion();

}

package org.spincast.core.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.spincast.shaded.org.apache.commons.io.FilenameUtils;

/**
 * Spincast utilities.
 */
public interface SpincastUtils {

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
     * Gets the <code>mime type</code> using multiple sources of information.
     *
     * @param contentTypeHeader an already existing Content-Type header on the
     * response. Can be null.
     * @param resourcePath the path (absolute or relative) to the target resource. Can be null.
     * @param requestPath the path of the current request. Can be null.
     *
     * @return the <code>mime type</code> or <code>null</code> if it can't be
     * decided.
     */
    public String getMimeTypeFromMultipleSources(String responseContentTypeHeader,
                                                 String resourcePath,
                                                 String requestPath);

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
     * Is the application currently running from
     * an executable .jar? An executable .jar is a
     * jar with <code>Main-Class</code> in its
     * <code>META-INF/MANIFEST.MF</code>.
     */
    public boolean isRunningFromExecutableJar();

    /**
     * If the project is running from an executable
     * .jar file, this will return the directory containing
     * this .jar file.
     *
     * @return the directory path or <code>null</code> if the
     * application is not running from an executable .jar
     * file.
     */
    public File getAppJarDirectory();

    /**
     * Is the classpath resource in a .jar file? Otherwise
     * it is on the file system.
     */
    public boolean isClasspathResourceInJar(String resourcePath);

    /**
     * If the project is not running from an executable
     * .jar file, this will return the root directory of the
     * project on the file system.
     *
     * @return the directory path or <code>null</code> if the
     * application is running from an executable .jar
     * file.
     */
    public File getAppRootDirectoryNoJar();

    /**
     * Gets the current Spincast version.
     */
    public String getSpincastCurrentVersion();

    /**
     * The cache buster to use.
     * <p>
     * This should probably change each time
     * the application is restarted or at least redeployed.
     * </p>
     * <p>
     * It should also be in such a format that it's possible to
     * remove it from a given text.
     * </p>
     * <p>
     * This must be kept in sync with
     * {@link #removeCacheBusterCode() removeCacheBusterCode}!
     * </p>
     */
    public String getCacheBusterCode();

    /**
     * Removes the cache buster code occurences from the
     * given text.
     * <p>
     * Note that this won't simply remove the <em>current</em>
     * cache busting code, it will remove <em>any</em> valid cache busting code...
     * This is what we want since we don't want a client sending a request
     * containing an old cache busting code to break!
     * </p>
     * <p>
     * This must be kept in sync with
     * {@link #getCacheBusterCode() getCacheBusterCode}!
     * </p>
     */
    public String removeCacheBusterCodes(String text);

    /**
     * Reads a file on the classpath and returns it as a
     * String.
     * <p>
     * Paths are always considered from the root at the classpath.
     * You can start the path with a "/" or not, it makes no difference.
     * <p>
     * Uses the UTF-8 encoding.
     *
     * @return the content of the file or <code>null</code>
     * if not found.
     */
    public String readClasspathFile(String path);

    /**
     * Reads a file on the classpath and returns it as a
     * String.
     * <p>
     * Paths are always considered from the root at the classpath.
     * You can start the path with a "/" or not, it makes no difference.
     *
     * @return the content of the file or <code>null</code>
     * if not found.
     */
    public String readClasspathFile(String path, String encoding);

    /**
     * Gets the {@link InputStream} from a resource on the classpath.
     * <p>
     * IMPORTANT : the code is responsible to close the
     * inputstream!
     * <p>
     * Also note that when an application is running from an executable
     * .jar, you must use this instead of trying to get a
     * {@link File} from the resource!
     *
     * @return the {@link InputStream} or <code>null</code> if the resource is
     * not found.
     */
    public InputStream getClasspathInputStream(String classpathPath);

    /**
     * Copy a file from the classpath (located in a .jar or not) to the specified
     * file system file.
     * <p>
     * If the target file already exists, if will be overwritten.
     */
    public void copyClasspathFileToFileSystem(String classpathFilePath, File targetFile);

    /**
     * Copy a directory from the classpath (located in a .jar or not) to the
     * specified file system directory.
     * <p>
     * If the target directory already exists, if will be overwritten.
     */
    public void copyClasspathDirToFileSystem(String classpathDirPath, File targetDir);

    /**
     * Valid of a String only contains characters over 31 (or
     * DEL (127).
     *
     * @return <code>true</code> if the String only contains
     * characters over 31 (or DEL (127). If the String is
     * <code>null</code>, <code>true</code> is returned.
     */
    public boolean isContainsSpecialCharacters(String str);

    /**
     * Format a String so it can be outputed in a HTML
     * page, as a one line javascript string or the
     * attribute of an HTML element.
     *
     * @param singleQuotes if <code>false</code>, double quotes are
     * expected as the string delimiter.
     */
    public String inQuotesStringFormat(String str, boolean singleQuotes);

    /**
     * Perform a <em>case insensitive</em>
     * <code>valueOf(value)</code> search on an enum.
     */
    public <T extends Enum<?>> T enumValueOfInsensitive(Class<T> enumClass, String str);

    public long[] convertLongSetToLongPrimitiveArray(Set<Long> longSet);

    /**
     * Creates a randomly named temp file path targetting the
     * writable directory.
     */
    public String createTempFilePath();

    /**
     * Cleans HTML, only allowing basic elements.
     * <p>
     * Elements from https://jsoup.org/apidocs/org/jsoup/safety/Whitelist.html#basic--
     * are allowed + "center" tags. Relative paths are allowed in links.
     * <p>
     * Images tags are not allowed.
     */
    public String basicHtml(boolean newlineToBrFirst, String html);

    /**
     * Cleans HTML, only allowing basic elements.
     * <p>
     * Elements from https://jsoup.org/apidocs/org/jsoup/safety/Whitelist.html#basic--
     * are allowed + "center" tags. Relative paths are allowed in links.
     *
     * @param allowImages if <code>true</code> image tags will also be allowed.
     */
    public String basicHtml(boolean newlineToBrFirst, String html, boolean allowImages);

    /**
     * Returns a random element from the set or
     * <code>null</code> if the set is null or empty.
     */
    public <T> T getRandomElement(Set<T> set);

    /**
     * Return the querystring of an URL, without
     * leading "?". Returns an empty string if there is
     * no querystring.
     * <p>
     * The querystring returned is <em>NOT</em> decoded using
     * {@link URLDecoder.decode}).
     */
    public String getQuerystringFromUrl(String url);

    /**
     * Parses an URL and returns the list of its querystring parameters,
     * if any.
     * <p>
     * The querystring will be decoded using (using {@link URLDecoder.decode}).
     */
    public Map<String, List<String>> getQuerystringParametersFromUrl(String url);

    /**
     * Parses a querystring (starting with a "?" or not) and returns
     * the list of parameters.
     *
     * @param decodeQueryStringFirst if <code>true</code>, the querystring will be
     * decoded (using {@link URLDecoder.decode}) prior to being parsed.
     */
    public Map<String, List<String>> getParametersFromQuerystring(String querystring, boolean decodeQueryStringFirst);

    /**
     * Returns <code>true</code> if the name of the requested
     * resource ends with the specified suffix. The name is the part
     * before the extension, if there is one.
     * <p>
     * Manage the fact that routing may be case sensitive
     * or not.
     * <p>
     * Note that you should specify a "-" if your
     * suffixe starts with this.
     */
    public boolean isRequestedResourceNameEndsWithBeforeExtension(URI currentURI, String suffix);

    /**
     * Returns <code>true</code> if the name of the requested
     * resource ends with the specified suffix. The name is the part
     * before the extension, if there is one.
     * <p>
     * Manage the fact that routing may be case sensitive
     * or not.
     * <p>
     * Note that you should specify a "-" if your
     * suffixe starts with this.
     *
     * @param resourceBaseName the base name of the requested resource, as
     * it would be returned by {@link FilenameUtils#getBaseName(String)},
     * without the potential extension.
     */
    public boolean isRequestedResourceNameEndsWithBeforeExtension(String resourceBaseName, String suffix);


}

package org.spincast.core.config;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * The configurations required by Spincast.
 */
public interface ISpincastConfig {

    /**
     * Let this to <code>true</code> on development environment,
     * where errors can be publicly displayed, where cache can
     * be disabled, etc. In production set it to <code>false</code>
     * 
     * The default value is <code>true</code>.
     * 
     * @return <code>true</code> if debug mode is enabled.
     */
    public boolean isDebugEnabled();

    /**
     * The host/IP on which the server will listen to.
     * 
     * The default value is <code>0.0.0.0</code>.
     * 
     * @return the host/IP
     */
    public String getServerHost();

    /**
     * The HTTP (unsecure) port on which the server will listen on. 
     * If &lt;= 0, it won't be used.
     * 
     * The default value is <code>44419</code> so HTTP
     * is enabled by default.
     * 
     * @return the HTTP port
     */
    public int getHttpServerPort();

    /**
     * The HTTPS (secure) port on which the server will listen on.
     * If &lt;= 0, it won't be used.
     * 
     * The default value is <code>-1</code> so HTTPS
     * is not enabled by default.
     * 
     * @return the HTTPS port
     */
    public int getHttpsServerPort();

    /**
     * The path to the <code>KeyStore</code>, for SSL. Can be a 
     * classpath path or and absolute path. 
     * The classpath will be checked first.
     * 
     * Only used if <code>getHttpsServerPort()</code> returns a port &gt; 0.
     * 
     * The default value is <code>null</code>.
     * 
     * @return the path to the <code>KeyStore</code>
     */
    public String getHttpsKeyStorePath();

    /**
     * The type of the <code>KeyStore</code>, for SSL.
     * For example: "JKS".
     * 
     * Only used if <code>getHttpsServerPort()</code> returns a port &gt; 0.
     * 
     * The default value is <code>null</code>.
     * 
     * @return the type of the <code>KeyStore</code>
     */
    public String getHttpsKeyStoreType();

    /**
     * The "storepass" of the <code>KeyStore</code>, for SSL.
     * 
     * Only used if <code>getHttpsServerPort()</code> returns a port > 0.
     * 
     * The default value is <code>null</code>.
     * 
     * @return the "storepass" of the <code>KeyStore</code>
     */
    public String getHttpsKeyStoreStorePass();

    /**
     * The "keypass" of the <code>KeyStore</code>, for SSL.
     * 
     * Only used if <code>getHttpsServerPort()</code> returns a port &gt; 0.
     * 
     * The default value is <code>null</code>.
     * 
     * @return the "keypass" of the <code>KeyStore</code>
     */
    public String getHttpsKeyStoreKeypass();

    /**
     * Are the URLS case-sensitive or not, during the route matching 
     * process?
     * 
     * The default value is <code>false</code>.
     * 
     * @return if they are case-sensitive
     * 
     * @see <a href="http://stackoverflow.com/questions/7996919/should-url-be-case-sensitive">Should URL be case sensitive?</a>
     */
    public boolean isRoutesCaseSensitive();

    /**
     * Returns the name of the environment.
     * 
     * The default value is <code>local</code>.
     * 
     * @return the name of the environment
     */
    public String getEnvironmentName();

    /**
     * Maximum number of bytes a request's body can have.
     * "-1" means no limit.
     * 
     * The default value is <code>104857600</code> (100MB).
     * 
     * @return the maximum number of bytes
     */
    public long getServerMaxRequestBodyBytes();

    /**
     * Even if gziping of the response is enabled, those Content-Types 
     * still won't be gzipped.
     * 
     * If an entry ends with "*", it will be considered as a <i>prefix</i>
     * (ex: "image/*" will match all Content-Types starting with "image/"). 
     * Otherwise, the current response Content-Type will have to exactly
     * match the entry (case insensitive), or being followed by a " " or
     * a ";".
     * 
     * @return the Content-Types that shouldn't be gzipped.
     */
    public List<String> getContentTypesToSkipGziping();

    /**
     * A directory where generated files and temporary files 
     * can be written by Spincast.
     * 
     * The default value uses <code>System.getProperty("java.io.tmpdir")</code>
     * to create a temporary directory.
     * 
     * @return a directory with write permissions for Spincast.
     */
    public File getSpincastWritableDir();

    /**
     * The default Locale to use if no other Locale can be found
     * as a "best match", for the current request.
     * 
     * The default value is <code>Locale.US</code>.
     * 
     * @return the default Locale
     */
    public Locale getDefaultLocale();

    /**
     * The maximum number of time a request can be forwarded to another
     * route. After this number, an exception will be thrown.
     * 
     * The default value is <code>2</code>.
     * 
     * @return the maximum number of time 
     * 
     */
    public int getRouteForwardingMaxNumber();

}

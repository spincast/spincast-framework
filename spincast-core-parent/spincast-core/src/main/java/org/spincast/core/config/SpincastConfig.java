package org.spincast.core.config;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.spincast.core.routing.StaticResourceCacheConfig;

/**
 * The configurations required by Spincast.
 */
public interface SpincastConfig {

    /**
     * Returns the name of the environment.
     * 
     * The default value is <code>local</code>.
     * 
     * @return the name of the environment
     */
    public String getEnvironmentName();

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
     * The application can't know by itself on which
     * scheme/host/port it is served publicly.
     * <p>
     * For example, the server can be started using host
     * "0.0.0.0", but the public URL to reach the application
     * will be <code>http://www.example.com</code>. The port can also
     * be different, for example if a reverse-proxy is used.
     * </p>
     * <p>
     * We need the public informations, for example :
     * <ul>
     *     <li>
     *         To build absolute URLs
     *     </li>
     *     <li>
     *         To create a cookie with an approriate <code>domain</code>,
     *         when none is specified.
     *     </li>
     *     <li>
     *         ...
     *     </li>
     * </ul>
     * </p>
     */
    public String getPublicUrlBase();

    /**
     * The public scheme
     */
    public String getPublicServerScheme();

    /**
     * The public host
     */
    public String getPublicServerHost();

    /**
     * The public port
     */
    public int getPublicServerPort();

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
     * Only used if <code>getHttpsServerPort()</code> returns a port &gt; 0.
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
    public String getHttpsKeyStoreKeyPass();

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
     * A directory where temporary generated files 
     * can be written by Spincast.
     * <p>
     * This directory will be emptied each time the application starts.
     * </p>
     * <p>
     * The default value uses <code>System.getProperty("java.io.tmpdir")</code>
     * to create the directory.
     * </p>
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

    /**
     * When using the <code>.cache()</code> method on
     * a route builder, this is the default number of
     * seconds to use.
     */
    public int getDefaultRouteCacheFilterSecondsNbr();

    /**
     * When using the <code>.cache()</code> method on
     * a route builder, is the cache private by default?
     */
    public boolean isDefaultRouteCacheFilterPrivate();

    /**
     * When using the <code>.cache()</code> method on
     * a route builder, this is the default number of
     * seconds to use by the CDNs.
     */
    public Integer getDefaultRouteCacheFilterSecondsNbrCdns();

    /**
     * The default cache configurations for the static resource.
     * <p>
     * This will be used if no <code>cache(...)</code> method is
     * used when building the static resource's route.
     * </p>
     * 
     * @param isDynamicResource if <code>true</code>, a generator is 
     * use when the resource doesn't exist. Those kind of generated resources
     * may be modified more often then trully static resources, so we may
     * want to use a different caching period.
     * 
     * @return the default cache configuration to use or <code>null</code>
     * so no cache headers are sent at all.
     */
    public StaticResourceCacheConfig getDefaultStaticResourceCacheConfig(boolean isDynamicResource);

    /**
     * If <code>true</code>, the dynamic resources (static resource
     * which have a generator in case they don't exist) won't be
     * saved to disk. This means that the generator will always be called,
     * so changes during development will be picked up.
     */
    public boolean isWriteToDiskDynamicStaticResource();

    /**
     * Should the GlobalTemplateVariablesAdderFilter be automatically
     * added to all routes?
     * <p>
     * This filter bind some default variables to be used by the 
     * templating engine.
     * </p>
     */
    public boolean isAddDefaultTemplateVariablesFilter();

    /**
     * If {@link #isAddDefaultTemplateVariablesFilter() addGlobalTemplateVariablesAdderFilter}
     * returns <code>true</code>, then this is the position at
     * which the filter will be added.
     */
    public int getDefaultTemplateVariablesFilterPosition();

    /**
     * Gets the maximum number of <code>keys</code> in a Map that
     * can be parsed as <code>JsonPaths</code> to create a
     * <code>JsonObject</code>.
     * <p>
     * This maximum is to prevent malicious user to POST
     * a very big number of <code>keys</code> and harm
     * the server.
     * </p>
     */
    public int getMaxNumberOfKeysWhenConvertingMapToJsonObject();

    /**
     * Gets the maximum length of a <code>key</code> in a Map that
     * can be parsed as <code>JsonPaths</code> using the 
     * {@link org.spincast.core.json.JsonManager#fromMap(java.util.Map, boolean) create}
     * method.
     * <p>
     * This maximum is to prevent malicious user to POST
     * very long and complex <code>keys</code> and harm
     * the server.
     * </p>
     */
    public int getKeyMaxLengthWhenConvertingMapToJsonObject();

    /**
     * Gets the maximum index of an element of an array, when parsing
     * a <code>JsonPath</code>.
     */
    public int getJsonPathArrayIndexMax();

    /**
     * By default, if {@link #getPublicServerHost()} returns
     * <code>"localhost"</code>, {@link #getEnvironmentName()} is
     * not <code>"local"</code> and {@link #isDebugEnabled()} is false,
     * an exception is thrown when the application starts.
     * <p>
     * This validation is to make sure the developers 
     * didn't forget to override the {@link #getPublicUrlBase()}
     * method when they release to a non local environment.
     * <p>
     * You can disable this validation if you want.
     */
    public boolean isValidateLocalhostHost();

    /**
     * If <code>true</code>, Spincast will always try to set a cookie
     * to validate if cookies are enabled on the client.
     * <p>
     * This is enabled by default
     */
    public boolean isEnableCookiesValidator();

    /**
     * Name of the Cookie to use to validate if 
     * cookies are enabled.
     * <p>
     * The default is "spincast_cookies_enabled".
     */
    public String getCookiesValidatorCookieName();

    /**
     * Name of the Cookie to to use save a 
     * Flash Message id.
     */
    public String getCookieNameFlashMessage();

    /**
     * The name of the queryString parameter to use
     * for a Flash Message id, when cookies are not
     * available.
     */
    public String getQueryParamFlashMessageId();

    /**
     * Name of the Cookie to use to 
     * save the user <code>Locale</code>.
     * <p>
     * The default is "spincast_locale"
     */
    public String getCookieNameLocale();

    /**
     * Name of the root variables reserved for
     * Spincast in the response's <em>model</em>.
     */
    public String getSpincastModelRootVariableName();

}

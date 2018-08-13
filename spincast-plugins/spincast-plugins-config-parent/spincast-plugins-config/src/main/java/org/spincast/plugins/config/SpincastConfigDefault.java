package org.spincast.plugins.config;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.DictionaryEntryNotFoundBehavior;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * If you extend this class (which is recommended to implement your
 * configuration), beware that causing circular dependencies is very 
 * easy if you inject more dependencies than SpincastConfigPluginConfig!
 * Indeed, most components depend on the config components.
 * 
 */
public class SpincastConfigDefault extends ConfigFinder implements SpincastConfig {

    public static final String ENVIRONMENT_NAME_DEFAULT = "local";

    private File spincastBaseWritableDir;
    private StaticResourceCacheConfig staticResourceCacheConfig;
    private StaticResourceCacheConfig dynamicResourceCacheConfig;
    private String publicServerSchemeHostPort;

    private URI publicUri;

    /**
     * Constructor
     */
    @Inject
    protected SpincastConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig) {
        super(spincastConfigPluginConfig.getClasspathFilePath(),
              spincastConfigPluginConfig.getExternalFilePath(),
              spincastConfigPluginConfig.getEnvironmentVariablesPrefixes(),
              spincastConfigPluginConfig.isEnvironmentVariablesStripPrefix(),
              spincastConfigPluginConfig.getSystemPropertiesPrefixes(),
              spincastConfigPluginConfig.isSystemPropertiesStripPrefix(),
              spincastConfigPluginConfig.isExternalFileConfigsOverrideEnvironmentVariables(),
              spincastConfigPluginConfig.isThrowExceptionIfSpecifiedClasspathConfigFileIsNotFound(),
              spincastConfigPluginConfig.isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound());
    }

    @Inject
    protected void init() {

        //==========================================
        // Initializes the configurations
        //==========================================
        getRawConfigs();
    }

    @Override
    public String getEnvironmentName() {
        return ENVIRONMENT_NAME_DEFAULT;
    }

    @Override
    public int getHttpServerPort() {
        return 44419;
    }

    @Override
    public int getHttpsServerPort() {
        return -1;
    }

    @Override
    public String getHttpsKeyStorePath() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreType() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreKeyPass() {
        return null;
    }

    protected URI getPublicURI() {

        if (this.publicUri == null) {
            try {
                this.publicUri = new URI(getPublicUrlBase());
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        return this.publicUri;
    }

    /**
     * Three of the rare "final" methods in Spincast!
     */
    @Override
    public final String getPublicServerScheme() {
        return getPublicURI().getScheme();
    }

    @Override
    public final String getPublicServerHost() {
        return getPublicURI().getHost();
    }

    @Override
    public final int getPublicServerPort() {
        return getPublicURI().getPort();
    }

    /**
     * You should override this config!!!!
     */
    @Override
    public String getPublicUrlBase() {

        if (this.publicServerSchemeHostPort == null) {
            StringBuilder builder = new StringBuilder();
            if (getHttpsServerPort() > -1) {
                builder.append("https://");
                builder.append(getHostForDefaultPublicServerSchemeHostPort());
                int port = getHttpsServerPort();
                if (port != 443) {
                    builder.append(":").append(port);
                }
            } else {
                builder.append("http://");
                builder.append(getHostForDefaultPublicServerSchemeHostPort());

                int port = getHttpServerPort();
                if (port != 80) {
                    builder.append(":").append(port);
                }
            }

            this.publicServerSchemeHostPort = builder.toString();
        }

        return this.publicServerSchemeHostPort;
    }

    protected String getHostForDefaultPublicServerSchemeHostPort() {
        String serverHost = getServerHost();
        if ("0.0.0.0".equals(serverHost)) {
            serverHost = "localhost";
        }
        return serverHost;
    }

    @Override
    public String getServerHost() {
        return "0.0.0.0";
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isRoutesCaseSensitive() {
        return false;
    }

    @Override
    public long getServerMaxRequestBodyBytes() {
        return 104857600; // 104857600 => 100MB
    }

    @Override
    public List<String> getContentTypesToSkipGziping() {

        List<String> prefixes = new ArrayList<String>();

        prefixes.add("image/*");
        prefixes.add("video/*");
        prefixes.add("audio/*");
        prefixes.add("application/pdf");
        prefixes.add("application/octet-stream");
        prefixes.add("application/exe");
        prefixes.add("application/x-font-woff");
        prefixes.add("application/zip");
        prefixes.add("application/x-gzip");
        prefixes.add("application/x-rar-compressed");

        return prefixes;
    }

    /**
     * Will call {@link #getSpincastWritableDirPath()} to get
     * the path to use for the writable directory. If this
     * method returns <code>null</code>, a directory will be
     * created under the <code>java.io.tmpdir</code> directory.
     */
    @Override
    public File getWritableRootDir() {

        if (this.spincastBaseWritableDir == null) {

            File baseDir = null;
            String path = getSpincastWritableDirPath();
            if (StringUtils.isBlank(path)) {
                baseDir = new File(System.getProperty("java.io.tmpdir"));
                if (!baseDir.isDirectory()) {
                    throw new RuntimeException("Temporary directory doesn't exist : " + baseDir.getAbsolutePath());
                }
            } else {

                baseDir = new File(path);

                //==========================================
                // Relative path?
                //==========================================
                if (!baseDir.isAbsolute()) {

                    String relativePath = StringUtils.stripStart(path, "./\\");

                    //==========================================
                    // Directory next to the executable .jar file?
                    //==========================================
                    File jarDir = getAppJarDirectory();
                    if (jarDir != null) {
                        baseDir = new File(jarDir.getAbsolutePath() + "/" + relativePath);
                    } else {

                        //==========================================
                        // Running outside an executable jar?
                        //==========================================
                        File noJarDir = getAppRootDirectoryNoJar();
                        if (noJarDir != null) {
                            baseDir = new File(noJarDir.getAbsolutePath() + "/" + relativePath);
                        }
                    }
                }
            }

            File spincastDir = new File(baseDir, "spincast");

            if (!spincastDir.isDirectory()) {
                boolean result = spincastDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the Spincast writable directory : " +
                                               spincastDir.getAbsolutePath());
                }
            }

            this.spincastBaseWritableDir = spincastDir;
        }
        return this.spincastBaseWritableDir;
    }

    @Override
    public File getTempDir() {
        return new File(getWritableRootDir(), "temp");
    }

    /**
     * The path to the writable directory.
     * <p>
     * The path can be relative or absolute.
     * 
     * @return the path or <code>null</code> to use the
     * default location (using the "java.io.tmpdir" folder).
     */
    protected String getSpincastWritableDirPath() {
        return null;
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.US;
    }

    @Override
    public TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("UTC");
    }

    @Override
    public int getRouteForwardingMaxNumber() {
        return 2;
    }

    @Override
    public int getDefaultRouteCacheFilterSecondsNbr() {
        return 3600; // == 1 hour
    }

    @Override
    public boolean isDefaultRouteCacheFilterPrivate() {
        return false;
    }

    @Override
    public Integer getDefaultRouteCacheFilterSecondsNbrCdns() {
        return null; // option not used by default
    }

    @Override
    public boolean isWriteToDiskDynamicStaticResource() {
        return !isDebugEnabled();
    }

    @Override
    public boolean isAddDefaultTemplateVariablesFilter() {
        return true;
    }

    @Override
    public int getDefaultTemplateVariablesFilterPosition() {
        //==========================================
        // "-10", before filter, so default values
        // can be modified by the main handler.
        //==========================================
        return -10;
    }

    @Override
    public StaticResourceCacheConfig getDefaultStaticResourceCacheConfig(boolean isDynamicResource) {

        if (this.staticResourceCacheConfig == null) {
            this.staticResourceCacheConfig = new StaticResourceCacheConfig() {

                @Override
                public int getCacheSeconds() {
                    return 86400; // 86400 => 1 day
                }

                @Override
                public boolean isCachePrivate() {
                    return false;
                }

                @Override
                public Integer getCacheSecondsCdn() {
                    return null;
                }
            };
        }

        if (this.dynamicResourceCacheConfig == null) {
            this.dynamicResourceCacheConfig = new StaticResourceCacheConfig() {

                @Override
                public int getCacheSeconds() {
                    return 3600; // 3600 => 1 hour
                }

                @Override
                public boolean isCachePrivate() {
                    return false;
                }

                @Override
                public Integer getCacheSecondsCdn() {
                    return null;
                }
            };
        }

        if (isDynamicResource) {
            return this.dynamicResourceCacheConfig;
        } else {
            return this.staticResourceCacheConfig;
        }
    }

    @Override
    public int getMaxNumberOfKeysWhenConvertingMapToJsonObject() {
        return 100;
    }

    @Override
    public int getKeyMaxLengthWhenConvertingMapToJsonObject() {
        return 512;
    }

    @Override
    public int getJsonPathArrayIndexMax() {
        return 10000;
    }

    @Override
    public boolean isValidateLocalhostHost() {
        return true;
    }

    @Override
    public boolean isEnableCookiesValidator() {
        return true;
    }

    @Override
    public String getQueryParamFlashMessageId() {
        return "spincast_flash";
    }

    @Override
    public String getCookieNameFlashMessage() {
        return "spincast_flash";
    }

    @Override
    public String getCookieNameLocale() {
        return "spincast_locale";
    }

    @Override
    public String getCookieNameTimeZoneId() {
        return "spincast_timezone";
    }

    @Override
    public String getCookiesValidatorCookieName() {
        return "spincast_cookies_enabled";
    }

    @Override
    public String getSpincastModelRootVariableName() {
        return "spincast";
    }

    @Override
    public String getValidationElementDefaultName() {
        return "validation";
    }

    @Override
    public DictionaryEntryNotFoundBehavior getDictionaryEntryNotFoundBehavior() {

        if (isDebugEnabled()) {
            return DictionaryEntryNotFoundBehavior.EXCEPTION;
        }

        return DictionaryEntryNotFoundBehavior.RETURN_EMPTY_STRING;
    }

}

package org.spincast.plugins.configpropsfile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * This configuration implementation will try to find a
 * properties file to load the configurations.
 */
public class SpincastConfigPropsFileBased extends SpincastConfigDefault implements SpincastConfig, FreeKeyConfig {

    protected final Logger logger = LoggerFactory.getLogger(SpincastConfigPropsFileBased.class);

    public static final String APP_PROPERTIES_KEY_ENVIRONMENT_NAME = "spincast.environment.name";
    public static final String APP_PROPERTIES_KEY_ENVIRONMENT_IS_DEBUG = "spincast.environment.isDebug";
    public static final String APP_PROPERTIES_KEY_SERVER_HOST = "spincast.server.host";
    public static final String APP_PROPERTIES_KEY_HTTP_SERVER_PORT = "spincast.httpServer.port";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_PORT = "spincast.httpsServer.port";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_PATH = "spincast.httpsServer.keystore.path";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_TYPE = "spincast.httpsServer.keystore.type";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_STOREPASS = "spincast.httpsServer.keystore.storepass";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_KEYPASS = "spincast.httpsServer.keystore.keypass";

    private final SpincastUtils spincastUtils;
    private final String[] mainArgs;
    private final SpincastConfigPropsFilePluginConfig pluginConfig;

    private boolean specificAppPropertiesFilePathInited = false;
    private String specificAppPropertiesFilePath;
    private Properties appProperties;
    private boolean appPropertiesLoaded = false;
    private String foundPropertiesFilePath;
    private String publicServerSchemeHostPort;

    /**
     * Constructor
     */
    @Inject
    public SpincastConfigPropsFileBased(SpincastUtils spincastUtils,
                                        @MainArgs @Nullable String[] mainArgs,
                                        SpincastConfigPropsFilePluginConfig pluginConfig) {
        super();
        this.spincastUtils = spincastUtils;

        if(mainArgs == null) {
            mainArgs = new String[0];
        }
        this.mainArgs = mainArgs;

        this.pluginConfig = pluginConfig;
    }

    protected SpincastConfigPropsFilePluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected String getSpecificAppPropertiesFilePath() {

        if(!this.specificAppPropertiesFilePathInited) {
            this.specificAppPropertiesFilePathInited = true;
            this.specificAppPropertiesFilePath = lookForPropsFileSpecificPath();
        }

        return this.specificAppPropertiesFilePath;
    }

    /**
     * Look for a specific .properties file.
     * 
     * You can override this to implement your own strategy
     * to locate the .properties file to use.
     */
    protected String lookForPropsFileSpecificPath() {

        //==========================================
        // Is the strategy to use a main arg parameter enabled?
        //==========================================
        if(getPluginConfig().getSpecificPathMainArgsPosition() > 0) {

            int argPos = getPluginConfig().getSpecificPathMainArgsPosition();

            String[] mainArgs = getMainArgs();
            if(mainArgs != null && mainArgs.length >= argPos) {
                String filePath = mainArgs[argPos - 1];
                this.logger.info("Main argument #" + argPos + " found to be used as the configuration file path: " + filePath);
                return filePath;
            } else {
                this.logger.info("The path to the configuration file to use was not found as a main argument, we'll use another strategy.");
            }
        }
        return null;
    }

    /**
     * The path to the acutally found .properties file.
     * 
     * @return the path of the file or <code>null</code> if
     * none was found.
     */
    protected String getFoundPropertiesFilePath() {
        return this.foundPropertiesFilePath;
    }

    protected String getConfigKeyEnvironmentName() {
        return APP_PROPERTIES_KEY_ENVIRONMENT_NAME;
    }

    protected String getConfigKeyEnvironmentIsDebug() {
        return APP_PROPERTIES_KEY_ENVIRONMENT_IS_DEBUG;
    }

    protected String getConfigKeyServerHost() {
        return APP_PROPERTIES_KEY_SERVER_HOST;
    }

    protected String getConfigKeyHttpServerPort() {
        return APP_PROPERTIES_KEY_HTTP_SERVER_PORT;
    }

    protected String getConfigKeyHttpsServerPort() {
        return APP_PROPERTIES_KEY_HTTPS_SERVER_PORT;
    }

    protected String getConfigKeyHttpsServerKeystorePath() {
        return APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_PATH;
    }

    protected String getConfigKeyHttpsServerKeystoreType() {
        return APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_TYPE;
    }

    protected String getConfigKeyHttpsServerKeystoreStorePass() {
        return APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_STOREPASS;
    }

    protected String getConfigKeyHttpsServerKeystoreKeyPass() {
        return APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_KEYPASS;
    }

    /**
     * Here is the algorithm used to find the configuration file :
     * 
     * 1) If getSpecificAppPropertiesFilePath() is not <code>null</code>,
     *    we use it as the path.
     *    
     * 2) Otherwise, we try to find the configuration file next to the ".jar"
     *    itself.
     *    
     * 3) It we didn't found the configuration file next to the ".jar" file,
     *    or if there is no .jar file (the code is running from an IDE, for example),
     *    then we fallback to the default configurations which are defined in the
     *    SpincastConfig base class.
     */
    protected Properties getAppProperties() {
        if(!this.appPropertiesLoaded) {
            this.appPropertiesLoaded = true;

            try {

                this.appProperties = new Properties();

                String specificPath = getSpecificAppPropertiesFilePath();
                if(!StringUtils.isBlank(specificPath)) {
                    if(!new File(specificPath).isFile()) {
                        throw new RuntimeException("Specified environment specific configuration file not found: " +
                                                   specificPath);
                    }
                    this.logger.info("Using environment specified configuration file : " + specificPath);
                    this.foundPropertiesFilePath = specificPath;
                    FileInputStream stream = new FileInputStream(specificPath);
                    try {
                        this.appProperties.load(stream);
                    } finally {
                        IOUtils.closeQuietly(stream);
                    }

                } else {
                    File jarDir = getSpincastUtils().getAppJarDirectory();
                    if(jarDir != null) {
                        File appConfigFile =
                                new File(jarDir.getAbsolutePath() + "/" + getPluginConfig().getNextToJarConfigFileName());
                        if(!appConfigFile.isFile()) {
                            this.logger.warn("No environment specific configuration file found. " +
                                             "Default configurations will be used! Was looking for : " +
                                             appConfigFile.getAbsolutePath());
                        } else {
                            this.logger.info("Environment specific configuration file found : " +
                                             appConfigFile.getAbsolutePath());
                            this.foundPropertiesFilePath = appConfigFile.getAbsolutePath();
                            this.appProperties.load(new FileInputStream(appConfigFile));
                        }
                    } else {
                        this.logger.info("Running from an IDE, default configurations will be used!");
                    }
                }
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        return this.appProperties;
    }

    /**
     * Gets a String configuration and throws an exception if the
     * specified key is not found.
     */
    @Override
    public String getConfig(String key) {
        return (String)getConfig(key, null, false);
    }

    /**
     * Gets a String configuration and uses the specified default value if the
     * key is not found.
     */
    @Override
    public String getConfig(String key, String defaultValue) {
        return (String)getConfig(key, defaultValue, true);
    }

    /**
     * Gets a boolean configuration and throws an exception if the
     * specified key is not found.
     */
    @Override
    public Boolean getConfigBoolean(String key) {
        Object value = getConfig(key, null, false);
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * Gets a boolean configuration and uses the specified default value if the
     * key is not found.
     */
    @Override
    public Boolean getConfigBoolean(String key, Boolean defaultValue) {
        Object value = getConfig(key, defaultValue, true);
        if(value instanceof Boolean) {
            return (boolean)value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * Gets a integer configuration and throws an exception if the
     * specified key is not found.
     */
    @Override
    public Integer getConfigInteger(String key) {
        Object value = getConfig(key, null, false);
        return Integer.parseInt(value.toString());
    }

    /**
     * Gets a integer configuration and uses the specified default value if the
     * key is not found.
     */
    @Override
    public Integer getConfigInteger(String key, Integer defaultValue) {
        Object value = getConfig(key, defaultValue, true);
        if(value instanceof Integer) {
            return (Integer)value;
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * Gets a configuration.
     * 
     * If 'useDefaultValue' is false, throws an exception if the key is not found.
     * Otherwise, returns the 'defaultValue'.
     */
    protected Object getConfig(String key, Object defaultValue, boolean useDefaultValue) {
        String value = getAppProperties().getProperty(key);
        if(value == null) {

            if(useDefaultValue) {
                return defaultValue;
            }

            String msg = "Configuration '" + key + "' not found and no default value provided.";
            if(getFoundPropertiesFilePath() != null) {
                msg += " Properties file: " + getFoundPropertiesFilePath();
            }
            throw new RuntimeException(msg);
        }
        return value;
    }

    @Override
    public String getEnvironmentName() {
        return getConfig(getConfigKeyEnvironmentName(), super.getEnvironmentName());
    }

    @Override
    public boolean isDebugEnabled() {
        return getConfigBoolean(getConfigKeyEnvironmentIsDebug(), super.isDebugEnabled());
    }

    @Override
    public String getServerHost() {
        return getConfig(getConfigKeyServerHost(), super.getServerHost());
    }

    @Override
    public int getHttpServerPort() {
        return getConfigInteger(getConfigKeyHttpServerPort(), super.getHttpServerPort());
    }

    @Override
    public int getHttpsServerPort() {
        return getConfigInteger(getConfigKeyHttpsServerPort(), super.getHttpsServerPort());
    }

    @Override
    public String getHttpsKeyStorePath() {
        return getConfig(getConfigKeyHttpsServerKeystorePath(), super.getHttpsKeyStorePath());
    }

    @Override
    public String getHttpsKeyStoreType() {
        return getConfig(getConfigKeyHttpsServerKeystoreType(), super.getHttpsKeyStoreType());
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        return getConfig(getConfigKeyHttpsServerKeystoreStorePass(), super.getHttpsKeyStoreStorePass());
    }

    @Override
    public String getHttpsKeyStoreKeypass() {
        return getConfig(getConfigKeyHttpsServerKeystoreKeyPass(), super.getHttpsKeyStoreStorePass());
    }

    @Override
    public String getPublicServerSchemeHostPort() {

        if(this.publicServerSchemeHostPort == null) {
            StringBuilder builder = new StringBuilder();
            if(getHttpsServerPort() > -1) {
                builder.append("https://")
                       .append(getServerHost());
                int port = getHttpsServerPort();
                if(port != 443) {
                    builder.append(":").append(port);
                }
            } else {
                builder.append("http://")
                       .append(getServerHost());
                int port = getHttpsServerPort();
                if(port != 80) {
                    builder.append(":").append(port);
                }
            }

            this.publicServerSchemeHostPort = builder.toString();
        }

        return this.publicServerSchemeHostPort;
    }

    @Override
    public String getQueryParamFlashMessageId() {
        return "flash";
    }

}

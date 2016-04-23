package org.spincast.website;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.config.SpincastConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class AppConfig extends SpincastConfig implements IAppConfig {

    protected final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String APP_PROPERTIES_FILE_NAME = "app.properties";
    public static final String APP_PROPERTIES_KEY_ENVIRONMENT_NAME = "environment.name";
    public static final String APP_PROPERTIES_KEY_ENVIRONMENT_IS_DEBUG = "environment.isDebug";
    public static final String APP_PROPERTIES_KEY_SERVER_HOST = "server.host";
    public static final String APP_PROPERTIES_KEY_HTTP_SERVER_PORT = "httpServer.port";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_PORT = "httpsServer.port";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_PATH = "httpsServer.keystore.path";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_TYPE = "httpsServer.keystore.type";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_STOREPASS = "httpsServer.keystore.storepass";
    public static final String APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_KEYPASS = "httpsServer.keystore.keypass";

    private final String specificAppPropertiesFilePath;
    private final ISpincastUtils spincastUtils;
    private Properties appProperties;

    /**
     * Constructor
     */
    @Inject
    public AppConfig(ISpincastUtils spincastUtils,
                     @MainArgs @Nullable String[] mainArgs) {
        super();
        this.spincastUtils = spincastUtils;

        //==========================================
        // We allow the starting script to pass the path to a
        // configuration .properties file, if any.
        //==========================================
        if(mainArgs != null && mainArgs.length > 0) {
            this.specificAppPropertiesFilePath = mainArgs[0];
        } else {
            this.specificAppPropertiesFilePath = null;
        }
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    /**
     * Here's the algorothme used to find the environment configurations :
     * 
     * 1) If a parameter has been passed to the main method of the application,
     *    it is considered as to be the path to the configuration file to use.
     *    
     * 2) Otherwise, we try to find the configuration file next to the ".jar"
     *    itself. It found we use it
     *    
     * 3) It we didn't found the configuration file next to the ".jar" file,
     *    or if there is no .jar file (the code is runnign from an IDE for example),
     *    the we fallback to the default configurations which are defined in the
     *    SpincastConfig class.
     */
    protected Properties getAppProperties() {
        if(this.appProperties == null) {

            try {

                this.appProperties = new Properties();

                if(!StringUtils.isBlank(this.specificAppPropertiesFilePath)) {
                    this.logger.info("Using specified configuration file : " + this.specificAppPropertiesFilePath);
                    this.appProperties.load(new FileInputStream(this.specificAppPropertiesFilePath));
                } else {
                    File jarDir = getSpincastUtils().getAppJarDirectory();
                    if(jarDir != null) {
                        File appConfigFile = new File(jarDir.getAbsolutePath() + "/" + APP_PROPERTIES_FILE_NAME);
                        if(!appConfigFile.isFile()) {
                            this.logger.warn("No environment specific configuration file found. " +
                                             "Default configurations will be used! Was looking for : " +
                                             appConfigFile.getAbsolutePath());
                        } else {
                            this.logger.info("Environment specific configuration file found : " +
                                             appConfigFile.getAbsolutePath());
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

    @Override
    public String getEnvironmentName() {

        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_ENVIRONMENT_NAME);
        if(value != null) {
            return value;
        }
        return super.getEnvironmentName();
    }

    @Override
    public boolean isDebugEnabled() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_ENVIRONMENT_IS_DEBUG);
        if(value != null) {
            return Boolean.valueOf(value);
        }
        return super.isDebugEnabled();
    }

    @Override
    public String getServerHost() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_SERVER_HOST);
        if(value != null) {
            return value;
        }
        return super.getServerHost();
    }

    @Override
    public int getHttpServerPort() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTP_SERVER_PORT);
        if(value != null) {
            return Integer.valueOf(value);
        }
        return 44420;
    }

    @Override
    public int getHttpsServerPort() {

        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTPS_SERVER_PORT);
        if(value != null) {
            return Integer.valueOf(value);
        }

        return super.getHttpsServerPort();
    }

    @Override
    public String getHttpsKeyStorePath() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_PATH);
        if(value != null) {
            return value;
        }
        return super.getHttpsKeyStorePath();
    }

    @Override
    public String getHttpsKeyStoreType() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_TYPE);
        if(value != null) {
            return value;
        }
        return super.getHttpsKeyStoreType();
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_STOREPASS);
        if(value != null) {
            return value;
        }
        return super.getHttpsKeyStoreStorePass();
    }

    @Override
    public String getHttpsKeyStoreKeypass() {
        String value = getAppProperties().getProperty(APP_PROPERTIES_KEY_HTTPS_SERVER_KEYSTORE_KEYPASS);
        if(value != null) {
            return value;
        }
        return super.getHttpsKeyStoreStorePass();
    }

}

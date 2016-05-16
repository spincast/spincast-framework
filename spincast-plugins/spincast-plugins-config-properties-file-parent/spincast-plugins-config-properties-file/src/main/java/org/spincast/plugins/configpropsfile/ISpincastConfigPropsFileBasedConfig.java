package org.spincast.plugins.configpropsfile;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast .properties based config plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastConfigPropsFileBasedConfigDefault.class)
public interface ISpincastConfigPropsFileBasedConfig {

    public static final String APP_PROPERTIES_FILE_NAME_DEFAULT = "app.properties";

    /**
     * The position of the main argument that specifies
     * the path to the .properties file to use for the
     * configurations. The first argument is at position 
     * "1".
     * <p>
     * Disabled by default.
     * </p>
     * 
     * @return the position of the argument or &lt;= 0 to
     * disable this strategy.
     */
    public int getSpecificPathMainArgsPosition();

    /**
     * The name of the .properties file to search for next
     * to the .jar file of the application.
     * <p>
     * Defaults to "app.properties".
     * </p>
     * 
     * @return the name of the config file or null/empty
     * to disable this strategy.
     */
    public String getNextToJarConfigFileName();

}

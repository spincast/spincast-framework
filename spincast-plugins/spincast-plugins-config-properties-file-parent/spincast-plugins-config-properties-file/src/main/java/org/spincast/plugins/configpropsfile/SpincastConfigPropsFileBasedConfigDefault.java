package org.spincast.plugins.configpropsfile;

/**
 * Default configuration for the .properties based config plugin.
 */
public class SpincastConfigPropsFileBasedConfigDefault implements SpincastConfigPropsFileBasedConfig {

    @Override
    public int getSpecificPathMainArgsPosition() {

        //==========================================
        // Disabled by default
        //==========================================
        return -1;
    }

    @Override
    public String getNextToJarConfigFileName() {
        return APP_PROPERTIES_FILE_NAME_DEFAULT;
    }

}

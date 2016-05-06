package org.spincast.website;

import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfigDefault;

/**
 * Custom configurations for the .properties file based config plugin.
 */
public class AppConfigPropsFileBasedConfig extends SpincastConfigPropsFileBasedConfigDefault {

    @Override
    public int getSpecificPathMainArgsPosition() {

        //==========================================
        // Enable this stategy: if present, the first main
        // argument will be considered as the path to
        // the .properties configuration file to use.
        //==========================================
        return 1;
    }
}

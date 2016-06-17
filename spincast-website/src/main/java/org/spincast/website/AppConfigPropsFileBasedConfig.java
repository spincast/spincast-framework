package org.spincast.website;

import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfigDefault;

/**
 * Custom configurations for the .properties file based config plugin.
 */
public class AppConfigPropsFileBasedConfig extends SpincastConfigPropsFileBasedConfigDefault {

    @Override
    public int getSpecificPathMainArgsPosition() {

        //==========================================
        // Enable this stategy: if > 0, the 
        // argument at the specified position will be considered 
        // as the path to  the .properties configuration file to use.
        // The first argument is at position "1".
        //==========================================
        return 1;
    }
}

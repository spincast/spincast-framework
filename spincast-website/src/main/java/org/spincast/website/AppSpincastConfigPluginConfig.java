package org.spincast.website;

import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfigDefault;

/**
 * How are our configurations loaded?
 */
public class AppSpincastConfigPluginConfig extends SpincastConfigPluginConfigDefault implements SpincastConfigPluginConfig {

    /**
     * If a "config" system property has been used, use this
     * as the path to the "external configuration file".
     */
    @Override
    public String getExternalFilePath() {

        String path = System.getProperty("config");
        if (path != null) {
            return path;
        }

        return super.getExternalFilePath();
    }

}

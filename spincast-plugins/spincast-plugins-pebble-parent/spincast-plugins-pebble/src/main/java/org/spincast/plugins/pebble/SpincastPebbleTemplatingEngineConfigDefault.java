package org.spincast.plugins.pebble;

import org.spincast.core.config.SpincastConfig;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Default implementation for the configurations of 
 * the Pebble templating engine plugin.
 */
public class SpincastPebbleTemplatingEngineConfigDefault implements SpincastPebbleTemplatingEngineConfig {

    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastPebbleTemplatingEngineConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public Extension getExtension() {

        //==========================================
        // No extension by default.
        //==========================================
        return null;
    }

    @Override
    public int getTemplateCacheItemNbr() {

        if (getSpincastConfig().isDevelopmentMode()) {
            return 0;
        }

        //==========================================
        // Disable the caching of templates by default,
        // even not in debug mode.
        //==========================================
        return 0;
    }

    @Override
    public int getTagCacheTypeItemNbr() {

        if (getSpincastConfig().isDevelopmentMode()) {
            return 0;
        }

        return 200;
    }

    @Override
    public boolean isStrictVariablesEnabled() {
        return getSpincastConfig().isDevelopmentMode();
    }

    @Override
    public String getValidationMessagesTemplatePath() {
        return "/spincast/spincast-plugins-pebble/spincastPebbleExtension/validationMessagesTemplate.html";
    }

    @Override
    public String getValidationGroupMessagesTemplatePath() {
        return "/spincast/spincast-plugins-pebble/spincastPebbleExtension/validationGroupMessagesTemplate.html";
    }

}

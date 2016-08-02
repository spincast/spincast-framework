package org.spincast.plugins.pebble;

import org.spincast.core.config.ISpincastConfig;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Default implementation for the configurations of 
 * the Pebble templating engine plugin.
 */
public class SpincastPebbleTemplatingEngineConfigDefault implements ISpincastPebbleTemplatingEngineConfig {

    private final ISpincastConfig spincastConfig;

    @Inject
    public SpincastPebbleTemplatingEngineConfigDefault(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public Extension getExtension() {

        //==========================================
        // No extensions by default.
        //==========================================
        return null;
    }

    @Override
    public int getTemplateCacheItemNbr() {

        if(getSpincastConfig().isDebugEnabled()) {
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

        if(getSpincastConfig().isDebugEnabled()) {
            return 0;
        }

        return 200;
    }

    @Override
    public boolean isStrictVariablesEnabled() {
        return getSpincastConfig().isDebugEnabled();
    }

}

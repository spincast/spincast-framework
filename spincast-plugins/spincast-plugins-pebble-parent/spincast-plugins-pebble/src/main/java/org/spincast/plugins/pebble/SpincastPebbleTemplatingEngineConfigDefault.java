package org.spincast.plugins.pebble;

import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Default implementation for the configurations of 
 * the Pebble templating engine plugin.
 */
public class SpincastPebbleTemplatingEngineConfigDefault implements ISpincastPebbleTemplatingEngineConfig {

    @Override
    public Extension getExtension() {

        //==========================================
        // No extensions by default.
        //==========================================
        return null;
    }

}

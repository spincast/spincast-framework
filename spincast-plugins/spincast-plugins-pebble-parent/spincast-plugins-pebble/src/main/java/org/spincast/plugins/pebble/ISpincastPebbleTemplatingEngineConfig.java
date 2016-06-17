package org.spincast.plugins.pebble;

import com.google.inject.ImplementedBy;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Configurations for the Pebble templating engine plugin. jjj123
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastPebbleTemplatingEngineConfigDefault.class)
public interface ISpincastPebbleTemplatingEngineConfig {

    /**
     * Pebble extension to register: allows you to add custom
     * filters, functions, etc.
     */
    public Extension getExtension();

}

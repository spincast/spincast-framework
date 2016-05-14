package org.spincast.plugins.jacksonjson;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Json Manager plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastJsonManagerConfigDefault.class)
public interface ISpincastJsonManagerConfig {

    /**
     * The number of space to use as indentation for pretty print.
     * Defaults to 4.
     */
    public int getPrettyPrinterIndentationSpaceNumber();

    /**
     * The newline characters to use for pretty print.
     * Defaults to "\n".
     */
    public String getPrettyPrinterNewlineChars();

}

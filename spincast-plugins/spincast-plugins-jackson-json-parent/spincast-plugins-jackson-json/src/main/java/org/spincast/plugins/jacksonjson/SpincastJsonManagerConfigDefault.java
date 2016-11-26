package org.spincast.plugins.jacksonjson;

/**
 * Default configuration for the Spincast Json Manager plugin.
 */
public class SpincastJsonManagerConfigDefault implements SpincastJsonManagerConfig {

    @Override
    public int getPrettyPrinterIndentationSpaceNumber() {
        return 4;
    }

    @Override
    public String getPrettyPrinterNewlineChars() {
        return "\n";
    }

}

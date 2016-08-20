package org.spincast.plugins.jacksonjson;

/**
 * Default configuration for the Spincast Json Manager plugin.
 */
public class SpincastJsonManagerConfigDefault implements ISpincastJsonManagerConfig {

    @Override
    public int getPrettyPrinterIndentationSpaceNumber() {
        return 4;
    }

    @Override
    public String getPrettyPrinterNewlineChars() {
        return "\n";
    }

    @Override
    public int getMaxNumberOfFieldPathKeys() {
        return 100;
    }

    @Override
    public int getFieldPathKeyMaxLength() {
        return 512;
    }
}

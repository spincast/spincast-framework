package org.spincast.plugins.jacksonxml;

/**
 * Default configuration for the Spincast Xml Manager plugin.
 */
public class SpincastXmlManagerConfigDefault implements SpincastXmlManagerConfig {

    @Override
    public int getPrettyPrinterIndentationSpaceNumber() {
        return 4;
    }

    @Override
    public String getPrettyPrinterNewlineChars() {
        return "\n";
    }
}

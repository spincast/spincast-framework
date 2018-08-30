package org.spincast.plugins.crypto.config;

/**
 * Default configurations for Spincast Simple Crypto plugin.
 */
public class SpincastCryptoConfigDefault implements SpincastCryptoConfig {

    @Override
    public boolean removeJavaCryptoRestrictionsOnInit() {
        return true;
    }
}




package org.spincast.plugins.crypto.config;

/**
 * Default configurations for Spincast Crypto plugin.
 */
public class SpincastCryptoConfigDefault implements SpincastCryptoConfig {

    @Override
    public boolean removeJavaCryptoRestrictionsOnInit() {
        return true;
    }
}




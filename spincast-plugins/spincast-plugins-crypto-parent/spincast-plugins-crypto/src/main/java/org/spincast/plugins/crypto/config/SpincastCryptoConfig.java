package org.spincast.plugins.crypto.config;

/**
 * Configurations for the Spincast Crypto plugin.
 */
public interface SpincastCryptoConfig {

    /**
     * Tries to remove the Java crypt restrictions
     * when the application starts.
     * <p>
     * Defaults to true.
     * <p>
     * @see https://stackoverflow.com/questions/1179672/how-to-avoid-installing-unlimited-strength-jce-policy-files-when-deploying-an/22492582#22492582
     */
    public boolean removeJavaCryptoRestrictionsOnInit();

}

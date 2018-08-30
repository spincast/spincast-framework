package org.spincast.plugins.crypto;

import org.spincast.plugins.crypto.config.SpincastCryptoConfig;

/**
 * Spincast Crypto utils
 */
public interface SpincastCryptoUtils {

    /**
     * Encrypts a string, using the given secrte key and the 
     * <code>AES</code> algorithm. 
     * <p>
     * The generated encrypted payload
     * is a string which is also base64 encoded so it can be
     * used in urls, emails, etc.
     * <p>
     * You must use the associated {@link #decrypt(String, String)} method to decrypt
     * the resultind payload. 
     * <p>
     * <strong>IMPORTANT!</strong>: For this method to work,
     * JCE policy files may  be installed first, security must
     * be configured properly, or security policy must be
     * disabled using the {@link SpincastCryptoConfig#removeJavaCryptoRestrictionsOnInit()}
     * configuration.
     * <p>
     * <a href="https://stackoverflow.com/questions/1179672/how-to-avoid-installing-unlimited-strength-jce-policy-files-when-deploying-an">more info</a>
     */
    public String encrypt(String toEncrypt, String secretKey);

    /**
     * Decrypts a string that was encrypted using 
     * {@link #encrypt(String, String)}.
     * <p>
     * <strong>IMPORTANT!</strong>: For this method to work,
     * JCE policy files may  be installed first, security must
     * be configured properly, or security policy must be
     * disabled using the {@link SpincastCryptoConfig#removeJavaCryptoRestrictionsOnInit()}
     * configuration.
     * <p>
     * <a href="https://stackoverflow.com/questions/1179672/how-to-avoid-installing-unlimited-strength-jce-policy-files-when-deploying-an">more info</a>
     */
    public String decrypt(String payload, String secretKey);


    /**
     * Tries to remove the Java cryptography restrictions.
     * <p>
     * @see https://stackoverflow.com/a/22492582/843699
     * 
     * @throws an Exception is the restrictions can't be removed.
     */
    public void removeCryptographyRestrictions() throws RuntimeException;

    /**
     * Is the current Java version cryptography restricted?
     */
    public boolean isRestrictedCryptographyJavaVersion();
}

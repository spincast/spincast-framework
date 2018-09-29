package org.spincast.plugins.crypto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Key;
import java.security.MessageDigest;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.crypto.config.SpincastCryptoConfig;

import com.google.inject.Inject;

public class SpincastCryptoUtilsDefault implements SpincastCryptoUtils {

    protected final Logger logger = LoggerFactory.getLogger(SpincastCryptoUtilsDefault.class);

    private final SpincastCryptoConfig spincastCryptoConfig;

    @Inject
    public SpincastCryptoUtilsDefault(SpincastCryptoConfig spincastCryptoConfig) {
        this.spincastCryptoConfig = spincastCryptoConfig;
    }

    protected SpincastCryptoConfig getSpincastCryptoConfig() {
        return this.spincastCryptoConfig;
    }

    @Inject
    protected void init() {

        //==========================================
        // Removes crypto restrictions on app start?
        //==========================================
        if (getSpincastCryptoConfig().removeJavaCryptoRestrictionsOnInit()) {
            removeCryptographyRestrictions();
        }
    }

    @Override
    public String encrypt(String toEncrypt, String secretKey) {

        try {
            Key aesKey = new SecretKeySpec(buildKey(secretKey), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes("UTF-8"));

            //==========================================
            // Makes sure we use *getUrlEncoder()* so the
            // resulting string can be used on url, emails, etc.
            //==========================================
            String encryptedStr = Base64.getUrlEncoder().encodeToString(encrypted);

            return encryptedStr;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String decrypt(String encrypted, String secretKey) {

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encrypted);

            Key aesKey = new SecretKeySpec(buildKey(secretKey), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            String decrypted = new String(cipher.doFinal(decoded), "UTF-8");

            return decrypted;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected byte[] buildKey(String original) {
        try {
            return MessageDigest.getInstance("MD5").digest(original.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String hashSecure(String toHash, String salt) {
        return BCrypt.hashpw(toHash, salt);
    }

    @Override
    public String generateNewHashSecureSalt() {
        return BCrypt.gensalt();
    }

    /**
     * From https://stackoverflow.com/a/22492582/843699
     */
    @Override
    public void removeCryptographyRestrictions() {

        if (!isRestrictedCryptographyJavaVersion()) {
            this.logger.info("Cryptography restrictions removal not needed");
            return;
        }
        try {

            //==========================================
            // Does the following, but with reflection, in order to 
            // bypass access checks:
            //
            // JceSecurity.isRestricted = false;
            // JceSecurity.defaultPolicy.perms.clear();
            // JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
            //==========================================
            final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
            final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
            final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

            final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
            isRestrictedField.setAccessible(true);
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
            isRestrictedField.set(null, false);

            final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
            defaultPolicyField.setAccessible(true);
            final PermissionCollection defaultPolicy = (PermissionCollection)defaultPolicyField.get(null);

            final Field perms = cryptoPermissions.getDeclaredField("perms");
            perms.setAccessible(true);
            ((Map<?, ?>)perms.get(defaultPolicy)).clear();

            final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
            instance.setAccessible(true);
            defaultPolicy.add((Permission)instance.get(null));

            this.logger.info("Successfully removed cryptography restrictions");

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public boolean isRestrictedCryptographyJavaVersion() {

        // This matches Oracle Java 7 and 8, but not Java 9 or OpenJDK.
        final String name = System.getProperty("java.runtime.name");
        final String ver = System.getProperty("java.version");
        return name != null && name.equals("Java(TM) SE Runtime Environment") && ver != null &&
               (ver.startsWith("1.7") || ver.startsWith("1.8"));
    }



}

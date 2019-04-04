package org.spincast.core.utils.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;

public class SSLContextFactoryDefault implements SSLContextFactory {

    protected static final Logger logger = LoggerFactory.getLogger(SSLContextFactoryDefault.class);

    @Override
    public SSLContext createSSLContext(String keyStorePath,
                                       String keyStoreType,
                                       String keyStoreStorePass,
                                       String keyStoreKeypass) {
        try {

            Objects.requireNonNull(keyStorePath, "The key Store Path can't be NULL");
            Objects.requireNonNull(keyStoreType, "The key Store Type can't be NULL");
            Objects.requireNonNull(keyStoreStorePass, "The key Store Store password can't be NULL");
            Objects.requireNonNull(keyStoreKeypass, "The key Store Key password can't be NULL");

            KeyManager[] keyManagers = getKeyManagers(keyStorePath, keyStoreType, keyStoreStorePass, keyStoreKeypass);
            TrustManager[] trustManagers = getTrustManagers();

            return createSSLContext(keyManagers, trustManagers);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected SSLContext createSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);

            return sslContext;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected KeyStore getKeyStore(String keyStorePath, String keyStoreType, String keyStoreStorePass) {

        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            InputStream keyStoreInputStream = getKeyStoreInputStream(keyStorePath);
            try {
                keyStore.load(keyStoreInputStream, keyStoreStorePass.toCharArray());
            } finally {
                SpincastStatics.closeQuietly(keyStoreInputStream);
            }

            return keyStore;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected InputStream getKeyStoreInputStream(String keyStorePath) {

        try {
            keyStorePath = keyStorePath.trim();

            //==========================================
            // Check classpath
            //==========================================
            String keyStoreClassPath = keyStorePath;
            if (keyStoreClassPath.startsWith("/")) {
                keyStoreClassPath = keyStoreClassPath.substring(1);
            }
            InputStream keyStoreStream = this.getClass().getClassLoader().getResourceAsStream(keyStoreClassPath);
            if (keyStoreStream != null) {
                logger.info("KeyStore found in classpath : " + keyStoreClassPath);
                return keyStoreStream;
            }

            //==========================================
            // Check filesystem
            //==========================================
            File keyStoreFile = new File(keyStorePath);
            if (keyStoreFile.isFile()) {
                logger.info("KeyStore found in file system : " + keyStorePath);
                return new FileInputStream(keyStoreFile);
            }

            throw new RuntimeException("The specified KeyStore can't be found : " + keyStorePath);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected KeyManager[] getKeyManagers(String keyStorePath, String keyStoreType, String keyStoreStorePass,
                                          String keyStoreKeypass) {

        try {
            KeyStore keyStore = getKeyStore(keyStorePath, keyStoreType, keyStoreStorePass);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStoreKeypass.toCharArray());

            return keyManagerFactory.getKeyManagers();

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected TrustManager[] getTrustManagers() {

        try {
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore)null);
            return trustManagerFactory.getTrustManagers();

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}

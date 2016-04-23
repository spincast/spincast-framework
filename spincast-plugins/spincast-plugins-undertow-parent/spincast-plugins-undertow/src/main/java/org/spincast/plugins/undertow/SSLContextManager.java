package org.spincast.plugins.undertow;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

public class SSLContextManager implements ISSLContextManager {

    protected final Logger logger = LoggerFactory.getLogger(SSLContextManager.class);

    private final ISpincastConfig spincastConfig;

    @Inject
    public SSLContextManager(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public SSLContext getSSLContext() {
        try {

            KeyManager[] keyManagers = getKeyManagers();
            TrustManager[] trustManagers = getTrustManagers();

            return createSSLContext(keyManagers, trustManagers);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected SSLContext createSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);

            return sslContext;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected KeyStore getKeyStore() {

        try {
            String keyStoreType = getSpincastConfig().getHttpsKeyStoreType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            InputStream keyStoreInputStream = getKeyStoreInputStream();
            try {
                String keyStorePassword = getSpincastConfig().getHttpsKeyStoreStorePass();
                keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
            } finally {
                IOUtils.closeQuietly(keyStoreInputStream);
            }

            return keyStore;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected InputStream getKeyStoreInputStream() {

        try {
            String keyStorePath = getSpincastConfig().getHttpsKeyStorePath();
            if(keyStorePath == null) {
                throw new RuntimeException("To use HTTPS, the KeyStore path can't be empty!");
            }
            keyStorePath = keyStorePath.trim();

            //==========================================
            // Check classpath
            //==========================================
            String keyStoreClassPath = keyStorePath;
            if(keyStoreClassPath.startsWith("/")) {
                keyStoreClassPath = keyStoreClassPath.substring(1);
            }
            InputStream keyStoreStream = this.getClass().getClassLoader().getResourceAsStream(keyStoreClassPath);
            if(keyStoreStream != null) {
                this.logger.info("KeyStore found in classpath : " + keyStoreClassPath);
                return keyStoreStream;
            }

            //==========================================
            // Check filesystem
            //==========================================
            File keyStoreFile = new File(keyStorePath);
            if(keyStoreFile.isFile()) {
                this.logger.info("KeyStore found in file system : " + keyStorePath);
                return new FileInputStream(keyStoreFile);
            }

            throw new RuntimeException("The specified KeyStore can't be found : " + keyStorePath);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected KeyManager[] getKeyManagers() {

        try {
            KeyStore keyStore = getKeyStore();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, getSpincastConfig().getHttpsKeyStoreKeypass().toCharArray());

            return keyManagerFactory.getKeyManagers();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected TrustManager[] getTrustManagers() {

        try {
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore)null);
            return trustManagerFactory.getTrustManagers();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}

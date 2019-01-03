package org.spincast.testing.core.utils;

import org.spincast.core.dictionary.DictionaryEntryNotFoundBehavior;
import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;

import com.google.inject.Inject;

/**
 * Default configurations for Spincast integration 
 * testing.
 */
public class SpincastConfigTestingDefault extends SpincastConfigDefault {

    private int serverPort = -1;

    /**
     * Constructor
     */
    @Inject
    protected SpincastConfigTestingDefault(SpincastConfigPluginConfig spincastConfigPluginConfig,
                                           @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    /**
     * We do not run in "debug" mode.
     */
    @Override
    public boolean isDevelopmentMode() {
        return false;
    }

    @Override
    public String getServerHost() {
        return "localhost";
    }

    @Override
    public int getHttpServerPort() {
        return -1;
    }

    @Override
    public int getHttpsServerPort() {
        if (this.serverPort == -1) {

            //==========================================
            // We reserve 44440-44450 for applications
            // that may be started during tests are running.
            //==========================================
            do {
                this.serverPort = SpincastTestingUtils.findFreePort();
            } while (this.serverPort >= 44440 && this.serverPort <= 44450);
        }
        return this.serverPort;
    }

    @Override
    public String getPublicUrlBase() {
        return "https://" + getServerHost() + ":" + getHttpsServerPort();
    }

    @Override
    public String getHttpsKeyStorePath() {
        return "spincast/testing/certificates/devKeyStore.jks";
    }

    @Override
    public String getHttpsKeyStoreType() {
        return "JKS";
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        return "myStorePass";
    }

    @Override
    public String getHttpsKeyStoreKeyPass() {
        return "myKeyPass";
    }

    @Override
    public boolean isValidateLocalhostHost() {
        return false;
    }

    @Override
    public boolean isEnableCookiesValidator() {
        return false;
    }

    @Override
    public DictionaryEntryNotFoundBehavior getDictionaryEntryNotFoundBehavior() {
        return DictionaryEntryNotFoundBehavior.EXCEPTION;
    }
};

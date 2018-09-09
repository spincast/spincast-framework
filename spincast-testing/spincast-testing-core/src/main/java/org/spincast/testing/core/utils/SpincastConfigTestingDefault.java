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

    /**
     * We use a free port.
     */
    @Override
    public int getHttpServerPort() {

        if (this.serverPort == -1) {

            //==========================================
            // We reserve 44419 for the default configuration.
            //==========================================
            do {
                this.serverPort = SpincastTestUtils.findFreePort();
            } while (this.serverPort == 44419);
        }
        return this.serverPort;
    }

    @Override
    public String getPublicUrlBase() {
        return "http://" + getServerHost() + ":" + getHttpServerPort();
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

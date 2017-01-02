package org.spincast.testing.core.utils;

import org.spincast.core.config.SpincastConfig;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.testing.core.SpincastConfigTesting;
import org.spincast.testing.core.SpincastTestBase;

/**
 * Default configurations for Spincast integration 
 * testing.
 * <p>
 * {@link SpincastTestBase} will add an AOP interceptor that is going
 * to redirect calls made to the original {@link SpincastConfig} object
 * to this testing version.
 */
public class SpincastConfigTestingDefault extends SpincastConfigDefault
                                          implements SpincastConfigTesting {

    private int serverPort = -1;

    /**
     * We do not run in "debug" mode.
     */
    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isRoutesCaseSensitive() {
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

        if(this.serverPort == -1) {

            //==========================================
            // We reserve 44419 for the default configuration.
            //==========================================
            do {
                this.serverPort = SpincastTestUtils.findFreePort();
            } while(this.serverPort == 44419);
        }
        return this.serverPort;
    }

    @Override
    public String getPublicServerSchemeHostPort() {
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
};

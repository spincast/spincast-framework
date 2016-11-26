package org.spincast.testing.core;

import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.testing.core.utils.SpincastTestUtils;

/**
 * Default configurations for Spincast tests.
 */
public class SpincastTestConfig extends SpincastConfigDefault {

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
            this.serverPort = SpincastTestUtils.findFreePort();
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

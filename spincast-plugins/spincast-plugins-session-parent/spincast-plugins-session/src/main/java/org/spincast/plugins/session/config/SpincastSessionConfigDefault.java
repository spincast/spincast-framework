package org.spincast.plugins.session.config;

import org.spincast.plugins.session.SpincastSessionPlugin;

/**
 * Default configurations for Spincast Session plugin.
 */
public class SpincastSessionConfigDefault implements SpincastSessionConfig {

    @Override
    public int getDeleteOldSessionsCronRunEveryNbrMinutes() {
        return 30;
    }

    @Override
    public int getSessionMaxInactiveMinutes() {
        return 10080;
    }

    @Override
    public String getSessionIdCookieName() {
        return SpincastSessionPlugin.class.getName() + "_sessionId";
    }

    @Override
    public int getUpdateNotDirtySessionPeriodInSeconds() {

        //==========================================
        // By default, only update a not dirty session's
        // modification date once every 5 minutes.
        //==========================================
        return 300;
    }

}




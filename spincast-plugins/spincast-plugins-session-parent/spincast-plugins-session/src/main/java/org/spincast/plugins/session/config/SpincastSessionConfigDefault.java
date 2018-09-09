package org.spincast.plugins.session.config;

/**
 * Default configurations for Spincast Session plugin.
 */
public class SpincastSessionConfigDefault implements SpincastSessionConfig {

    @Override
    public int getDeleteOldSessionsScheduledTaskRunEveryNbrMinutes() {
        return 30;
    }

    @Override
    public int getSessionMaxInactiveMinutes() {
        return 10080;
    }

    @Override
    public String getSessionIdCookieName() {
        return "spincast_sid";
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



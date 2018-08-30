package org.spincast.plugins.session.config;

/**
 * Configurations for the Spincast Session plugin.
 */
public interface SpincastSessionConfig {

    /**
     * If {@link #autoRegisterCronJobToDeleteOldSessions()} is
     * enabled, this is the number of minutes before
     * an inactive session is deleted.
     * <p>
     * Defaults to 7 days.
     */
    public int getSessionMaxInactiveMinutes();

    /**
     * The cron to delete old sessions should run 
     * every X minutes.
     */
    public int getDeleteOldSessionsCronRunEveryNbrMinutes();

    /**
     * The name of the cookie used to store the
     * Session id, by default.
     */
    public String getSessionIdCookieName();

    /**
     * The number of seconds between two updates of the
     * modification date of a session, wheen the session
     * is not dirty.
     * <p>
     * This value must be less than
     * {@link #getSessionMaxInactiveMinutes()} or active
     * sessions will be deleted!
     * <p>
     * Note that when something changes on the session 
     * the session becomes dirty and is <em>always</em> 
     * saved and an updated modification date.
     */
    public int getUpdateNotDirtySessionPeriodInSeconds();

}

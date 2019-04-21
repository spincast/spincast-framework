package org.spincast.plugins.session.config;

import org.spincast.plugins.session.SpincastSessionRepository;

/**
 * Configurations for the Spincast Session plugin.
 */
public interface SpincastSessionConfig {

    /**
     * When a cookie is automatically added to a
     * visitor, should it be permanent (10 years)?
     * Otherwise, it will be browser-session long
     * only (the default).
     */
    public boolean isSessionPermanentByDefault();

    /**
     * The number of minutes before
     * an inactive session is deleted.
     * <p>
     * Defaults to 7 days.
     */
    public int getSessionMaxInactiveMinutes();

    /**
     * The acheduled task to delete old sessions should run
     * every X minutes.
     */
    public int getDeleteOldSessionsScheduledTaskRunEveryNbrMinutes();

    /**
     * The name of the cookie used to store the
     * Session id, by default.
     */
    public String getSessionIdCookieName();

    /**
     * When the default {@link SpincastSessionRepository} repository
     * is used (we suggest you bind a custom one, based on a database though!),
     * this will be the name of the cookie used to saved the session, on the client.
     */
    public String getDefaultCookieRepositoryCookieName();

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

    /**
     * Should the required <em>before</em> and <em>after</em>
     * filters be added automatically?
     * <p>
     * If <code>false</code>, you will have to add them by
     * yourself.
     */
    public boolean isAutoAddSessionFilters();

    /**
     * When the session filters are added automatically,
     * this would be the position of the <code>before</code>
     * filter.
     */
    public int getAutoAddedFilterBeforePosition();

    /**
     * When the session filters are added automatically,
     * this would be the position of the <code>after</code>
     * filter.
     */
    public int getAutoAddedFilterAfterPosition();



}

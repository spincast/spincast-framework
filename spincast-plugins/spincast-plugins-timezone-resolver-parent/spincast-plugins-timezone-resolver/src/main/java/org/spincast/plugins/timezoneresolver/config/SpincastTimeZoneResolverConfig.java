package org.spincast.plugins.timezoneresolver.config;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast TimeZone Resolver plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastTimeZoneResolverConfigDefault.class)
public interface SpincastTimeZoneResolverConfig {

    /**
     * Should the page be refreshed once the TimeZone
     * cookie has been set for the first time?
     * <p>
     * This allows you to obtain the timeZone of the user
     * even before rendering the very first page he visits.
     * Simply put <code>{{ timeZoneCookie(true) }}</code>
     * very high in the header of the page!
     * <p>
     * Defaults to <code>true</code>.
     */
    public boolean isRefreshPageAfterAddingPebbleTimeZoneCookie();

    /**
     * The domain to use for the TimeZone coookie.
     * Default to "." + the app domain, which makes
     * the cookie available to all sud domains.
     */
    public String getPebbleTimeZoneCookieDomain();

    /**
     * The path to use for the TimeZone coookie.
     * <p>
     * Defaults to "/".
     */
    public String getPebbleTimeZoneCookiePath();

    /**
     * The name of the querystring parameter that
     * will be used is reloading the page after the
     * timeZone cookie is set is enabled.
     * <p>
     * Defaults to "spincast_tz".
     */
    public String getPebbleTimeZoneCookieReloadingQsParamName();

    /**
     * The number of hours the timeZone cookie will
     * live.
     * <p>
     * Defaults to 10 years.
     */
    public int getPebbleTimeZoneCookieExpiredHoursNbr();


}

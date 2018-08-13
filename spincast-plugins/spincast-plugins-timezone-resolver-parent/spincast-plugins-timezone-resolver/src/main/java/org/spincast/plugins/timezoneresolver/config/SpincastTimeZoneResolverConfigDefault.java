package org.spincast.plugins.timezoneresolver.config;

import org.spincast.core.config.SpincastConfig;

import com.google.inject.Inject;

/**
 * Default configurations for Spincast TimeZone Resolver plugin.
 */
public class SpincastTimeZoneResolverConfigDefault implements SpincastTimeZoneResolverConfig {

    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastTimeZoneResolverConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public boolean isRefreshPageAfterAddingPebbleTimeZoneCookie() {
        return true;
    }

    @Override
    public String getPebbleTimeZoneCookieDomain() {
        //==========================================
        // By default, we prefix with a "." so the cookie
        // is vailable to all sub domains.
        //==========================================
        return "." + getSpincastConfig().getPublicServerHost();
    }

    @Override
    public String getPebbleTimeZoneCookiePath() {
        return "/";
    }

    @Override
    public String getPebbleTimeZoneCookieReloadingQsParamName() {
        return "spincast_tz";
    }

    @Override
    public int getPebbleTimeZoneCookieExpiredHoursNbr() {
        return 10 * 365 * 24;
    }
}




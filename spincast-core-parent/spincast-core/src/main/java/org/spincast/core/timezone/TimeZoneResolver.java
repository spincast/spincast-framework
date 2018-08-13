package org.spincast.core.timezone;

import java.util.TimeZone;

/**
 * Component which finds the best TimeZone to use.
 */
public interface TimeZoneResolver {

    /**
     * The best TimeZone to use.
     * 
     * @return The best TimeZone to use. If none is found, returns
     * the <em>default</em> TimeZone, as defined by 
     * {@link org.spincast.core.config.SpincastConfig#getDefaultTimeZone() SpincastConfig#getDefaultTimeZone()}
     */
    public TimeZone getTimeZoneToUse();
}

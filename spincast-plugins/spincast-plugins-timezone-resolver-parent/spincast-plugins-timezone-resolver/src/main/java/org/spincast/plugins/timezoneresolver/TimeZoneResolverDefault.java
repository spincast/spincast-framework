package org.spincast.plugins.timezoneresolver;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.timezone.TimeZoneResolver;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

public class TimeZoneResolverDefault implements TimeZoneResolver {

    private final SpincastConfig spincastConfig;
    private final Provider<RequestContext<?>> requestContextProvider;
    private final Set<String> validTimeZoneIds;
    private TimeZone defaultTimeZone;

    @Inject
    public TimeZoneResolverDefault(SpincastConfig spincastConfig,
                                   Provider<RequestContext<?>> requestContextProvider) {
        this.spincastConfig = spincastConfig;
        this.requestContextProvider = requestContextProvider;
        this.validTimeZoneIds = new HashSet<String>();
    }

    @Inject
    protected void init() {

        this.defaultTimeZone = getSpincastConfig().getDefaultTimeZone();
        if (this.defaultTimeZone == null) {
            this.defaultTimeZone = TimeZone.getTimeZone("UTC");
        }

        String[] availableIDs = TimeZone.getAvailableIDs();
        for (String availableID : availableIDs) {
            this.validTimeZoneIds.add(availableID);
        }
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
    }

    protected Set<String> getValidTimeZoneIds() {
        return this.validTimeZoneIds;
    }

    protected TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    @Override
    public TimeZone getTimeZoneToUse() {

        //==========================================
        // This can be called outside of a Request scope, for
        // example by a cron job. In that case, the default
        // TimeZone will be used.
        //==========================================
        try {
            RequestContext<?> context = getRequestContextProvider().get();

            //==========================================
            // TimeZone saved to a cookie?
            //==========================================
            String cookieValue = context.request().getCookie(getSpincastConfig().getCookieNameTimeZoneId());
            if (cookieValue != null && getValidTimeZoneIds().contains(cookieValue)) {
                return TimeZone.getTimeZone(cookieValue);
            }
        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, use the default TimeZone
        }

        return getDefaultTimeZone();
    }

}

package org.spincast.plugins.dateformatter;

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

    @Inject
    public TimeZoneResolverDefault(SpincastConfig spincastConfig,
                                   Provider<RequestContext<?>> requestContextProvider) {
        this.spincastConfig = spincastConfig;
        this.requestContextProvider = requestContextProvider;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
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
            if (cookieValue != null) {

                try {
                    TimeZone timeZone = TimeZone.getTimeZone(cookieValue);
                    return timeZone;
                } catch (Exception ex) {
                    context.response().deleteCookie(getSpincastConfig().getCookieNameTimeZoneId());
                    // ...
                }
            }
        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, use the default TimeZone
        }

        TimeZone timeZone = getSpincastConfig().getDefaultTimeZone();
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone("UTC");
        }
        return timeZone;
    }

}

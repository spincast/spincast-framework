package org.spincast.plugins.localeresolver;

import java.util.Locale;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.shaded.org.apache.commons.lang3.LocaleUtils;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

public class LocaleResolverDefault implements LocaleResolver {

    private final SpincastConfig spincastConfig;
    private final Provider<RequestContext<?>> requestContextProvider;

    @Inject
    public LocaleResolverDefault(SpincastConfig spincastConfig,
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

    /**
     * The default way to find the Locale to use is to check for
     * a cookie, or otherwise use the request "Accept-Language" header.
     */
    @Override
    public Locale getLocaleToUse() {

        //==========================================
        // This can be called outside of a Request scope, for
        // example by a scheduled task. In that case, the default
        // Locale will be used.
        //==========================================
        try {
            RequestContext<?> context = getRequestContextProvider().get();

            String cookieValue = context.request().getCookieValue(getSpincastConfig().getCookieNameLocale());
            if (cookieValue != null) {

                try {
                    Locale locale = LocaleUtils.toLocale(cookieValue);
                    return locale;
                } catch (Exception ex) {
                    context.response().deleteCookie(getSpincastConfig().getCookieNameLocale());
                    // ...
                }
            }

            Locale requestLocaleBestMatch = context.request().getLocaleBestMatch();
            if (requestLocaleBestMatch != null) {
                return requestLocaleBestMatch;
            }
        } catch (OutOfScopeException | ProvisionException ex) {
            // ok, use the default Locale
        }

        return getSpincastConfig().getDefaultLocale();
    }

}

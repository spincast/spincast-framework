package org.spincast.plugins.localeresolver;

import java.util.Locale;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.shaded.org.apache.commons.lang3.LocaleUtils;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

public class SpincastLocaleResolver implements ILocaleResolver {

    private final ISpincastConfig spincastConfig;
    private final Provider<IRequestContext<?>> requestContextProvider;

    @Inject
    public SpincastLocaleResolver(ISpincastConfig spincastConfig,
                                  Provider<IRequestContext<?>> requestContextProvider) {
        this.spincastConfig = spincastConfig;
        this.requestContextProvider = requestContextProvider;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Provider<IRequestContext<?>> getRequestContextProvider() {
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
        // example by a cron job. In that case, the default
        // Locale will be used.
        //==========================================
        try {
            IRequestContext<?> context = getRequestContextProvider().get();

            ICookie cookie = context.cookies().getCookie(SpincastConstants.COOKIE_NAME_LOCALE_TO_USE);
            if(cookie != null && !cookie.isExpired()) {

                try {
                    Locale locale = LocaleUtils.toLocale(cookie.getValue());
                    return locale;
                } catch(Exception ex) {
                    context.cookies().deleteCookie(SpincastConstants.COOKIE_NAME_LOCALE_TO_USE);
                    // ...
                }
            }

            Locale requestLocaleBestMatch = context.request().getLocaleBestMatch();
            if(requestLocaleBestMatch != null) {
                return requestLocaleBestMatch;
            }
        } catch(OutOfScopeException | ProvisionException ex) {
            // ok, use the default Locale
        }

        return getSpincastConfig().getDefaultLocale();
    }

}

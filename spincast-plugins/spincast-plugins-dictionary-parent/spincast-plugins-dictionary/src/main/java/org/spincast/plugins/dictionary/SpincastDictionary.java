package org.spincast.plugins.dictionary;

import java.util.Locale;

import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.locale.ILocaleResolver;

import com.google.inject.Inject;

public class SpincastDictionary implements ISpincastDictionary {

    private final ILocaleResolver localeResolver;

    @Inject
    public SpincastDictionary(ILocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    protected Locale getLocale() {
        return this.localeResolver.getLocaleToUse();
    }

    @Override
    public String route_notFound_default_message() {

        @SuppressWarnings("unused")
        Locale locale = getLocale();

        // No i18n for now...
        return "Not found";
    }

    @Override
    public String exception_default_message() {
        return "Internal Error";
    }

}

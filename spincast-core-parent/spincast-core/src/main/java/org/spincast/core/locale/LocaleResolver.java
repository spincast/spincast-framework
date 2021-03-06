package org.spincast.core.locale;

import java.util.Locale;

/**
 * Component which finds the best Locale to use, using a cookie, a
 * <code>Accept-Language</code> header or using a custom way.
 */
public interface LocaleResolver {

    /**
     * The best Locale to use.
     * 
     * @return The best Locale to use. If none is found, returns
     * the <em>default</em> Locale, as defined by {@link org.spincast.core.config.SpincastConfig#getDefaultLocale() SpincastConfig#getDefaultLocale()}
     */
    public Locale getLocaleToUse();
}

package org.spincast.core.dictionary;

import java.util.Locale;

public class DictionaryKeyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String key;
    private final Locale locale;

    public DictionaryKeyNotFoundException(String key, Locale locale) {
        super("Key \"" + key + "\" not found for Locale \"" + locale + "\"!");
        this.key = key;
        this.locale = locale;
    }

    public String getKey() {
        return this.key;
    }

    public Locale getLocale() {
        return this.locale;
    }
}

package org.spincast.plugins.dateformatter;

import java.util.Locale;

import org.spincast.core.locale.LocaleResolver;

public interface RelativeDateFormatter {

    /**
     * The locale to use.
     * Defaults to the one provided by the
     * {@link LocaleResolver}.
     */
    public RelativeDateFormatter locale(Locale locale);

    /**
     * The type of the format to use.
     * Defaults to {@link RelativeDateFormatType#DEFAULT}.
     */
    public RelativeDateFormatter formatType(RelativeDateFormatType formatType);

    /**
     * Format!
     */
    public String format();
}

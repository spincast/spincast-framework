package org.spincast.plugins.dateformatter;

import java.util.Locale;
import java.util.TimeZone;

import org.spincast.core.locale.LocaleResolver;

/**
 * Date formatter builder.
 * 
 */
public interface DateFormatter {

    /**
     * The locale to use.
     * Defaults to the one provided by the
     * {@link LocaleResolver}.
     */
    public DateFormatter locale(Locale locale);

    /**
     * The timeZone to use.
     */
    public DateFormatter timeZone(TimeZone timeZone);

    /**
     * Which part(s) of ther date to format and 
     * output?
     */
    public DateFormatter parts(DateParts dateParts);

    /**
     * The pattern to use for the *date* part (without
     * time).
     */
    public DateFormatter datePattern(DatePattern datePattern);

    /**
     * The custom pattern to use for the *date* part (without
     * time).
     */
    public DateFormatter datePattern(String customPattern);

    /**
     * The pattern to use for the *time* part (without
     * the date).
     */
    public DateFormatter timePattern(DatePattern timePattern);

    /**
     * The custom pattern to use for the *time* part (without
     * the date).
     */
    public DateFormatter timePattern(String customPattern);

    /**
     * The separator string to use between the date
     * part and the time part.
     * Defaults to " - ".
     */
    public DateFormatter separator(String separator);

    /**
     * Format!
     */
    public String format();

}



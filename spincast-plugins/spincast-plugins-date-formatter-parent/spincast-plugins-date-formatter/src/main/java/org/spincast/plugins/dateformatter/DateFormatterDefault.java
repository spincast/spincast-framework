package org.spincast.plugins.dateformatter;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.shaded.org.apache.commons.lang3.time.FastDateFormat;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class DateFormatterDefault implements DateFormatter {

    private final Date date;
    private final LocaleResolver localeResolver;
    private final TimeZoneResolver timeZoneResolver;

    private Locale locale;
    private TimeZone timeZone;
    private DateParts dateParts = null;
    private DatePattern datePattern = null;
    private String datePatternCustom = null;
    private DatePattern timePattern = null;
    private String timePatternCustom = null;
    private String separator = null;

    @AssistedInject
    public DateFormatterDefault(@Assisted Instant instant,
                                LocaleResolver localeResolver,
                                TimeZoneResolver timeZoneResolver) {
        this(Date.from(instant), localeResolver, timeZoneResolver);
    }

    @AssistedInject
    public DateFormatterDefault(@Assisted Date date,
                                LocaleResolver localeResolver,
                                TimeZoneResolver timeZoneResolver) {
        this.date = date;
        this.localeResolver = localeResolver;
        this.timeZoneResolver = timeZoneResolver;
    }

    @Override
    public DateFormatter locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public DateFormatter timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    @Override
    public DateFormatter parts(DateParts dateParts) {
        this.dateParts = dateParts;
        return this;
    }

    @Override
    public DateFormatter datePattern(DatePattern datePattern) {
        this.datePattern = datePattern;
        this.datePatternCustom = null;
        return this;
    }

    @Override
    public DateFormatter datePattern(String datePatternCustom) {
        this.datePatternCustom = datePatternCustom;
        this.datePattern = null;
        return this;
    }

    @Override
    public DateFormatter timePattern(DatePattern timePattern) {
        this.timePattern = timePattern;
        this.timePatternCustom = null;
        return this;
    }

    @Override
    public DateFormatter timePattern(String timePatternCustom) {
        this.timePatternCustom = timePatternCustom;
        this.timePattern = null;
        return this;
    }

    @Override
    public DateFormatter separator(String separator) {
        this.separator = separator;
        return this;
    }

    protected String getDefaultSeparator() {
        return " - ";
    }

    @Override
    public String format() {

        DateParts dateParts = this.dateParts;
        if (dateParts == null) {
            dateParts = DateParts.BOTH;
        }

        Locale locale = this.locale;
        if (locale == null) {
            locale = this.localeResolver.getLocaleToUse();
        }

        TimeZone timeZone = this.timeZone;
        if (timeZone == null) {
            timeZone = this.timeZoneResolver.getTimeZoneToUse();
        }

        StringBuilder formatted = new StringBuilder();

        //==========================================
        // Date
        //==========================================
        if (dateParts == DateParts.BOTH || dateParts == DateParts.DATE) {

            FastDateFormat formatter = null;
            if (this.datePatternCustom != null) {
                formatter = FastDateFormat.getInstance(this.datePatternCustom, timeZone, locale);
            } else {
                DatePattern datePattern = this.datePattern;
                if (datePattern == null) {
                    datePattern = DatePattern.FULL;
                }
                formatter = FastDateFormat.getDateInstance(datePattern.getPatternNbr(), timeZone, locale);
            }

            String dateFormatted = formatter.format(this.date);
            formatted.append(dateFormatted);
        }

        //==========================================
        // Time
        //==========================================
        if (dateParts == DateParts.BOTH || dateParts == DateParts.TIME) {

            //==========================================
            // Separator
            //==========================================
            if (dateParts == DateParts.BOTH) {
                formatted.append(this.separator != null ? this.separator : getDefaultSeparator());
            }

            FastDateFormat formatter = null;
            if (this.timePatternCustom != null) {
                formatter = FastDateFormat.getInstance(this.timePatternCustom, timeZone, locale);
            } else {
                DatePattern timePattern = this.timePattern;
                if (timePattern == null) {
                    timePattern = DatePattern.SHORT;
                }
                formatter = FastDateFormat.getTimeInstance(timePattern.getPatternNbr(), timeZone, locale);
            }

            String timeFormatted = formatter.format(this.date);
            formatted.append(timeFormatted);
        }

        return formatted.toString();
    }


}

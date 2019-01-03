package org.spincast.plugins.dateformatter;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class RelativeDateFormatterDefault implements RelativeDateFormatter {

    private final Date date;
    private final LocaleResolver localeResolver;

    private final Map<Locale, PrettyTime> prettyTimesByLocale;

    private Locale locale;
    private RelativeDateFormatType formatType;

    @AssistedInject
    public RelativeDateFormatterDefault(@Assisted Instant instant,
                                        LocaleResolver localeResolver) {
        this(Date.from(instant), localeResolver);
    }

    @AssistedInject
    public RelativeDateFormatterDefault(@Assisted Date date,
                                        LocaleResolver localeResolver) {
        this.date = date;
        this.localeResolver = localeResolver;
        this.prettyTimesByLocale = new HashMap<Locale, PrettyTime>();
    }

    protected Date getDate() {
        return this.date;
    }

    protected LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    protected Map<Locale, PrettyTime> getPrettyTimesByLocale() {
        return this.prettyTimesByLocale;
    }

    protected PrettyTime getPrettyTime(Locale locale) {

        Map<Locale, PrettyTime> map = getPrettyTimesByLocale();
        PrettyTime prettyTime = map.get(locale);
        if (prettyTime == null) {
            prettyTime = new PrettyTime(locale);
            map.put(locale, prettyTime);
        }
        return prettyTime;
    }

    @Override
    public RelativeDateFormatter locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public RelativeDateFormatter formatType(RelativeDateFormatType formatType) {
        this.formatType = formatType;
        return this;
    }

    @Override
    public String format() {

        Locale locale = this.locale;
        if (locale == null) {
            locale = getLocaleResolver().getLocaleToUse();
        }

        RelativeDateFormatType formatType = this.formatType;
        if (formatType == null) {
            formatType = RelativeDateFormatType.DEFAULT;
        }

        String formatted;
        if (formatType == RelativeDateFormatType.DEFAULT) {
            formatted = getPrettyTime(locale).format(getDate());
        } else if (formatType == RelativeDateFormatType.DURATION) {
            formatted = getPrettyTime(locale).formatDuration(getDate());

            //==========================================
            // PrettyTime will return an empty string
            // if the target date is very close to now!
            //==========================================
            if (StringUtils.isBlank(formatted)) {
                formatted = getAFewSecondsLabel();
            }
        } else if (formatType == RelativeDateFormatType.UNROUNDED) {
            formatted = getPrettyTime(locale).formatUnrounded(getDate());
        } else {
            throw new RuntimeException("Unmanaged relative format type : " + formatType);
        }

        return formatted;
    }

    protected String getAFewSecondsLabel() {
        return "a few seconds";
    }

}

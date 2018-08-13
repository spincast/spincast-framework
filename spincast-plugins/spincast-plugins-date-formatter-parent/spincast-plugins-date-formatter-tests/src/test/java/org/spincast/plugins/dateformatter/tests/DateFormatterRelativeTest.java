package org.spincast.plugins.dateformatter.tests;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.junit.Test;
import org.spincast.plugins.dateformatter.RelativeDateFormatType;
import org.spincast.plugins.dateformatter.RelativeDateFormatter;

public class DateFormatterRelativeTest extends DateFormatterTestBase {

    @Test
    public void yearsAgoDefault() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);

        String result = formatter.format();
        assertEquals("10 years ago", result);
    }

    @Test
    public void yearsAgoDuration() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);
        formatter.formatType(RelativeDateFormatType.DURATION);

        String result = formatter.format();
        assertEquals("10 years", result);
    }

    @Test
    public void yearsAgoUnrounded() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);
        formatter.formatType(RelativeDateFormatType.UNROUNDED);

        String result = formatter.format();
        assertEquals("9 years ago", result);
    }

    @Test
    public void specificLocale() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);
        formatter.locale(Locale.CANADA_FRENCH);

        String result = formatter.format();
        assertEquals("il y a 10 ans", result);
    }

    @Test
    public void test10HoursAgo() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10, ChronoUnit.HOURS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);

        String result = formatter.format();
        assertEquals("10 hours ago", result);
    }

    @Test
    public void test10HourswAgo() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10, ChronoUnit.HOURS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);


        String result = formatter.format();
        assertEquals("10 hours ago", result);
    }

    @Test
    public void yearsIn() throws Exception {

        Instant date = Instant.now();
        date = date.plus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);

        String result = formatter.format();
        assertEquals("10 years from now", result);
    }

    @Test
    public void yearsInSpecificLocale() throws Exception {

        Instant date = Instant.now();
        date = date.plus(10 * 365, ChronoUnit.DAYS);

        RelativeDateFormatter formatter = getDateFormatterFactory().createRelativeFormatter(date);
        formatter.locale(Locale.CANADA_FRENCH);

        String result = formatter.format();
        assertEquals("dans 10 ans", result);
    }

}

package org.spincast.plugins.dateformatter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.plugins.dateformatter.DateFormatter;
import org.spincast.plugins.dateformatter.DateParts;
import org.spincast.plugins.dateformatter.DatePattern;

public class DateFormatterTest extends DateFormatterTestBase {

    @Test
    public void defaultTest() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);

        String result = formatter.format();
        assertEquals("2007-12-03 - 10:15:30", result);
    }

    @Test
    public void changeSeparator() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.separator("~~~");

        String result = formatter.format();
        assertEquals("2007-12-03~~~10:15:30", result);
    }

    @Test
    public void datePartBothExplicit() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.BOTH);

        String result = formatter.format();
        assertEquals("2007-12-03 - 10:15:30", result);
    }

    @Test
    public void datePartOnly() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.DATE);

        String result = formatter.format();
        assertEquals("2007-12-03", result);
    }

    @Test
    public void datePartOnlyShort() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.DATE).datePattern(DatePattern.SHORT);

        String result = formatter.format();
        assertEquals("12/3/07", result);
    }

    @Test
    public void datePartOnlyCustom() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.DATE).datePattern("YYYY");

        String result = formatter.format();
        assertEquals("2007", result);
    }

    @Test
    public void datePartOnlyCustomInvalid() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.DATE).datePattern("q");

        try {
            formatter.format();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void timePartOnly() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.TIME);

        String result = formatter.format();
        assertEquals("10:15:30", result);
    }

    @Test
    public void timePartOnlyLong() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.TIME).timePattern(DatePattern.LONG);

        String result = formatter.format();
        assertEquals("10:15:30 AM UTC", result);
    }

    @Test
    public void timePartOnlyCustom() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.TIME).timePattern("HH");

        String result = formatter.format();
        assertEquals("10", result);
    }

    @Test
    public void timePartOnlyCustomInvalid() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.parts(DateParts.TIME).timePattern("q");

        try {
            formatter.format();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void bothCustom() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.datePattern("YYYY").timePattern("HH").separator(".");

        String result = formatter.format();
        assertEquals("2007.10", result);
    }

    @Test
    public void customLocale() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.locale(Locale.CANADA_FRENCH);

        String result = formatter.format();
        assertEquals("2007-12-03 - 10:15:30", result);
    }

    @Test
    public void customTimeZone() throws Exception {
        Instant date = Instant.parse("2007-12-03T10:15:30.00Z");

        DateFormatter formatter = getDateFormatterFactory().createFormatter(date);
        formatter.timeZone(TimeZone.getTimeZone("GMT-1:00"));

        String result = formatter.format();
        assertEquals("2007-12-03 - 09:15:30", result);
    }


}

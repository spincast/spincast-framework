package org.spincast.plugins.dateformatter.tests;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.spincast.core.json.JsonObject;

public class PebbleExtensionTest extends DateFormatterTestBase {

    @Test
    public void dateFormatDefault() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03 - 10:15:30");
    }

    @Test
    public void dateFormatDatePartOnlyFull() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('full', '') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "Monday, December 3, 2007");
    }

    @Test
    public void dateFormatDatePartOnlyShort() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('short', '') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "12/3/07");
    }

    @Test
    public void dateFormatDatePartOnlyCustom() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('YYYY', '') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007");
    }

    @Test
    public void dateFormatDatePartOnlyCustomInvalid() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('q', '') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals("2007-12-03T10:15:30Z", result);
    }

    @Test
    public void dateFormatTimePartOnlyShort() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('', 'short') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10:15 AM");
    }

    @Test
    public void dateFormatTimePartOnlyLong() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('', 'long') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10:15:30 AM UTC");
    }

    @Test
    public void dateFormatTimePartOnlyCustom() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('', 'HH') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10");
    }

    @Test
    public void dateFormatTimePartOnlyCustomInvalid() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('', 'q') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03T10:15:30Z");
    }

    @Test
    public void dateFormatFullFull() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('full', 'full') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "Monday, December 3, 2007 - 10:15:30 AM Coordinated Universal Time");
    }

    @Test
    public void iso() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('iso', 'iso') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03 - 10:15:30");
    }

    @Test
    public void dateFormatUnderescopeMeansDefault() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('_', 'full') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03 - 10:15:30 AM Coordinated Universal Time");
    }

    @Test
    public void dateFormatUnderscoreMeansDefault2() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('short', '_') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "12/3/07 - 10:15:30");
    }

    @Test
    public void dateFormatUnderescopeMeansDefault3() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('_', '_') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03 - 10:15:30");
    }

    @Test
    public void dateFormatChangeSeparator() throws Exception {

        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject model = getJsonManager().create();
        model.set("now", now);

        String content = "{{now | dateFormat('_', '_', ' * ') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "2007-12-03 * 10:15:30");
    }

    @Test
    public void relativeDefault() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        JsonObject model = getJsonManager().create();
        model.set("date", date);

        String content = "{{date | dateFormat('relative') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10 years ago");
    }

    @Test
    public void relativeDefaultExplicit() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        JsonObject model = getJsonManager().create();
        model.set("date", date);

        String content = "{{date | dateFormat('relative', 'default') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10 years ago");
    }

    @Test
    public void relativeDuration() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        JsonObject model = getJsonManager().create();
        model.set("date", date);

        String content = "{{date | dateFormat('relative', 'duration') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10 years");
    }

    @Test
    public void relativeUnrounded() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        JsonObject model = getJsonManager().create();
        model.set("date", date);

        String content = "{{date | dateFormat('relative', 'unrounded') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "9 years ago");
    }

    @Test
    public void relativeAsString() throws Exception {

        Instant date = Instant.now();
        date = date.minus(10 * 365, ChronoUnit.DAYS);

        JsonObject model = getJsonManager().create();
        model.set("date", date.toString());

        String content = "{{date | dateFormat('relative') }}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "10 years ago");
    }

}

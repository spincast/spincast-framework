package org.spincast.plugins.timezoneresolver.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.google.inject.Inject;

public class PebbleExtensionDefaultTest extends NoAppTestingBase {

    @Inject
    protected TimeZoneResolver timeZoneResolver;

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected TemplatingEngine templatingEngine;

    protected TimeZoneResolver getTimeZoneResolver() {
        return this.timeZoneResolver;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Test
    public void defaultTest() throws Exception {

        String content = "{{ timeZoneCookie() }}";
        String result = getTemplatingEngine().evaluate(content);
        assertTrue(result.indexOf("<script>") > -1);
        assertTrue(result.indexOf("document.cookie = \"spincast_timezone=") > -1);
        assertTrue(result.indexOf("domain=." + getSpincastConfig().getPublicServerHost() + ";") > -1);
        assertTrue(result.indexOf("path=/;") > -1);
        assertTrue(result.indexOf("var part = 'spincast_tz=1';") > -1);
        assertTrue(result.indexOf("window.location.href = url;") > -1);
        assertTrue(result.indexOf("</script>") > -1);
    }

    @Test
    public void withScriptTagsExplicit() throws Exception {

        String content = "{{ timeZoneCookie(true) }}";
        String result = getTemplatingEngine().evaluate(content);
        assertTrue(result.indexOf("<script>") > -1);
        assertTrue(result.indexOf("document.cookie = \"spincast_timezone=") > -1);
        assertTrue(result.indexOf("domain=." + getSpincastConfig().getPublicServerHost() + ";") > -1);
        assertTrue(result.indexOf("path=/;") > -1);
        assertTrue(result.indexOf("var part = 'spincast_tz=1';") > -1);
        assertTrue(result.indexOf("window.location.href = url;") > -1);
        assertTrue(result.indexOf("</script>") > -1);
    }

    @Test
    public void withoutScriptTags() throws Exception {

        String content = "{{ timeZoneCookie(false) }}";
        String result = getTemplatingEngine().evaluate(content);
        assertTrue(result.indexOf("<script>") == -1);
        assertTrue(result.indexOf("document.cookie = \"spincast_timezone=") > -1);
        assertTrue(result.indexOf("domain=." + getSpincastConfig().getPublicServerHost() + ";") > -1);
        assertTrue(result.indexOf("path=/;") > -1);
        assertTrue(result.indexOf("var part = 'spincast_tz=1';") > -1);
        assertTrue(result.indexOf("window.location.href = url;") > -1);
        assertTrue(result.indexOf("</script>") == -1);
    }


}

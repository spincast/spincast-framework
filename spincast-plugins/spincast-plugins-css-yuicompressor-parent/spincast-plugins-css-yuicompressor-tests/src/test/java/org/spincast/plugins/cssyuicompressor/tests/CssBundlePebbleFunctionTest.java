package org.spincast.plugins.cssyuicompressor.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.spincast.plugins.cssyuicompressor.tests.utils.CssYuiCompressorTestBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class CssBundlePebbleFunctionTest extends CssYuiCompressorTestBase {

    @Override
    public void beforeTest() {
        super.beforeTest();

        //==========================================
        // Clear cache
        //==========================================
        getServer().removeAllStaticResourcesServed();
        getSpincastUtils().clearDirectory(getSpincastCssYuiCompressorConfig().getCssBundlesDir());
    }

    protected static final Pattern bundleLinkPattern =
            Pattern.compile("^<link rel=\"stylesheet\" href=\"(.+)\">.*", Pattern.DOTALL);

    protected String validateAndExtractBundlePath(String output) {
        assertNotNull(output);
        Matcher m = bundleLinkPattern.matcher(output);
        assertTrue(m.matches());
        String path = m.group(1);
        assertFalse(StringUtils.isBlank(path));
        assertTrue(path.startsWith("/test-css-bundles/"));
        return path;
    }

    @Test
    public void bundle() throws Exception {

        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/b.css') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);
        assertTrue(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedCss = response.getContentAsString();
        assertEquals("#test1{width:100px}#test2{width:100px}", optimizedCss);
    }

    @Test
    public void bundleWithLineBreakPos() throws Exception {
        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/b.css', '--line-break-pos', '10') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);
        assertTrue(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedCss = response.getContentAsString();
        assertEquals("#test1{width:100px}\n#test2{width:100px}", optimizedCss);
    }

    @Test
    public void bundleWithLineBreakPosInvalidNumber() throws Exception {
        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/b.css', '--line-break-pos', 'nope') }}";

        try {
            getTemplatingEngine().evaluate(html);
            fail();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    @Test
    public void fileNotFound() throws Exception {
        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/nope.css') }}";

        try {
            getTemplatingEngine().evaluate(html);
            fail();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Test
    public void disableCacheBusting() throws Exception {

        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/b.css', '--spincast-no-cache-busting') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);
        assertFalse(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedCss = response.getContentAsString();
        assertEquals("#test1{width:100px}#test2{width:100px}", optimizedCss);
    }

}



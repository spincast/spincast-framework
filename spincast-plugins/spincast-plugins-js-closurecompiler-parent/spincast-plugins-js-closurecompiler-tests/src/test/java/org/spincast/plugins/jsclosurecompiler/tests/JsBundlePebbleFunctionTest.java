package org.spincast.plugins.jsclosurecompiler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPebbleExtensionDefault;
import org.spincast.plugins.jsclosurecompiler.tests.utils.JsClosureCompileTestBase;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class JsBundlePebbleFunctionTest extends JsClosureCompileTestBase {

    @Override
    public void beforeTest() {
        super.beforeTest();

        //==========================================
        // Clear cache
        //==========================================
        getServer().removeAllStaticResourcesServed();
        getSpincastUtils().clearDirectory(getSpincastJsClosureCompilerConfig().getJsBundlesDir());
    }

    protected static final Pattern bundleLinkPattern =
            Pattern.compile("^<script src=\"(.+)\"></script>.*", Pattern.DOTALL);

    protected String validateAndExtractBundlePath(String output) {
        assertNotNull(output);
        Matcher m = bundleLinkPattern.matcher(output);
        assertTrue(m.matches());
        String path = m.group(1);
        assertFalse(StringUtils.isBlank(path));
        assertTrue(path.startsWith("/test-js-bundles/"));
        return path;
    }

    @Test
    public void bundle() throws Exception {

        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/b.js') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);
        assertTrue(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedJs = response.getContentAsString();
        assertEquals("function titi(a,b,c){return a+b}function toto(a,b,c){return a+b};\n", optimizedJs);
    }

    @Test
    public void bundleWithArgs() throws Exception {
        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/b.js', '--compilation_level', 'WHITESPACE_ONLY') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedJs = response.getContentAsString();
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb}function toto(aaa,bbb,ccc){return aaa+bbb};\n", optimizedJs);
    }

    @Test
    public void fileNotFound() throws Exception {
        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/nope.js') }}";

        try {
            getTemplatingEngine().evaluate(html);
            fail();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Test
    public void disableCacheBusting() throws Exception {

        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/b.js', '" +
                      SpincastJsClosureCompilerPebbleExtensionDefault.JS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING + "') }}";

        String output = getTemplatingEngine().evaluate(html);
        String bundleLink = validateAndExtractBundlePath(output);
        assertFalse(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedJs = response.getContentAsString();
        assertEquals("function titi(a,b,c){return a+b}function toto(a,b,c){return a+b};\n", optimizedJs);
    }

}



package org.spincast.plugins.jsclosurecompiler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPebbleExtension;
import org.spincast.plugins.jsclosurecompiler.tests.utils.JsClosureCompileTestBase;
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

    @Test
    public void bundle() throws Exception {

        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/b.js') }}";

        String bundleLink = getTemplatingEngine().evaluate(html);
        assertNotNull(bundleLink);
        assertTrue(bundleLink.startsWith("/test-js-bundles/"));
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

        String bundleLink = getTemplatingEngine().evaluate(html);
        assertNotNull(bundleLink);
        assertTrue(bundleLink.startsWith("/test-js-bundles/"));

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
                      SpincastJsClosureCompilerPebbleExtension.JS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING + "') }}";

        String bundleLink = getTemplatingEngine().evaluate(html);
        assertNotNull(bundleLink);
        assertTrue(bundleLink.startsWith("/test-js-bundles/"));
        assertFalse(bundleLink.contains(getSpincastUtils().getCacheBusterCode()));

        HttpResponse response = GET(bundleLink).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        String optimizedJs = response.getContentAsString();
        assertEquals("function titi(a,b,c){return a+b}function toto(a,b,c){return a+b};\n", optimizedJs);
    }

}



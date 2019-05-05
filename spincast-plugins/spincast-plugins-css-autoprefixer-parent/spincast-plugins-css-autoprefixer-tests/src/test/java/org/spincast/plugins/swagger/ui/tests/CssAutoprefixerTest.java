package org.spincast.plugins.swagger.ui.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.swagger.ui.tests.utils.CssAutoprefixerTestBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

public class CssAutoprefixerTest extends CssAutoprefixerTestBase {

    @Test
    public void autoPrefixString() throws Exception {

        String css = "::placeholder {" +
                     "    color: lime;" +
                     "}";

        css = getSpincastCssAutoprefixerManager().autoPrefix(css);
        assertNotNull(css);
        assertTrue(css.contains("::-webkit-"));
    }

    @Test
    public void autoPrefixFile() throws Exception {

        String css = "::placeholder {" +
                     "    color: pink;" +
                     "}";

        File tmpFile = new File(createTestingFilePath());
        FileUtils.writeStringToFile(tmpFile, css, "UTF-8");

        getSpincastCssAutoprefixerManager().autoPrefix(tmpFile);

        css = FileUtils.readFileToString(tmpFile, "UTF-8");
        assertNotNull(css);
        assertTrue(css.contains("::-webkit-"));
    }

    @Test
    public void options() throws Exception {

        // @formatter:off
        String css = "@supports (width: fit-content) {" +
                     "    .menu {" +
                     "        width: 100px;" +
                     "    }" +
                     "}";
        // @formatter:on

        //==========================================
        // No options
        //==========================================
        String result = getSpincastCssAutoprefixerManager().autoPrefix(css);
        assertNotNull(result);
        assertTrue(result.contains("-webkit-fit-content"));

        //==========================================
        // supports - false
        //==========================================
        JsonObject options = getJsonManager().create();
        options.set("supports", false);

        result = getSpincastCssAutoprefixerManager().autoPrefix(css, options);
        assertNotNull(result);
        assertFalse(result.contains("-webkit-fit-content"));

        //==========================================
        // supports - true
        //==========================================
        options.set("supports", true);

        result = getSpincastCssAutoprefixerManager().autoPrefix(css, options);
        assertNotNull(result);
        assertTrue(result.contains("-webkit-fit-content"));
    }

    @Test
    public void gridDefault() throws Exception {

        // @formatter:off
        String css = ".autoplacement-example {" +
                          "display: grid;" +
                          "grid-template-columns: 1fr 1fr;" +
                          "grid-template-rows: auto auto;" +
                          "grid-gap: 20px;" +
                      "}";
        // @formatter:on

        css = getSpincastCssAutoprefixerManager().autoPrefix(css);
        assertNotNull(css);
        assertFalse(css.contains("-ms-grid"));
    }

    @Test
    public void gridAutoplace() throws Exception {

        // @formatter:off
        String css = ".autoplacement-example {" +
                          "display: grid;" +
                          "grid-template-columns: 1fr 1fr;" +
                          "grid-template-rows: auto auto;" +
                          "grid-gap: 20px;" +
                      "}";
        // @formatter:on

        JsonObject options = getJsonManager().create();
        options.set("grid", "autoplace");

        css = getSpincastCssAutoprefixerManager().autoPrefix(css, options);
        assertNotNull(css);
        assertTrue(css.contains("-ms-grid"));
    }

}



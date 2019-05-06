package org.spincast.plugins.cssyuicompressor.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.spincast.plugins.cssyuicompressor.tests.utils.CssYuiCompressorTestBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

public class CssYuiCompressorTest extends CssYuiCompressorTestBase {

    @Test
    public void fromString() throws Exception {

        // @formatter:off
        String css = "#test { \n" +
                     "    width: 100px;\n" +
                     "}\n" +
                     " ";
        // @formatter:on

        String minified = getSpincastCssYuiCompressorManager().minify(css);
        assertEquals("#test{width:100px}", minified);
    }

    @Test
    public void fromFile() throws Exception {

        // @formatter:off
        String css = "#test { \n" +
                     "    width: 100px;\n" +
                     "}\n" +
                     "#test2 { \n" +
                     "    width: 100px;\n" +
                     "}\n" +
                     " ";
        // @formatter:on

        File tmpFile = new File(createTestingFilePath());
        FileUtils.writeStringToFile(tmpFile, css, "UTF-8");

        getSpincastCssYuiCompressorManager().minify(tmpFile);

        String minified = FileUtils.readFileToString(tmpFile, "UTF-8");
        assertEquals("#test{width:100px}#test2{width:100px}", minified);
    }

    @Test
    public void maxLineLength() throws Exception {

        // @formatter:off
        String css = "#test { \n" +
                     "    width: 100px;\n" +
                     "}\n" +
                     "#test2 { \n" +
                     "    width: 100px;\n" +
                     "}\n" +
                     " ";
        // @formatter:on

        String minified = getSpincastCssYuiCompressorManager().minify(css, 10);
        assertEquals("#test{width:100px}\n#test2{width:100px}", minified);
    }

}



package org.spincast.plugins.cssyuicompressor;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.yahoo.platform.yui.compressor.CssCompressor;

public class SpincastCssYuiCompressorManagerDefault implements SpincastCssYuiCompressorManager {

    @Override
    public void minify(File cssFile) {
        minify(cssFile, StandardCharsets.UTF_8, -1);
    }

    @Override
    public void minify(File cssFile, int lineBreakPos) {
        minify(cssFile, StandardCharsets.UTF_8, lineBreakPos);
    }

    @Override
    public void minify(File cssFile, Charset encoding) {
        minify(cssFile, encoding, -1);
    }

    @Override
    public void minify(File cssFile, Charset encoding, int lineBreakPos) {
        if (!cssFile.isFile()) {
            throw new RuntimeException("The specified file doesn't exit: " + cssFile.getAbsolutePath());
        }

        try {
            String content = FileUtils.readFileToString(cssFile, encoding);
            content = minify(content, lineBreakPos);
            FileUtils.writeStringToFile(cssFile, content, encoding);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String minify(String cssContent) {
        return minify(cssContent, -1);
    }

    @Override
    public String minify(String cssContent, int lineBreakPos) {

        if (lineBreakPos <= 0) {
            lineBreakPos = -1;
        }

        if (StringUtils.isBlank(cssContent)) {
            return "";
        }

        try {

            StringReader cssContentReader = new StringReader(cssContent);
            CssCompressor compressor = new CssCompressor(cssContentReader);

            StringWriter stringWriter = new StringWriter();
            compressor.compress(stringWriter, lineBreakPos);
            cssContent = stringWriter.toString();

            return cssContent;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}


package org.spincast.plugins.cssyuicompressor;

import java.io.File;
import java.nio.charset.Charset;

public interface SpincastCssYuiCompressorManager {

    /**
     * Minify the specified CSS String.
     */
    public String minify(String cssContent);

    /**
     * Minify the specified CSS String.
     *
     * @param lineBreakPos From the
     * YUI Compressor <a href="https://yui.github.io/yuicompressor/#using">documentation</a>:
     * <pre>
     * Some source control tools don't like files containing lines longer than,
     * say 8000 characters. The linebreak option is used in that case to split
     * long lines after a specific column. It can also be used to make the code
     * more readable, easier to debug (especially with the MS Script Debugger)
     * Specify 0 to get a line break after each semi-colon in JavaScript, and
     * after each rule in CSS.
     * </pre>
     */
    public String minify(String cssContent, int lineBreakPos);

    /**
     * Minify the specified CSS file.
     * <p>
     * Use <code>UTF-8</code> as the encoding.
     */
    public void minify(File cssFile);

    /**
     * Minify the specified CSS file.
     * <p>
     * Use <code>UTF-8</code> as the encoding.
     *
     * @param lineBreakPos From the
     * YUI Compressor <a href="https://yui.github.io/yuicompressor/#using">documentation</a>:
     * <pre>
     * Some source control tools don't like files containing lines longer than,
     * say 8000 characters. The linebreak option is used in that case to split
     * long lines after a specific column. It can also be used to make the code
     * more readable, easier to debug (especially with the MS Script Debugger)
     * Specify 0 to get a line break after each semi-colon in JavaScript, and
     * after each rule in CSS.
     * </pre>
     */
    public void minify(File cssFile, int lineBreakPos);

    /**
     * Minify the specified CSS file.
     */
    public void minify(File cssFile, Charset encoding);

    /**
     * Minify the specified CSS file.
     *
     * @param lineBreakPos From the
     * YUI Compressor <a href="https://yui.github.io/yuicompressor/#using">documentation</a>:
     * <pre>
     * Some source control tools don't like files containing lines longer than,
     * say 8000 characters. The linebreak option is used in that case to split
     * long lines after a specific column. It can also be used to make the code
     * more readable, easier to debug (especially with the MS Script Debugger)
     * Specify 0 to get a line break after each semi-colon in JavaScript, and
     * after each rule in CSS.
     * </pre>
     */
    public void minify(File cssFile, Charset encoding, int lineBreakPos);

}

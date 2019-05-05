package org.spincast.plugins.cssautoprefixer;

import java.io.File;
import java.nio.charset.Charset;

import org.spincast.core.json.JsonObject;

public interface SpincastCssAutoprefixerManager {

    /**
     * Return <code>true</code> if Autoprefixer can be
     * run properly.
     * <p>
     * Read the <a href="https://www.spincast.org/plugins/spincast-css-autoprefixer#installation">documentation</a>
     * in order to learn how to install the requirements.
     */
    public boolean isValidAutoprefixerEnvironment();

    /**
     * Apply Autoprefixer on the specified
     * CSS String.
     */
    public String autoPrefix(String cssContent);

    /**
     * Apply Autoprefixer on the specified
     * CSS String.
     *
     * @param options The
     * <a href="https://github.com/postcss/autoprefixer#options">Autoprefixer options</a> to use.
     */
    public String autoPrefix(String cssContent, JsonObject options);

    /**
     * Apply Autoprefixer on the specified
     * CSS file.
     * <p>
     * Use <code>UTF-8</code> as the encoding.
     *
     */
    public void autoPrefix(File cssFile);

    /**
     * Apply Autoprefixer on the specified
     * CSS file.
     * <p>
     * Use <code>UTF-8</code> as the encoding.
     *
     * @param options The
     * <a href="https://github.com/postcss/autoprefixer#options">Autoprefixer options</a> to use.
     */
    public void autoPrefix(File cssFile, JsonObject options);

    /**
     * Apply Autoprefixer on the specified
     * CSS file.
     */
    public void autoPrefix(File cssFile, Charset encoding);

    /**
     * Apply Autoprefixer on the specified
     * CSS file.
     *
     * @param options The
     * <a href="https://github.com/postcss/autoprefixer#options">Autoprefixer options</a> to use.
     */
    public void autoPrefix(File cssFile, Charset encoding, JsonObject options);

}

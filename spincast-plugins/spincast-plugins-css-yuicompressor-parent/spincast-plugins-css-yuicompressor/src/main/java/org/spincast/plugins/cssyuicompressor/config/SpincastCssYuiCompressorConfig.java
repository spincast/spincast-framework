package org.spincast.plugins.cssyuicompressor.config;

import java.io.File;

import org.spincast.core.config.SpincastConfig;

/**
 * Spincast CSS YUI Compressor plugin configurations.
 */
public interface SpincastCssYuiCompressorConfig {

    /**
     * The name of the provided Pebble function to
     * bundle multiple .css files.
     * <p>
     * Defaults to "cssBundle".
     */
    public String getCssBundlePebbleFunctionName();

    /**
     * The relative url path to serve css files
     * bundled using the Pebble "cssBundle()" function.
     * <p>
     * Defaults to "<code>/spincast/plugins/cssyuicompressor/cssbundles</code>".
     */
    public String getCssBundlesUrlPath();

    /**
     * The directory where the generated css bundles
     * will be saved by the Pebble "cssBundle()" function.
     * <p>
     * Defaults to "<code>[WRITABLE_DIR]/spincast/plugins/cssyuicompressor/cssBundles</code>".
     * where "<code>[WRITABLE_DIR]</code>" is {@link SpincastConfig#getWritableRootDir()}.
     */
    public File getCssBundlesDir();

    /**
     * When creating a CSS bundle using the Pebble "cssBundle()" function,
     * should we ignore SSL certificate errors such as errors
     * for self-signed certificates when performing the requests to
     * retrieve the CSS files?
     * <p>
     * By default, return <code>true</code> if {@link SpincastConfig#isDevelopmentMode()}
     * or {@link SpincastConfig#isTestingMode()} are <code>true</code>.
     */
    public boolean isCssBundlesIgnoreSslCertificateErrors();

    /**
     * Is bundling disabled?
     * <p>
     * This is useful during development when you want changes to
     * the CSS files to be reflected and not be cached.
     * <p>
     * By default, return <code>true</code> if {@link SpincastConfig#isDevelopmentMode()}
     * is <code>true</code>.
     */
    public boolean isCssBundlesDisabled();

}

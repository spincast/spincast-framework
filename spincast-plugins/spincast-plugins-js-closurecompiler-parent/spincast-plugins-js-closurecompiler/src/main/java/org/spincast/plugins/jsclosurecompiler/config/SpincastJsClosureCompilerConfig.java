package org.spincast.plugins.jsclosurecompiler.config;

import java.io.File;

import org.spincast.core.config.SpincastConfig;

/**
 * Spincast JS Closure Compiler plugin configurations.
 */
public interface SpincastJsClosureCompilerConfig {

    /**
     * The name of the provided Pebble function to
     * bundle multiple .js files.
     * <p>
     * Defaults to "jsBundle".
     */
    public String getJsBundlePebbleFunctionName();

    /**
     * The relative url path to serve js files
     * bundled using the Pebble "jsBundle()" function.
     * <p>
     * Defaults to "<code>/spincast/plugins/jsclosurecompiler/jsbundles</code>".
     */
    public String getJsBundlesUrlPath();

    /**
     * The directory where the generated js bundles
     * will be saved by the Pebble "jsBundle()" function.
     * <p>
     * Defaults to "<code>[WRITABLE_DIR]/spincast/plugins/jsclosurecompiler/jsBundles</code>".
     * where "<code>[WRITABLE_DIR]</code>" is {@link SpincastConfig#getWritableRootDir()}.
     */
    public File getJsBundlesDir();

    /**
     * When creating a JS bundle using the Pebble "jsBundle()" function,
     * should we ignore SSL certificate errors such as errors
     * for self-signed certificates when performing the requests to
     * retrieve the Javascript files?
     * <p>
     * By default, return <code>true</code> if {@link SpincastConfig#isDevelopmentMode()}
     * or {@link SpincastConfig#isTestingMode()} are <code>true</code>.
     */
    public boolean isJsBundlesIgnoreSslCertificateErrors();

    /**
     * Is bundling disabled?
     * <p>
     * This is useful during development when you want changes to
     * the JS files to be reflected and not be cached.
     * <p>
     * By default, return <code>true</code> if {@link SpincastConfig#isDevelopmentMode()}
     * is <code>true</code>.
     */
    public boolean isJsBundlesDisabled();


}

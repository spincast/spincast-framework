package org.spincast.plugins.cssautoprefixer.config;


public interface SpincastCssAutoprefixerConfig {

    /**
     * The name or full path to the
     * "postcss" npm artifact executable.
     * <p>
     * By default this will be "<code>postcss</code>" except
     * on Windows where it will be "<code>postcss.cmd</code>".
     * <p>
     * With those defaults, the "postcss" npm
     * library must have been installed <em>globally</em>!
     */
    public String getPostcssExecutableName();

}

package org.spincast.plugins.jsclosurecompiler.config;


/**
 * Spincast JS Closure COmpiler plugin configurations.
 */
public interface SpincastJsClosureCompilerConfig {

    /**
     * The path to the <code>java</code> binary.
     * <p>
     * Is "<em>java</em>" par default.
     * You can set it to an absolute path if required.
     */
    public String getJavaBinPath();
}

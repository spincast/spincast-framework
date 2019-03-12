package org.spincast.plugins.logbackutils.config;

import org.spincast.core.utils.ResourceInfo;

public interface SpincastLogbackConfigurerConfig {

    /**
     * The path about the Loback XML file to load.
     * <p>
     * The file can be on the classpath or on the file
     * system.
     * <p>
     * You can return <code>null</code> if you want to
     * provide the full content using the
     * {@link #tweakContent(String)} method (an empty
     * string would then be passed to that method).
     */
    public ResourceInfo getResourceInfo();

    /**
     * Allows you to tweak the Logback content before it is applied.
     * <p>
     * You can use this for example to replace some <em>placeholders</em>
     * in the Logback content.
     */
    public String tweakContent(String logbackContent);
}

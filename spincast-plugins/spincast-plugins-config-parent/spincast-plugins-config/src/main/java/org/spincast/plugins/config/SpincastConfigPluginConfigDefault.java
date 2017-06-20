package org.spincast.plugins.config;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Default configuration for the Spincast Config plugin.
 * <p>
 * WARNING : Beware of circular dependencies here... In
 * general, this component should not depend on anything 
 * else, since pretty much all other components depend on it.
 * <p>
 * One dependency you <i>can</i> use, is 
 * <code>@MainArgs String[]</code> 
 * (or <code>@MainArgs List&lt;String&gt;</code>) to get access to
 * the application arguments.
 */
public class SpincastConfigPluginConfigDefault implements SpincastConfigPluginConfig {

    public static final String CONFIG_FILE_NAME_DEFAULT = "app-config.yaml";
    public static final String PREFIX_DEFAULT = "app.";

    @Override
    public String getClasspathFilePath() {
        return CONFIG_FILE_NAME_DEFAULT;
    }

    @Override
    public String getExternalFilePath() {
        return CONFIG_FILE_NAME_DEFAULT;
    }

    @Override
    public List<String> getEnvironmentVariablesPrefixes() {
        return Lists.newArrayList(PREFIX_DEFAULT);
    }

    @Override
    public boolean isEnvironmentVariablesStripPrefix() {
        return false;
    }

    @Override
    public List<String> getSystemPropertiesPrefixes() {
        return Lists.newArrayList(PREFIX_DEFAULT);
    }

    @Override
    public boolean isSystemPropertiesStripPrefix() {
        return false;
    }

    @Override
    public boolean isExternalFileConfigsOverrideEnvironmentVariables() {
        return false;
    }

    @Override
    public boolean isThrowExceptionIfSpecifiedClasspathConfigFileIsNotFound() {
        return false;
    }

    @Override
    public boolean isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound() {
        return false;
    }



}

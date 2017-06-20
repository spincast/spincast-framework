package org.spincast.plugins.config;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Config plugin.
 * 
 * WARNING : Beware of circular dependencies in implementations
 * of this interface. In general, this component should not 
 * depend on anything else, since pretty much all other components 
 * depend on it.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastConfigPluginConfigDefault.class)
public interface SpincastConfigPluginConfig {

    /**
     * The path to a configuration file to load from the
     * classpath.
     * <p>
     * Defaults to "<code>app-config.yaml</code>".
     * This means you can simply create that file
     * in your <code>/src/main/resources/</code> folder and it
     * will be used.
     */
    public String getClasspathFilePath();

    /**
     * The path to a configuration file to load from the
     * file system.
     * <p>
     * The path can be relative or absolute. Spincast will check 
     * this using :
     * <code>
     * File configFile = new File(thePath);
     * if(configFile.isAbsolute()) {...}
     * </code>
     * <p>
     * If the path is relative, it is from
     * the executable .jar.
     * <p>
     * Defaults to "<code>app-config.yaml</code>".
     */
    public String getExternalFilePath();

    /**
     * The allowed prefixes an environment variable can have
     * to be used as a configuration.
     * <p>
     * Defaults to <code>"app."</code>.
     * 
     * @return the allowed prefixes or <code>null</code> to
     * disable environment variables as a source for configurations.
     */
    public List<String> getEnvironmentVariablesPrefixes();

    /**
     * Should the prefix of an environment variable be stripped?
     * For example, if {@link #environmentVariablesPrefixes} indicates
     * that "app." is an environment variable prefix, then "app.admin.email"
     * will result in a "admin.email" key.
     * <p>
     * Defaults to <code>false</code>.
     */
    public boolean isEnvironmentVariablesStripPrefix();

    /**
     * The allowed prefixes a system property can have
     * to be used as a configuration.
     * <p>
     * Defaults to <code>"app."</code>.
     * 
     * @return the allowed prefixes or <code>null</code> to
     * disable system properties as a source for configurations.
     */
    public List<String> getSystemPropertiesPrefixes();

    /**
     * Should the prefix of an system property be stripped?
     * For example, if {@link #systemPropertiesPrefixes} indicates
     * that "app." is an system property prefix, then "app.admin.email"
     * will result in a "admin.email" key.
     * <p>
     * Defaults to <code>false</code>.
     */
    public boolean isSystemPropertiesStripPrefix();

    /**
     * If an external configuration file is used and
     * environment variables too, should configurations
     * from the file override those from environment variables?
     * <p>
     * The default is <code>false</code> : environment
     * variables have priority.
     */
    public boolean isExternalFileConfigsOverrideEnvironmentVariables();

    /**
     * Should an exception be thrown if a classpath config file is specified
     * (is not <code>null</code>) but is not found. 
     * <p>
     * If set to <code>false</code>, a message will be logged but no 
     * exception will be thrown.
     * <p>
     * Defaults to <code>false</code>.
     */
    public boolean isThrowExceptionIfSpecifiedClasspathConfigFileIsNotFound();

    /**
     * Should an exception be thrown if an external config file is specified
     * (is not <code>null</code>) but is not found. 
     * <p>
     * If set to <code>false</code>, a message will be logged but no 
     * exception will be thrown.
     * <p>
     * Defaults to <code>false</code>.
     */
    public boolean isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound();


}

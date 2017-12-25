package org.spincast.plugins.config;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtilsDefault;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

public class ConfigFinder {

    protected final Logger logger = LoggerFactory.getLogger(ConfigFinder.class);

    protected Map<String, Object> rawConfigs;
    protected Map<String, Object> configs;
    private final Object rawConfigsLock = new Object();

    private File appJarDirectory;
    private boolean appJarDirectoryChecked;
    private final Object appJarDirectoryLock = new Object();

    private File appRootDirectoryNoJar;
    private boolean appRootDirectoryNoJarChecked;
    private final Object appRootDirectoryNoJarLock = new Object();

    private final String classpathFilePath;
    private final String externalFilePath;
    private final List<String> environmentVariablesPrefixes;
    private final boolean environmentVariablesStripPrefix;
    private final List<String> systemPropertiesPrefixes;
    private final boolean systemPropertiesStripPrefix;
    private final boolean externalFileConfigsOverrideEnvironmentVariables;
    private final boolean throwExceptionIfSpecifiedClasspathConfigFileIsNotFound;
    private final boolean throwExceptionIfSpecifiedExternalConfigFileIsNotFound;

    /**
     * This component is not part of the Guice context because
     * you can very easily created circular dependencies with
     * configurations since they are used everywhere.
     */
    protected ConfigFinder(String classpathFilePath,
                           String externalFilePath,
                           List<String> environmentVariablesPrefixes,
                           boolean environmentVariablesStripPrefix,
                           List<String> systemPropertiesPrefixes,
                           boolean systemPropertiesStripPrefix,
                           boolean externalFileConfigsOverrideEnvironmentVariables,
                           boolean throwExceptionIfSpecifiedClasspathConfigFileIsNotFound,
                           boolean throwExceptionIfSpecifiedExternalConfigFileIsNotFound) {
        this.classpathFilePath = classpathFilePath;
        this.externalFilePath = externalFilePath;
        this.environmentVariablesPrefixes = clearPrefixes(environmentVariablesPrefixes);
        this.environmentVariablesStripPrefix = environmentVariablesStripPrefix;
        this.systemPropertiesPrefixes = clearPrefixes(systemPropertiesPrefixes);
        this.systemPropertiesStripPrefix = systemPropertiesStripPrefix;
        this.externalFileConfigsOverrideEnvironmentVariables = externalFileConfigsOverrideEnvironmentVariables;
        this.throwExceptionIfSpecifiedClasspathConfigFileIsNotFound = throwExceptionIfSpecifiedClasspathConfigFileIsNotFound;
        this.throwExceptionIfSpecifiedExternalConfigFileIsNotFound = throwExceptionIfSpecifiedExternalConfigFileIsNotFound;
    }

    protected List<String> clearPrefixes(List<String> prefixes) {

        List<String> clearPrefixes = new ArrayList<>();
        if (prefixes != null) {
            for (String prefix : prefixes) {
                if (prefix != null) {
                    prefix = prefix.trim();
                    if (!prefix.endsWith(".")) {
                        prefix = prefix + ".";
                    }
                    clearPrefixes.add(prefix);
                }
            }
        }

        return clearPrefixes;
    }

    public static class ConfigFinderBuilder {

        private String classpathFilePath = null;
        private String externalFilePath = null;
        private List<String> environmentVariablesPrefixes;
        private boolean environmentVariablesStripPrefix = false;
        private List<String> systemPropertiesPrefixes;
        private boolean systemPropertiesStripPrefix = false;
        private boolean externalFileConfigsOverrideEnvironmentVariables = false;
        private boolean throwExceptionIfSpecifiedClasspathConfigFileIsNotFound = false;
        private boolean throwExceptionIfSpecifiedExternalConfigFileIsNotFound = false;

        /**
         * A .yaml config file to search on the classpath. This is where you would put
         * the default values of your configurations that can be overriden externally.
         * This file would be provided in the resources of your application, for example :
         * <code>src/main/resources/app-config.yaml</code>
         */
        public ConfigFinderBuilder classpathFile(String classpathYamlFilePath) {
            this.classpathFilePath = classpathYamlFilePath;
            return this;
        }

        /**
         * A .yaml config file to search to be found outside the application.
         * This can be a relative or an absolute path. If the application is ran
         * from the executable .jar, a relative path is relative to this .jar file.
         * <p>
         * Important : a relative path MUST start with ".", otherwise the path will be
         * considered as being absolute. For example, if your configuration file
         * is called "app-config.yaml" and is next to the executable jar, you would
         * use "./app-config.yaml" as the path.
         */
        public ConfigFinderBuilder externalFile(String externalYamlFilePath) {
            return externalFile(this.externalFilePath, false);
        }

        /**
         * A .yaml config file to search to be found outside the application.
         * This can be a relative or an absolute path. If the application is ran
         * from the executable .jar, a relative path is relative to this .jar file.
         * 
         * @param overrideEnvironmentVariables If environment variables
         * are used too, should the configs from this file override them? The default 
         * is <code>false</code>: environment variables override the configs
         * from the external file.
         */
        public ConfigFinderBuilder externalFile(String externalYamlFilePath, boolean overrideEnvironmentVariables) {
            this.externalFilePath = externalYamlFilePath;
            this.externalFileConfigsOverrideEnvironmentVariables = overrideEnvironmentVariables;
            return this;
        }

        /**
         * The allowed prefixes an environment variable can have
         * to be used as a configuration.
         * <p>
         * Those prefixes will be stripped to produce the configuration
         * keys!
         * <p>
         * Multiple prefixes are allowed so you can have configurations
         * specific to an application, but also configurations common to
         * multiple applications on the same server.
         */
        public ConfigFinderBuilder environmentVariablesPrefixes(List<String> prefixes) {
            this.environmentVariablesPrefixes = prefixes;
            return this;
        }

        /**
         * Should the prefix of an environment variable be stripped?
         * For example, if {@link #environmentVariablesPrefixes} indicate
         * that "app." is an environment variable prefix, then "app.admin.email"
         * would result in a "admin.email" key.
         * <p>
         * Defaults to <code>false</code>.
         */
        public ConfigFinderBuilder environmentVariablesStripPrefix(boolean strip) {
            this.environmentVariablesStripPrefix = strip;
            return this;
        }

        /**
         * The allowed prefixes a system property can have
         * to be used as a configuration.
         * <p>
         * Those prefixes will be stripped to produce the configuration
         * keys!
         * <p>
         * Multiple prefixes are allowed so you can have configurations
         * specific to an application, but also configurations common to
         * multiple applications on the same server.
         */
        public ConfigFinderBuilder systemPropertiesPrefixes(List<String> prefixes) {
            this.systemPropertiesPrefixes = prefixes;
            return this;
        }

        /**
         * Should the prefix of an system property be stripped?
         * For example, if {@link #systemPropertiesPrefixes} indicate
         * that "app." is an system property prefix, then "app.admin.email"
         * would result in a "admin.email" key.
         * <p>
         * Defaults to <code>false</code>.
         */
        public ConfigFinderBuilder systemPropertiesStripPrefix(boolean strip) {
            this.systemPropertiesStripPrefix = strip;
            return this;
        }

        /**
         * Should an exception be thrown if a classpath config file is specified
         * (is not <code>null</code>) but is not found. 
         * <p>
         * If set to <code>false</code>, a message will be logged but no 
         * exception will be thrown.
         * <p>
         * Defaults to <code>false</code>.
         */
        public ConfigFinderBuilder throwExceptionIfSpecifiedClasspathConfigFileIsNotFound(boolean enable) {
            this.throwExceptionIfSpecifiedClasspathConfigFileIsNotFound = enable;
            return this;
        }

        /**
         * Should an exception be thrown if an external config file is specified
         * (is not <code>null</code>) but is not found. 
         * <p>
         * If set to <code>false</code>, a message will be logged but no 
         * exception will be thrown.
         * <p>
         * Defaults to <code>false</code>.
         */
        public ConfigFinderBuilder throwExceptionIfSpecifiedExternalConfigFileIsNotFound(boolean enable) {
            this.throwExceptionIfSpecifiedExternalConfigFileIsNotFound = enable;
            return this;
        }

        public ConfigFinder build() {
            return new ConfigFinder(this.classpathFilePath,
                                    this.externalFilePath,
                                    this.environmentVariablesPrefixes,
                                    this.environmentVariablesStripPrefix,
                                    this.systemPropertiesPrefixes,
                                    this.systemPropertiesStripPrefix,
                                    this.externalFileConfigsOverrideEnvironmentVariables,
                                    this.throwExceptionIfSpecifiedClasspathConfigFileIsNotFound,
                                    this.throwExceptionIfSpecifiedExternalConfigFileIsNotFound);
        }
    }

    public static ConfigFinderBuilder configure() {
        return new ConfigFinderBuilder();
    }

    protected String getClasspathFilePath() {
        return this.classpathFilePath;
    }

    protected String getExternalFilePath() {
        return this.externalFilePath;
    }

    protected List<String> getEnvironmentVariablesPrefixes() {
        return this.environmentVariablesPrefixes;
    }

    protected boolean isEnvironmentVariablesStripPrefix() {
        return this.environmentVariablesStripPrefix;
    }

    protected List<String> getSystemPropertiesPrefixes() {
        return this.systemPropertiesPrefixes;
    }

    protected boolean isSystemPropertiesStripPrefix() {
        return this.systemPropertiesStripPrefix;
    }

    protected boolean isExternalFileConfigsOverrideEnvironmentVariables() {
        return this.externalFileConfigsOverrideEnvironmentVariables;
    }

    protected boolean isThrowExceptionIfSpecifiedClasspathConfigFileIsNotFound() {
        return this.throwExceptionIfSpecifiedClasspathConfigFileIsNotFound;
    }

    protected boolean isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound() {
        return this.throwExceptionIfSpecifiedExternalConfigFileIsNotFound;
    }

    protected Map<String, Object> getConfigs() {
        if (this.configs == null) {
            this.configs = new HashMap<String, Object>();
        }
        return this.configs;
    }

    /**
     * Get raw configs, as a Map
     */
    protected Map<String, Object> getRawConfigs() {

        if (this.rawConfigs == null) {
            synchronized (this.rawConfigsLock) {
                if (this.rawConfigs == null) {

                    try {

                        Yaml yaml = new Yaml();

                        //==========================================
                        // Config file on the classpath
                        //==========================================
                        if (getClasspathFilePath() != null) {

                            InputStream configFileStream = this.getClass().getClassLoader()
                                                               .getResourceAsStream(getClasspathFilePath());
                            if (configFileStream == null) {

                                String msg = "Classpath config file not found : " + getClasspathFilePath();
                                if (isThrowExceptionIfSpecifiedClasspathConfigFileIsNotFound()) {
                                    msg += "\nSpincast was configured to throw an exception when such file is specified " +
                                           "but missing. You can disable this exception using the " +
                                           "'throwExceptionIfSpecifiedClasspathConfigFileIsNotFound()' method.";
                                    throw new RuntimeException(msg);
                                } else {
                                    this.logger.info(msg);
                                }
                            } else {
                                try {

                                    Object data = yaml.load(configFileStream);
                                    if (data == null) {
                                        this.logger.warn("Empty config file : " + getClasspathFilePath());
                                    } else {
                                        if (!(data instanceof Map)) {
                                            throw new RuntimeException("Unable to convert the Yaml config file to a Map  : " +
                                                                       getClasspathFilePath());
                                        }
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> mapTemp = (Map<String, Object>)data;

                                        this.rawConfigs = mergeMaps(this.rawConfigs, mapTemp);

                                        this.logger.info("Configurations - classpath config file applied : " +
                                                         getClasspathFilePath());
                                    }
                                } finally {
                                    SpincastStatics.closeQuietly(configFileStream);
                                }
                            }
                        }

                        //==========================================
                        // Configs from Environment variables, if 
                        // they are overriden by configs from an external 
                        // config file.
                        //==========================================
                        List<String> environmentVariablesPrefixes = getEnvironmentVariablesPrefixes();
                        if (environmentVariablesPrefixes != null && environmentVariablesPrefixes.size() > 0 &&
                            isExternalFileConfigsOverrideEnvironmentVariables()) {
                            Map<String, Object> environmentVariablesConfigs = getEnvironmentVariablesConfigs();
                            this.rawConfigs = mergeMaps(this.rawConfigs, environmentVariablesConfigs);
                        }

                        //==========================================
                        // External config file
                        //==========================================
                        if (getExternalFilePath() != null) {

                            String externalFilePath = getExternalFilePath().trim();

                            File externalFile = new File(externalFilePath);

                            //==========================================
                            // Absolute path
                            //==========================================
                            if (externalFile.isAbsolute()) {

                                if (!externalFile.isFile()) {

                                    String msg = "External config file not found : " + externalFilePath;
                                    if (isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound()) {
                                        msg += "\nSpincast was configured to throw an exception when such file is specified " +
                                               "but missing. You can disable this exception using the " +
                                               "'throwExceptionIfSpecifiedExternalConfigFileIsNotFound()' method.";
                                        throw new RuntimeException(msg);
                                    } else {
                                        this.logger.info(msg);
                                    }
                                } else {
                                    Reader reader = new FileReader(externalFile);
                                    try {

                                        Object data = yaml.load(reader);

                                        if (data == null) {
                                            this.logger.warn("Empty config file : " + getClasspathFilePath());
                                        } else {
                                            if (!(data instanceof Map)) {
                                                throw new RuntimeException("Unable to convert the Yaml config file to a Map  : " +
                                                                           externalFile);
                                            }

                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> mapTemp = (Map<String, Object>)data;
                                            this.rawConfigs = mergeMaps(this.rawConfigs, mapTemp);

                                            this.logger.info("Configurations - External config file applied : " +
                                                             externalFile.getAbsolutePath());
                                        }
                                    } finally {
                                        SpincastStatics.closeQuietly(reader);
                                    }
                                }

                            } else {

                                //==========================================
                                // Relative path
                                //==========================================
                                String externalFilePathClean = StringUtils.stripStart(externalFilePath, "./\\");

                                //==========================================
                                // Config file next to the executable .jar file?
                                //==========================================
                                File jarDir = getAppJarDirectory();
                                if (jarDir != null) {

                                    File configFile =
                                            new File(jarDir.getAbsolutePath() + "/" + externalFilePathClean);

                                    if (!configFile.isFile()) {

                                        String msg =
                                                "External configuration file not found next to the executable .jar, using relative path " +
                                                     externalFilePath;
                                        if (isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound()) {
                                            msg += "\nSpincast was configured to throw an exception when such file is specified " +
                                                   "but missing. You can disable this exception using the " +
                                                   "'throwExceptionIfSpecifiedExternalConfigFileIsNotFound()' method.";
                                            throw new RuntimeException(msg);
                                        } else {
                                            this.logger.info(msg);
                                        }

                                    } else {
                                        loadYamlFileConfigs(configFile, yaml);

                                        this.logger.info("Configurations - Config file next to the executable .jar applied : " +
                                                         configFile.getAbsolutePath());
                                    }
                                } else {

                                    //==========================================
                                    // Running outside an executable jar?
                                    //==========================================
                                    File noJarDir = getAppRootDirectoryNoJar();
                                    if (noJarDir != null) {

                                        File configFile =
                                                new File(noJarDir.getAbsolutePath() + "/" + externalFilePathClean);

                                        if (!configFile.isFile()) {

                                            String msg = "External configuration file not found using relative path " +
                                                         externalFilePath;
                                            if (isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound()) {
                                                msg += "\nSpincast was configured to throw an exception when such file is specified " +
                                                       "but missing. You can disable this exception using the " +
                                                       "'throwExceptionIfSpecifiedExternalConfigFileIsNotFound()' method.";
                                                throw new RuntimeException(msg);
                                            } else {
                                                this.logger.info(msg);
                                            }
                                        } else {
                                            loadYamlFileConfigs(configFile, yaml);

                                            this.logger.info("Configurations - Config file applied : " +
                                                             configFile.getAbsolutePath());
                                        }
                                    }
                                }
                            }
                        }

                        //==========================================
                        // Configs from Environment variables, if 
                        // they override configs from an external config file.
                        //==========================================
                        if (environmentVariablesPrefixes != null && environmentVariablesPrefixes.size() > 0 &&
                            !isExternalFileConfigsOverrideEnvironmentVariables()) {
                            Map<String, Object> environmentVariablesConfigs = getEnvironmentVariablesConfigs();
                            this.rawConfigs = mergeMaps(this.rawConfigs, environmentVariablesConfigs);
                        }

                        //==========================================
                        // Configs using System properties
                        //==========================================
                        List<String> systemPropertiesPrefixes = getSystemPropertiesPrefixes();
                        if (systemPropertiesPrefixes != null && systemPropertiesPrefixes.size() > 0) {
                            Map<String, Object> systemPropertiesConfigs = getSystemPropertiesConfigs();
                            this.rawConfigs = mergeMaps(this.rawConfigs, systemPropertiesConfigs);
                        }

                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.rawConfigs;
    }

    protected void loadYamlFileConfigs(File configFile, Yaml yaml) {

        try {
            Map<String, Object> appConfigMap;
            Reader reader = new FileReader(configFile);
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapTemp = (Map<String, Object>)yaml.load(reader);
                appConfigMap = mapTemp;
            } finally {
                SpincastStatics.closeQuietly(reader);
            }

            this.rawConfigs = mergeMaps(this.rawConfigs, appConfigMap);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected Set<Entry<String, String>> getEnvironmentVariables() {
        return System.getenv().entrySet();
    }

    protected Map<String, Object> getEnvironmentVariablesConfigs() {

        List<String> environmentVariablesPrefixes = getEnvironmentVariablesPrefixes();
        if (environmentVariablesPrefixes == null || environmentVariablesPrefixes.size() == 0) {
            return new HashMap<>();
        }

        Map<String, Object> envVarsConfigsUnparsed = new HashMap<String, Object>();

        for (Entry<String, String> entry : getEnvironmentVariables()) {
            String key = entry.getKey();

            if (key != null) {
                for (String prefix : environmentVariablesPrefixes) {
                    if (key.startsWith(prefix)) {

                        //==========================================
                        // Removes the prefixe?
                        //==========================================
                        if (isEnvironmentVariablesStripPrefix()) {
                            key = key.substring(prefix.length());
                            if (envVarsConfigsUnparsed.containsKey(key)) {
                                throw new RuntimeException("All environment variables keys must be unique once their prefix is " +
                                                           "stripped. Currently duplicated : \"" + key + "\".");
                            }
                        }

                        envVarsConfigsUnparsed.put(key, entry.getValue());
                        break;
                    }
                }
            }
        }

        Map<String, Object> envVarsConfigs = expandMap(envVarsConfigsUnparsed);

        return envVarsConfigs;
    }

    protected Map<String, Object> getSystemPropertiesConfigs() {

        List<String> systemPropertiesPrefixes = getSystemPropertiesPrefixes();
        if (systemPropertiesPrefixes == null || systemPropertiesPrefixes.size() == 0) {
            return new HashMap<>();
        }

        Map<String, Object> sysPropConfigsUnparsed = new HashMap<String, Object>();

        Properties systemProperties = System.getProperties();
        for (Object keyObj : systemProperties.keySet()) {

            if (keyObj != null && keyObj instanceof String) {

                String key = (String)keyObj;

                for (String prefix : systemPropertiesPrefixes) {
                    if (key.startsWith(prefix)) {
                        Object value = systemProperties.get(keyObj);

                        //==========================================
                        // Removes the prefix?
                        //==========================================
                        if (isSystemPropertiesStripPrefix()) {
                            key = key.substring(prefix.length());
                            if (sysPropConfigsUnparsed.containsKey(key)) {
                                throw new RuntimeException("All system variables keys must be unique once their prefix is " +
                                                           "stripped. Currently duplicated : \"" + key + "\".");
                            }
                        }

                        sysPropConfigsUnparsed.put(key, value);
                        break;
                    }
                }
            }
        }

        Map<String, Object> sysPropConfigs = expandMap(sysPropConfigsUnparsed);

        return sysPropConfigs;
    }

    /**
     * Parses the keys of the map as "dotted paths" and created
     * an expanded Map from them.
     */
    protected Map<String, Object> expandMap(Map<String, Object> sourceMap) {

        Map<String, Object> expandedMap = new HashMap<String, Object>();

        for (Entry<String, Object> entry : sourceMap.entrySet()) {
            String dottedKey = entry.getKey();
            Object value = entry.getValue();

            if (!dottedKey.contains(".")) {
                expandedMap.put(dottedKey, value);
                continue;
            }

            Map<String, Object> currentMap = expandedMap;

            String[] tokens = dottedKey.split("\\.");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];

                if (StringUtils.isBlank(token)) {
                    throw new RuntimeException("A configuration key can't contain empty tokens : " + dottedKey);
                }

                if (i == (tokens.length - 1)) {

                    //==========================================
                    // Configuration clash
                    //==========================================
                    if (currentMap.containsKey(token)) {
                        throw new RuntimeException("Configuration clash at key \"" + dottedKey + "\"");
                    }

                    currentMap.put(token, value);
                } else {

                    if (currentMap.containsKey(token)) {

                        //==========================================
                        // Configuration clash
                        //==========================================
                        if (!(currentMap.get(token) instanceof Map)) {
                            throw new RuntimeException("Configuration clash at key \"" + dottedKey + "\"");
                        }

                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>)currentMap.get(token);
                        currentMap = map;
                    } else {

                        Map<String, Object> newMap = new HashMap<>();
                        currentMap.put(token, newMap);
                        currentMap = newMap;
                    }
                }
            }
        }

        return expandedMap;
    }

    /**
     * Merges two maps :
     * 
     * The elements from the second map override the elements of
     * the first map of the same path, except for Map elements which
     * are merged.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> mergeMaps(Map<String, Object> map1, Map<String, Object> map2) {

        if (map1 == null) {
            map1 = new HashMap<String, Object>();
        }
        if (map2 == null) {
            map2 = new HashMap<String, Object>();
        }

        for (String key : map2.keySet()) {
            Object value2 = map2.get(key);
            if (map1.containsKey(key)) {
                Object value1 = map1.get(key);
                if (value1 instanceof Map && value2 instanceof Map) {
                    value2 = mergeMaps((Map<String, Object>)value1, (Map<String, Object>)value2);
                }
            }
            map1.put(key, value2);
        }

        return map1;
    }

    /**
     * Gets a config from the raw Map. The dotted key
     * will be parsed as the path to use.
     */
    public Object getRawConfig(String dottedKey) {
        return getConfigFromMap(getRawConfigs(), dottedKey);
    }

    /**
     * Gets a config from the given Map. The dotted key
     * will be parsed as the path to use.
     * 
     * @return the config value or <code>null</code> if not found.
     */
    public Object getConfigFromMap(Map<String, Object> map, String dottedKey) {
        return getConfigFromMap(map, dottedKey, null);
    }

    /**
     * Gets a config from the given Map. The dotted key
     * will be parsed as the path to use.
     * 
     * @return the config value or the default value if not found.
     */
    public Object getConfigFromMap(Map<String, Object> map, String dottedKey, Object defaultValue) {

        Objects.requireNonNull(dottedKey, "The key can't be NULL");

        if (map == null) {
            map = new HashMap<String, Object>();
        }

        if (!dottedKey.contains(".")) {
            return map.get(dottedKey);
        }

        String[] tokens = dottedKey.split("\\.");
        StringBuilder currentPath = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (i > 0) {
                currentPath.append(".");
            }
            currentPath.append(token);

            if (StringUtils.isBlank(token)) {
                throw new RuntimeException("A configuration key can't contain empty token : " + dottedKey);
            }

            if (i == (tokens.length - 1)) {
                return map.get(token);
            } else {

                if (!map.containsKey(token)) {
                    return null;
                }

                Object el = map.get(token);
                if (el == null) {
                    return null;
                }

                if (!(el instanceof Map)) {
                    throw new RuntimeException("The \"" + currentPath + "\" key was expected to be a Map!");
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> mapTmp = (Map<String, Object>)el;
                map = mapTmp;
            }
        }
        return null;
    }

    public Object getConfig(String key) {
        return getConfig(key, null);
    }

    /**
     * Gets an untype config from the config map. If the
     * config isn't there, we'll try to get it from
     * the raw configs map.
     * <p>
     * If the resulting config is <code>null</code>, the
     * default value will be returned.
     */
    public Object getConfig(String key, Object defaultValue) {

        Map<String, Object> configs = getConfigs();
        if (!configs.containsKey(key)) {
            configs.put(key, getRawConfig(key));
        }

        Object val = configs.get(key);
        if (val == null) {
            val = defaultValue;
        }

        return val;
    }

    public List<Object> getConfigList(String key) {
        return getConfigList(key, null);
    }

    public List<Object> getConfigList(String key, List<?> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Object> list = new ArrayList<Object>();

        if (!(valueObj instanceof List)) {
            list.add(valueObj);
        } else {

            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;
            list.addAll(listObj);
        }

        return list;
    }

    public Map<String, Object> getMap(String key) {
        return getMap(key, null);
    }

    public Map<String, Object> getMap(String key, Map<String, Object> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        if (!(valueObj instanceof Map)) {
            throw new RuntimeException("Configuration '" + key + "' is not a Map : " + valueObj);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)valueObj;

        return map;
    }

    public List<Map<String, Object>> getMapList(String key) {
        return getMapList(key, null);
    }

    public List<Map<String, Object>> getMapList(String key, List<Map<String, Object>> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (!(valueObj instanceof List)) {

            if (!(valueObj instanceof Map)) {
                throw new RuntimeException("Configuration '" + key + "' is not a List or a Map : " + valueObj);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)valueObj;
            list.add(map);
        } else {
            @SuppressWarnings("unchecked")

            List<Map<String, Object>> listObj = (List<Map<String, Object>>)valueObj;
            list = listObj;
        }

        return list;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }
        return valueObj.toString();
    }

    public List<String> getStringList(String key) {
        return getStringList(key, null);
    }

    public List<String> getStringList(String key, List<String> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<String> list = new ArrayList<String>();

        if (!(valueObj instanceof List)) {
            list.add(valueObj.toString());
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                if (val == null) {
                    list.add(null);
                } else {
                    list.add(val.toString());
                }
            }
        }

        return list;
    }

    protected Boolean getBooleanFromElement(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof Boolean) {
            return (Boolean)val;
        } else {
            return Boolean.valueOf(val.toString());
        }
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        return getBooleanFromElement(valueObj);
    }

    public List<Boolean> getBooleanList(String key) {
        return getBooleanList(key, null);
    }

    public List<Boolean> getBooleanList(String key, List<Boolean> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Boolean> list = new ArrayList<Boolean>();

        if (!(valueObj instanceof List)) {
            list.add(getBooleanFromElement(valueObj));
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                list.add(getBooleanFromElement(val));
            }
        }

        return list;
    }

    protected Integer getIntegerFromElement(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof Integer) {
            return (Integer)val;
        } else {
            return Integer.parseInt(val.toString());
        }
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        return getIntegerFromElement(valueObj);
    }

    public List<Integer> getIntegerList(String key) {
        return getIntegerList(key, null);
    }

    public List<Integer> getIntegerList(String key, List<Integer> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Integer> list = new ArrayList<Integer>();

        if (!(valueObj instanceof List)) {
            list.add(getIntegerFromElement(valueObj));
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                list.add(getIntegerFromElement(val));
            }
        }

        return list;
    }

    protected Long getLongFromElement(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof Long) {
            return (Long)val;
        } else {
            return Long.parseLong(val.toString());
        }
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        return getLongFromElement(valueObj);
    }

    public List<Long> getLongList(String key) {
        return getLongList(key, null);
    }

    public List<Long> getLongList(String key, List<Long> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Long> list = new ArrayList<Long>();

        if (!(valueObj instanceof List)) {
            list.add(getLongFromElement(valueObj));
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                list.add(getLongFromElement(val));
            }
        }

        return list;
    }

    protected BigDecimal getBigDecimalFromElement(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof BigDecimal) {
            return (BigDecimal)val;
        } else {
            return new BigDecimal(val.toString());
        }
    }

    public BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        return getBigDecimalFromElement(valueObj);
    }

    public List<BigDecimal> getBigDecimalList(String key) {
        return getBigDecimalList(key, null);
    }

    public List<BigDecimal> getBigDecimalList(String key, List<BigDecimal> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<BigDecimal> list = new ArrayList<BigDecimal>();

        if (!(valueObj instanceof List)) {
            list.add(getBigDecimalFromElement(valueObj));
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                list.add(getBigDecimalFromElement(val));
            }
        }

        return list;
    }

    protected Date getDateFromElement(Object date) {
        if (date == null) {
            return null;
        } else if (date instanceof Date) {
            return (Date)date;
        } else {
            return SpincastStatics.parseISO8601date(date.toString());
        }
    }

    public Date getDate(String key) {
        return getDate(key, null);
    }

    public Date getDate(String key, Date defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        return getDateFromElement(valueObj);
    }

    public List<Date> getDateList(String key) {
        return getDateList(key, null);
    }

    public List<Date> getDateList(String key, List<Date> defaultValue) {

        Object valueObj = getConfig(key, defaultValue);
        if (valueObj == null) {
            return null;
        }

        List<Date> list = new ArrayList<Date>();

        if (!(valueObj instanceof List)) {
            list.add(getDateFromElement(valueObj));
        } else {
            @SuppressWarnings("unchecked")
            List<Object> listObj = (List<Object>)valueObj;

            for (Object val : listObj) {
                list.add(getDateFromElement(val));
            }
        }

        return list;
    }

    /**
     * If the project is running from an executable
     * .jar file, this will return the directory containing
     * this .jar file.
     * 
     * @return the directory path or <code>null</code> if the
     * application is not running from an executable .jar
     * file.
     */
    protected File getAppJarDirectory() {

        if (!this.appJarDirectoryChecked) {
            synchronized (this.appJarDirectoryLock) {
                if (!this.appJarDirectoryChecked) {
                    this.appJarDirectoryChecked = true;

                    try {

                        String jarPath = SpincastUtilsDefault.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                        if (jarPath == null) {
                            throw new RuntimeException("Unable to get the path of " + SpincastUtilsDefault.class.getName() + "!");
                        }

                        jarPath = URLDecoder.decode(jarPath, "UTF-8");
                        if (jarPath.toLowerCase().endsWith(".jar")) {
                            File jarFile = new File(jarPath);
                            if (!jarFile.isFile()) {
                                throw new RuntimeException("This is supposed to be a file : " + jarFile.getAbsolutePath());
                            }

                            String jar2Path = ConfigFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                            if (jar2Path == null) {
                                throw new RuntimeException("Unable to get the path of " + ConfigFinder.class.getName() + "!");
                            }
                            jar2Path = URLDecoder.decode(jar2Path, "UTF-8");

                            if (!jarPath.equals(jar2Path)) {
                                this.logger.warn("Not inside a single executable jar.");
                            } else {

                                String manifestPath = "jar:file:" + jarPath + "!/META-INF/MANIFEST.MF";
                                Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                                Attributes attr = manifest.getMainAttributes();
                                String mainClass = attr.getValue("Main-Class");
                                if (mainClass == null) {
                                    this.logger.warn("Not inside an executable jar : " + jarFile.getAbsolutePath());
                                } else {
                                    File appJarDir = jarFile.getParentFile();
                                    if (!appJarDir.isDirectory()) {
                                        throw new RuntimeException("This is supposed to be a directory : " +
                                                                   appJarDir.getAbsolutePath());
                                    }
                                    this.appJarDirectory = appJarDir;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.appJarDirectory;
    }

    /**
     * If the project is not running from an executable
     * .jar file, this will return the root directory of the
     * project on the file system.
     * 
     * @return the directory path or <code>null</code> if the
     * application is running from an executable .jar
     * file.
     */
    protected File getAppRootDirectoryNoJar() {

        if (!this.appRootDirectoryNoJarChecked) {
            synchronized (this.appRootDirectoryNoJarLock) {
                if (!this.appRootDirectoryNoJarChecked) {
                    this.appRootDirectoryNoJarChecked = true;

                    try {
                        //==========================================
                        // Running in a .jar
                        //==========================================
                        if (getAppJarDirectory() != null) {
                            this.appRootDirectoryNoJar = null;
                        } else {
                            String path = new File(".").getAbsolutePath();
                            path = path.substring(0, path.length() - 1);
                            this.appRootDirectoryNoJar = new File(path);
                        }

                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.appRootDirectoryNoJar;
    }

}

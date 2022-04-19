package org.spincast.plugins.pebble;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.pebble.utils.SpincastCaffeineTagCache;
import org.spincast.plugins.pebble.utils.SpincastCaffeineTemplateCache;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Pebble Html template engine
 */
public class SpincastPebbleTemplatingEngine implements TemplatingEngine {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastPebbleTemplatingEngine.class);

    public static final String PEBBLE_PARAMS_AS_JSONOBJECT =
            SpincastPebbleTemplatingEngine.class.getName() + "paramsAsJsonObject";

    private final SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;
    private final SpincastConfig spincastConfig;
    private final Set<Extension> extensions;
    private PebbleEngine pebbleEngineString;
    private PebbleEngine pebbleEngineTemplateClasspath;
    private PebbleEngine pebbleEngineTemplateFileSystem;
    private final JsonManager jsonManager;

    @Inject
    public SpincastPebbleTemplatingEngine(SpincastConfig spincastConfig,
                                          SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                          Set<Extension> extensions,
                                          JsonManager jsonManager) {
        this.spincastConfig = spincastConfig;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.extensions = extensions;
        this.jsonManager = jsonManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    protected Set<Extension> getExtensions() {
        return this.extensions;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected PebbleEngine getPebbleEngineString() {
        if (this.pebbleEngineString == null) {

            Builder builder = new PebbleEngine.Builder().loader(new StringLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineString = builder.build();
        }
        return this.pebbleEngineString;
    }

    protected PebbleEngine getPebbleEngineTemplateClasspath() {
        if (this.pebbleEngineTemplateClasspath == null) {

            Builder builder = new PebbleEngine.Builder().loader(getClasspathTemplateLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineTemplateClasspath = builder.build();
        }
        return this.pebbleEngineTemplateClasspath;
    }

    protected PebbleEngine getPebbleEngineTemplateFileSystem() {
        if (this.pebbleEngineTemplateFileSystem == null) {

            Builder builder = new PebbleEngine.Builder().loader(getFileSystemTemplateLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineTemplateFileSystem = builder.build();
        }
        return this.pebbleEngineTemplateFileSystem;
    }

    protected void addCommonLoaderFeatures(Builder builder) {

        builder.strictVariables(getSpincastPebbleTemplatingEngineConfig().isStrictVariablesEnabled());
        builder.newLineTrimming(false);

        int templateCacheItemNbr = getSpincastPebbleTemplatingEngineConfig().getTemplateCacheItemNbr();
        if (templateCacheItemNbr < 0) {
            templateCacheItemNbr = 0;
        }
        PebbleCache<Object, PebbleTemplate> templateCache = new SpincastCaffeineTemplateCache(templateCacheItemNbr);
        builder.templateCache(templateCache);

        int tagCacheItemNbr = getSpincastPebbleTemplatingEngineConfig().getTagCacheTypeItemNbr();
        if (tagCacheItemNbr < 0) {
            tagCacheItemNbr = 0;
        }
        PebbleCache<CacheKey, Object> tagCache = new SpincastCaffeineTagCache(tagCacheItemNbr);
        builder.tagCache(tagCache);

        //==========================================
        // Pebble extensions, from Spincast and from
        // the plugins.
        //==========================================
        for (Extension extension : getExtensions()) {
            if (extension != null) {
                builder.extension(extension);
            }
        }

        //==========================================
        // Some custom extension to add?
        //==========================================
        Extension extension = getSpincastPebbleTemplatingEngineConfig().getExtension();
        if (extension != null) {
            builder.extension(extension);
        }
    }

    protected Loader<String> getClasspathTemplateLoader() {
        ClasspathLoader classpathLoader = new ClasspathLoader(SpincastPebbleTemplatingEngine.class.getClassLoader());
        return classpathLoader;
    }

    protected Loader<String> getFileSystemTemplateLoader() {
        FileLoader fileSystemLoader = new FileLoader();
        return fileSystemLoader;
    }

    @Override
    public String evaluate(String content) {
        return evaluate(content, (JsonObject)null);
    }

    @Override
    public String evaluate(String content, JsonObject jsonObject) {
        return evaluate(content, jsonObject, null);
    }

    @Override
    public String evaluate(String content, JsonObject jsonObject, Locale locale) {
        return parse(content, jsonObject, (jsonObject != null ? jsonObject.convertToPlainMap() : null), false, false, locale);
    }

    @Override
    public String evaluate(String content, Map<String, Object> params) {
        return parse(content, getJsonManager().fromMap(params), params, false, false, null);
    }

    @Override
    public String evaluate(String content, Map<String, Object> params, Locale locale) {
        return parse(content, getJsonManager().fromMap(params), params, false, false, locale);
    }

    @Override
    public String fromTemplate(String templatePath, JsonObject jsonObject) {
        return fromTemplate(templatePath, jsonObject, null);
    }

    @Override
    public String fromTemplate(String templatePath, JsonObject jsonObject, Locale locale) {
        return parse(templatePath, jsonObject, (jsonObject != null ? jsonObject.convertToPlainMap() : null), true, true, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject) {
        return fromTemplate(templatePath, isClasspathPath, jsonObject, null);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject, Locale locale) {
        return parse(templatePath,
                     jsonObject,
                     (jsonObject != null ? jsonObject.convertToPlainMap() : null),
                     true,
                     isClasspathPath,
                     locale);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params) {
        return parse(templatePath, getJsonManager().fromMap(params), params, true, true, null);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale) {
        return parse(templatePath, getJsonManager().fromMap(params), params, true, true, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params) {
        return parse(templatePath, getJsonManager().fromMap(params), params, true, isClasspathPath, null);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, Locale locale) {
        return parse(templatePath, getJsonManager().fromMap(params), params, true, isClasspathPath, locale);
    }

    protected String parse(String htmlOrPath,
                           JsonObject paramsAsJsonObject,
                           Map<String, Object> params,
                           boolean isTemplate,
                           boolean isClasspathPath,
                           Locale locale) {
        try {

            if (params == null) {
                params = new HashMap<String, Object>();
            }

            if (locale == null) {
                locale = getSpincastConfig().getDefaultLocale();
            }

            PebbleEngine pebbleEngine;
            if (isTemplate) {
                if (isClasspathPath) {
                    pebbleEngine = getPebbleEngineTemplateClasspath();

                    if (htmlOrPath != null && htmlOrPath.startsWith("/")) {
                        htmlOrPath = htmlOrPath.substring(1);
                    }
                } else {
                    pebbleEngine = getPebbleEngineTemplateFileSystem();
                }
            } else {
                pebbleEngine = getPebbleEngineString();
            }

            PebbleTemplate template = pebbleEngine.getTemplate(htmlOrPath);

            Writer writer = new StringWriter();

            //==========================================
            // We add the params as a JsonObject too.
            // This will allows us to evaluate a dynamically created
            // key in a template.
            //==========================================
            @SuppressWarnings("unchecked")
            Map<String, Object> map =
                    (Map<String, Object>)params.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
            if (map == null) {
                map = new HashMap<String, Object>();
                params.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP, map);
            }
            map.put(PEBBLE_PARAMS_AS_JSONOBJECT, paramsAsJsonObject);

            template.evaluate(writer, params, locale);
            String result = writer.toString();

            return result;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String createPlaceholder(String variable) {
        return "{{" + variable + "}}";
    }

}

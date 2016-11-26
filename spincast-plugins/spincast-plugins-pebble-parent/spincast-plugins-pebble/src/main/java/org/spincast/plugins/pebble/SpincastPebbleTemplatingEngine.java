package org.spincast.plugins.pebble;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastStatics;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
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
    private final SpincastPebbleExtension spincastPebbleExtension;
    private PebbleEngine pebbleEngineString;
    private PebbleEngine pebbleEngineTemplateClasspath;
    private PebbleEngine pebbleEngineTemplateFileSystem;
    private final JsonManager jsonManager;

    @Inject
    public SpincastPebbleTemplatingEngine(SpincastConfig spincastConfig,
                                          SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                          @Nullable SpincastPebbleExtension spincastPebbleExtension,
                                          JsonManager jsonManager) {
        this.spincastConfig = spincastConfig;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.spincastPebbleExtension = spincastPebbleExtension;
        this.jsonManager = jsonManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    protected SpincastPebbleExtension getSpincastPebbleExtension() {
        return this.spincastPebbleExtension;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected PebbleEngine getPebbleEngineString() {
        if(this.pebbleEngineString == null) {

            Builder builder = new PebbleEngine.Builder().loader(new StringLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineString = builder.build();
        }
        return this.pebbleEngineString;
    }

    protected PebbleEngine getPebbleEngineTemplateClasspath() {
        if(this.pebbleEngineTemplateClasspath == null) {

            Builder builder = new PebbleEngine.Builder().loader(getClasspathTemplateLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineTemplateClasspath = builder.build();
        }
        return this.pebbleEngineTemplateClasspath;
    }

    protected PebbleEngine getPebbleEngineTemplateFileSystem() {
        if(this.pebbleEngineTemplateFileSystem == null) {

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
        if(templateCacheItemNbr < 0) {
            templateCacheItemNbr = 0;
        }
        Cache<Object, PebbleTemplate> cache = CacheBuilder.newBuilder().maximumSize(templateCacheItemNbr).build();
        builder.templateCache(cache);

        int tagCacheItemNbr = getSpincastPebbleTemplatingEngineConfig().getTagCacheTypeItemNbr();
        if(tagCacheItemNbr < 0) {
            tagCacheItemNbr = 0;
        }
        Cache<BaseTagCacheKey, Object> tagCache = CacheBuilder.newBuilder().maximumSize(templateCacheItemNbr).build();
        builder.tagCache(tagCache);

        //==========================================
        // Spincast Pebble extension
        //==========================================
        if(getSpincastPebbleExtension() != null) {
            builder.extension(getSpincastPebbleExtension());
        }

        //==========================================
        // Some custom extension to add?
        //==========================================
        Extension extension = getSpincastPebbleTemplatingEngineConfig().getExtension();
        if(extension != null) {
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
    public String evaluate(String content, JsonObject jsonObject) {
        return parse(content, jsonObject, jsonObject.convertToPlainMap(), false, false, null);
    }

    @Override
    public String evaluate(String content, JsonObject jsonObject, Locale locale) {
        return parse(content, jsonObject, jsonObject.convertToPlainMap(), false, false, locale);
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
        return parse(templatePath, jsonObject, jsonObject.convertToPlainMap(), true, true, null);
    }

    @Override
    public String fromTemplate(String templatePath, JsonObject jsonObject, Locale locale) {
        return parse(templatePath, jsonObject, jsonObject.convertToPlainMap(), true, true, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject) {
        return parse(templatePath, jsonObject, jsonObject.convertToPlainMap(), true, isClasspathPath, null);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject, Locale locale) {
        return parse(templatePath, jsonObject, jsonObject.convertToPlainMap(), true, isClasspathPath, locale);
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

            if(params == null) {
                params = new HashMap<String, Object>();
            }

            if(locale == null) {
                locale = getSpincastConfig().getDefaultLocale();
            }

            PebbleEngine pebbleEngine;
            if(isTemplate) {
                if(isClasspathPath) {
                    pebbleEngine = getPebbleEngineTemplateClasspath();

                    if(htmlOrPath != null && htmlOrPath.startsWith("/")) {
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
            if(map == null) {
                map = new HashMap<String, Object>();
                params.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP, map);
            }
            map.put(PEBBLE_PARAMS_AS_JSONOBJECT, paramsAsJsonObject);

            template.evaluate(writer, params, locale);
            String result = writer.toString();

            return result;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String createPlaceholder(String variable) {
        return "{{" + variable + "}}";
    }

}

package org.spincast.plugins.pebble;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.templating.ITemplatingEngine;
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
public class SpincastPebbleTemplatingEngine implements ITemplatingEngine {

    private final ISpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;
    private final ISpincastConfig spincastConfig;
    private final ISpincastPebbleExtension spincastPebbleExtension;
    private PebbleEngine pebbleEngineString;
    private PebbleEngine pebbleEngineTemplateClasspath;
    private PebbleEngine pebbleEngineTemplateFileSystem;

    @Inject
    public SpincastPebbleTemplatingEngine(ISpincastConfig spincastConfig,
                                          ISpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                          @Nullable ISpincastPebbleExtension spincastPebbleExtension) {
        this.spincastConfig = spincastConfig;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.spincastPebbleExtension = spincastPebbleExtension;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected ISpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    protected ISpincastPebbleExtension getSpincastPebbleExtension() {
        return this.spincastPebbleExtension;
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
    public String evaluate(String content, Map<String, Object> params) {
        return evaluate(content, params, null);
    }

    @Override
    public String evaluate(String content, Map<String, Object> params, Locale locale) {
        return parse(content, params, false, false, locale);
    }

    @Override
    public String evaluate(String content, IJsonObject jsonObject) {
        return evaluate(content, convertJsonObjectToMap(jsonObject), null);
    }

    @Override
    public String evaluate(String content, IJsonObject jsonObject, Locale locale) {
        return evaluate(content, convertJsonObjectToMap(jsonObject), locale);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params) {
        return fromTemplate(templatePath, params, null);
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject jsonObject) {
        return fromTemplate(templatePath, convertJsonObjectToMap(jsonObject), null);
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject jsonObject, Locale locale) {
        return fromTemplate(templatePath, convertJsonObjectToMap(jsonObject), locale);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale) {
        return parse(templatePath, params, true, true, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params) {
        return parse(templatePath, params, true, isClasspathPath, null);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, Locale locale) {
        return parse(templatePath, params, true, isClasspathPath, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, IJsonObject jsonObject) {
        return parse(templatePath, convertJsonObjectToMap(jsonObject), true, isClasspathPath, null);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, IJsonObject jsonObject, Locale locale) {
        return parse(templatePath, convertJsonObjectToMap(jsonObject), true, isClasspathPath, locale);
    }

    /**
     * Converts a IJsonObject to a Map&lt;String, Object&gt;, but only
     * the first level, only so it can be pass to the Pebble templating
     * engine which requires a Map&lt;String, Object&gt;.
     */
    protected Map<String, Object> convertJsonObjectToMap(IJsonObject jsonObject) {

        Map<String, Object> map = new HashMap<String, Object>();
        if(jsonObject != null) {
            for(Entry<String, Object> entry : jsonObject) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    protected String parse(String htmlOrPath,
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

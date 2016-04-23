package org.spincast.plugins.pebble;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Pebble Html template engine
 */
public class SpincastPebbleTemplatingEngine implements ITemplatingEngine {

    private final ISpincastConfig spincastConfig;
    private PebbleEngine pebbleEngineString;
    private PebbleEngine pebbleEngineTemplate;

    @Inject
    public SpincastPebbleTemplatingEngine(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected PebbleEngine getPebbleEngineString() {
        if(this.pebbleEngineString == null) {

            Builder builder = new PebbleEngine.Builder().loader(new StringLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineString = builder.build();
        }
        return this.pebbleEngineString;
    }

    protected PebbleEngine getPebbleEngineTemplate() {
        if(this.pebbleEngineTemplate == null) {

            Builder builder = new PebbleEngine.Builder().loader(getTemplateLoader());
            addCommonLoaderFeatures(builder);
            this.pebbleEngineTemplate = builder.build();
        }
        return this.pebbleEngineTemplate;
    }

    protected void addCommonLoaderFeatures(Builder builder) {
        if(getSpincastConfig().isDebugEnabled()) {
            builder.strictVariables(true);
            builder.tagCache(null);
            builder.templateCache(null);
        }
    }

    protected Loader<String> getTemplateLoader() {
        ClasspathLoader classpathLoader = new ClasspathLoader(SpincastPebbleTemplatingEngine.class.getClassLoader());
        return classpathLoader;
    }

    @Override
    public String evaluate(String content, Map<String, Object> params) {
        return evaluate(content, params, null);
    }

    @Override
    public String evaluate(String content, Map<String, Object> params, Locale locale) {
        return parse(content, params, false, locale);
    }

    @Override
    public String evaluate(String content, IJsonObject jsonObject) {
        return evaluate(content, jsonObject.getUnderlyingMap(), null);
    }

    @Override
    public String evaluate(String content, IJsonObject jsonObject, Locale locale) {
        return evaluate(content, jsonObject.getUnderlyingMap(), locale);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params) {
        return fromTemplate(templatePath, params, null);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale) {

        if(templatePath != null && templatePath.startsWith("/")) {
            templatePath = templatePath.substring(1);
        }

        return parse(templatePath, params, true, locale);
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject jsonObject) {
        return fromTemplate(templatePath, jsonObject.getUnderlyingMap(), null);
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject jsonObject, Locale locale) {
        return fromTemplate(templatePath, jsonObject.getUnderlyingMap(), locale);
    }

    protected String parse(String htmlOrPath,
                           Map<String, Object> params,
                           boolean isTemplate,
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
                pebbleEngine = getPebbleEngineTemplate();
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

}

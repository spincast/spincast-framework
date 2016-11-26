package org.spincast.plugins.templatingaddon;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.templating.TemplatingRequestContextAddon;

import com.google.inject.Inject;

public class SpincastTemplatingRequestContextAddon<R extends RequestContext<?>>
                                                  implements TemplatingRequestContextAddon<R> {

    private final R requestContext;
    private final TemplatingEngine templatingEngine;
    private final LocaleResolver localeResolver;
    private final JsonManager jsonManager;
    private Map<String, Object> templatingGlobalVariables;

    @Inject
    public SpincastTemplatingRequestContextAddon(R requestContext,
                                                 TemplatingEngine templatingEngine,
                                                 LocaleResolver localeResolver,
                                                 JsonManager jsonManager) {
        this.requestContext = requestContext;
        this.templatingEngine = templatingEngine;
        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected Locale getLocaleToUse() {
        return getLocaleResolver().getLocaleToUse();
    }

    @Override
    public Map<String, Object> getTemplatingGlobalVariables() {
        if(this.templatingGlobalVariables == null) {
            this.templatingGlobalVariables = new HashMap<String, Object>();
        }
        return this.templatingGlobalVariables;
    }

    @Override
    public String evaluate(String content, Map<String, Object> params) {
        return evaluate(content, params, getLocaleToUse());
    }

    @Override
    public String evaluate(String content, Map<String, Object> params, Locale locale) {

        if(params == null) {
            params = new HashMap<String, Object>();
        }

        JsonObject model = getJsonManager().fromMap(params);

        return evaluate(content, model, locale);
    }

    @Override
    public String evaluate(String content, JsonObject model) {
        return evaluate(content, model, getLocaleToUse());
    }

    @Override
    public String evaluate(String content, JsonObject model, Locale locale) {

        if(model == null) {
            model = getJsonManager().create();
        }

        // We also add the global variables!
        model.merge(getTemplatingGlobalVariables());

        return getTemplatingEngine().evaluate(content, model, locale);
    }

    @Override
    public String fromTemplate(String templatePath, JsonObject model) {
        return fromTemplate(templatePath, true, model, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, JsonObject model, Locale locale) {
        return fromTemplate(templatePath, true, model, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject model) {
        return fromTemplate(templatePath, isClasspathPath, model, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params) {
        return fromTemplate(templatePath, true, params, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params) {
        return fromTemplate(templatePath, isClasspathPath, params, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale) {
        return fromTemplate(templatePath, true, params, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject model, Locale locale) {

        return fromTemplate(templatePath,
                            isClasspathPath,
                            (model != null ? model.convertToPlainMap() : null),
                            locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, Locale locale) {

        if(params == null) {
            params = new HashMap<String, Object>();
        }

        // We also add the global variables!
        params.putAll(getTemplatingGlobalVariables());

        return getTemplatingEngine().fromTemplate(templatePath, isClasspathPath, params, locale);
    }

    @Override
    public void addTemplatingGlobalVariable(String key, Object value) {

        value = convertTemplatingGlobalVariableValue(value);
        getTemplatingGlobalVariables().put(key, value);
    }

    /**
     * Converts JsonObject and JsonArray to plain Maps and Lists.
     */
    protected Object convertTemplatingGlobalVariableValue(Object value) {
        if(value != null) {
            if(value instanceof JsonObject) {
                value = ((JsonObject)value).convertToPlainMap();
            } else if(value instanceof JsonArray) {
                value = ((JsonArray)value).convertToPlainList();
            }
        }
        return value;
    }

    @Override
    public void addTemplatingGlobalVariables(Map<String, Object> variables) {
        getTemplatingGlobalVariables().putAll(variables);
    }

    @Override
    public Object getTemplatingGlobalVariable(String key) {
        return getTemplatingGlobalVariables().get(key);
    }

    @Override
    public void deleteAllTemplatingGlobalVariables() {
        getTemplatingGlobalVariables().clear();
    }

    @Override
    public void deleteTemplatingGlobalVariable(String key) {
        getTemplatingGlobalVariables().remove(key);
    }

    @Override
    public String createPlaceholder(String variable) {
        return getTemplatingEngine().createPlaceholder(variable);
    }

    @Override
    public Map<String, Object> getSpincastReservedMap() {

        @SuppressWarnings("unchecked")
        Map<String, Object> map =
                (Map<String, Object>)getTemplatingGlobalVariable(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
        if(map == null) {
            map = new HashMap<String, Object>();
            addTemplatingGlobalVariable(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP,
                                        map);
        } else if(!(map instanceof Map)) {
            throw new RuntimeException("The '" +
                                       SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP +
                                       "' root variable is reserved for Spincast and must be an instance of Map<String, Object>.");
        }

        return map;
    }

}

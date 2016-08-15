package org.spincast.plugins.templatingaddon;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.templating.ITemplatingRequestContextAddon;

import com.google.inject.Inject;

public class SpincastTemplatingRequestContextAddon<R extends IRequestContext<?>>
                                                  implements ITemplatingRequestContextAddon<R> {

    private final R requestContext;
    private final ITemplatingEngine templatingEngine;
    private final ILocaleResolver localeResolver;
    private final IJsonManager jsonManager;
    private Map<String, Object> templatingGlobalVariables;

    @Inject
    public SpincastTemplatingRequestContextAddon(R requestContext,
                                                 ITemplatingEngine templatingEngine,
                                                 ILocaleResolver localeResolver,
                                                 IJsonManager jsonManager) {
        this.requestContext = requestContext;
        this.templatingEngine = templatingEngine;
        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected ITemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected ILocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    protected IJsonManager getJsonManager() {
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

        IJsonObject model = getJsonManager().create(params);

        return evaluate(content, model, locale);
    }

    @Override
    public String evaluate(String content, IJsonObject model) {
        return evaluate(content, model, getLocaleToUse());
    }

    @Override
    public String evaluate(String content, IJsonObject model, Locale locale) {

        if(model == null) {
            model = getJsonManager().create();
        }

        // We also add the global variables!
        model.merge(getTemplatingGlobalVariables());

        return getTemplatingEngine().evaluate(content, model, locale);
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject model) {
        return fromTemplate(templatePath, true, model, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, IJsonObject model, Locale locale) {
        return fromTemplate(templatePath, true, model, locale);
    }

    @Override
    public String fromTemplate(String templatePath, boolean isClasspathPath, IJsonObject model) {
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
    public String fromTemplate(String templatePath, boolean isClasspathPath, IJsonObject model, Locale locale) {

        if(model == null) {
            model = getJsonManager().create();
        }

        return fromTemplate(templatePath, isClasspathPath, model.convertToPlainMap(), locale);
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
        getTemplatingGlobalVariables().put(key, value);
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

}

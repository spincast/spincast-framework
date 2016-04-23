package org.spincast.plugins.templatingaddon;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.templating.ITemplatingRequestContextAddon;

import com.google.inject.Inject;

public class SpincastTemplatingRequestContextAddon<R extends IRequestContext<?>>
                                                  implements ITemplatingRequestContextAddon<R> {

    private final R requestContext;
    private final ITemplatingEngine templatingEngine;
    private final ILocaleResolver localeResolver;
    private Map<String, Object> templatingGlobalVariables;

    @Inject
    public SpincastTemplatingRequestContextAddon(R requestContext,
                                                 ITemplatingEngine templatingEngine,
                                                 ILocaleResolver localeResolver) {
        this.requestContext = requestContext;
        this.templatingEngine = templatingEngine;
        this.localeResolver = localeResolver;
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

        // We also add the global variables!
        params.putAll(getTemplatingGlobalVariables());

        return getTemplatingEngine().evaluate(content, params, locale);
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params) {
        return fromTemplate(templatePath, params, getLocaleToUse());
    }

    @Override
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale) {

        if(params == null) {
            params = new HashMap<String, Object>();
        }

        // We also add the global variables!
        params.putAll(getTemplatingGlobalVariables());

        return getTemplatingEngine().fromTemplate(templatePath, params, locale);
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

}

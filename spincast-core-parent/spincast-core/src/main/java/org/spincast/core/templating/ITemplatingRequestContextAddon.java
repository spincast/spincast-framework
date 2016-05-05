package org.spincast.core.templating;

import java.util.Locale;
import java.util.Map;

import org.spincast.core.exchange.IRequestContext;

/**
 * Provides methods to deal with templating.
 */
public interface ITemplatingRequestContextAddon<R extends IRequestContext<?>> {

    /**
     * Adds a global variable that will be available to any following
     * evaluation by the templating engine.
     */
    public void addTemplatingGlobalVariable(String key, Object value);

    /**
     * Adds global variables that will be available to any following
     * evaluation by the templating engine.
     */
    public void addTemplatingGlobalVariables(Map<String, Object> variables);

    /**
     * The global templating variables.
     */
    public Map<String, Object> getTemplatingGlobalVariables();

    /**
     * Gets a global templating variable.
     */
    public Object getTemplatingGlobalVariable(String key);

    /**
     * Deletes all global templating variables.
     */
    public void deleteAllTemplatingGlobalVariables();

    /**
     * Deletes a global templating variable.
     */
    public void deleteTemplatingGlobalVariable(String key);

    /**
     * Evaluates some content using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     */
    public String evaluate(String content, Map<String, Object> params);

    /**
     * Evaluates some content using the given parameters.
     * 
     * Uses the specified <code>Locale</code>.
     */
    public String evaluate(String content, Map<String, Object> params, Locale locale);

    /**
     * Renders a template using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params);

    /**
     * Renders a template usgin the given parameters.
     * Uses the <code>Locale</code> specified.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale);

}

package org.spincast.core.templating;

import java.util.Locale;
import java.util.Map;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonObject;

/**
 * Provides methods to deal with templating.
 */
public interface TemplatingRequestContextAddon<R extends RequestContext<?>> {

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
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params);

    /**
     * Renders a template usgin the given parameters.
     * Uses the <code>Locale</code> specified.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale);

    /**
     * Renders a template using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params);

    /**
     * Renders a template usgin the given parameters.
     * Uses the <code>Locale</code> specified.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, Locale locale);

    /**
     * Evaluates some content using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     */
    public String evaluate(String content, JsonObject model);

    /**
     * Evaluates some content using the given parameters.
     * 
     * Uses the specified <code>Locale</code>.
     */
    public String evaluate(String content, JsonObject model, Locale locale);

    /**
     * Renders a template using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, JsonObject model);

    /**
     * Renders a template usgin the given parameters.
     * Uses the <code>Locale</code> specified.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, JsonObject model, Locale locale);

    /**
     * Renders a template using the given parameters.
     * 
     * Uses the <code>Locale</code> found by the <code>LocaleResolver</code>.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject model);

    /**
     * Renders a template usgin the given parameters.
     * Uses the <code>Locale</code> specified.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject model, Locale locale);

    /**
     * Creates a placeholder using the current templating engine
     * implementation. 
     * <p>
     * This is mainly useful for the tests, which don't know in advance
     * which templating engine will be used, so which syntax to use
     * for the placeholders.
     * </p>
     * <p>
     * For example, using Pebble, a call to <code>createPlaceholder("name")</code> will
     * result in "<code>{{name}}</code>" (without the quotes).
     * </p>
     */
    public String createPlaceholder(String variable);

    /**
     * Gets the Map reserved for Spincast usage to put some
     * global templating variables.
     */
    public Map<String, Object> getSpincastReservedMap();

}

package org.spincast.core.templating;

import java.util.Locale;
import java.util.Map;

import org.spincast.core.json.JsonObject;

/**
 * Component that "evaluates" some templates or inline content, using some parameters. 
 * The most frequent use of this is
 * to generate an <code>HTML</code> page to render.
 */
public interface TemplatingEngine {

    /**
     * Evaluates the content, using the given parameters.
     * 
     * Uses the default Locale.
     */
    public String evaluate(String content, Map<String, Object> params);

    /**
     * Evaluates the content, using the given parameters.
     * 
     * Uses the specified Locale.
     */
    public String evaluate(String content, Map<String, Object> params, Locale locale);

    /**
     * Evaluates the content, using the given parameters.
     * specified as a <code>JsonObject</code>.
     * 
     * Uses the default Locale.
     */
    public String evaluate(String content, JsonObject jsonObject);

    /**
     * Evaluates the content, using the given parameters.
     * specified as a <code>JsonObject</code>.
     * 
     * Uses the specified Locale.
     */
    public String evaluate(String content, JsonObject jsonObject, Locale locale);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the default Locale.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the specified Locale.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>JsonObject</code>.
     * 
     * Uses the default Locale.
     * 
     * @param templatePath must be a classpath's relative path.
     */
    public String fromTemplate(String templatePath, JsonObject jsonObject);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>JsonObject</code>.
     * 
     * Uses the specified Locale.
     * 
     * @param templatePath must be a classpath's relative path.
     * 
     */
    public String fromTemplate(String templatePath, JsonObject jsonObject, Locale locale);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the default Locale.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the specified Locale.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, Locale locale);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>JsonObject</code>.
     * 
     * Uses the default Locale.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>JsonObject</code>.
     * 
     * Uses the specified Locale.
     * 
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public String fromTemplate(String templatePath, boolean isClasspathPath, JsonObject jsonObject, Locale locale);

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

}

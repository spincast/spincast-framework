package org.spincast.core.templating;

import java.util.Locale;
import java.util.Map;

import org.spincast.core.json.IJsonObject;

/**
 * Component that "evaluates" some templates or inline content, using some parameters. 
 * The most frequent use of this is
 * to generate an <code>HTML</code> page to render.
 */
public interface ITemplatingEngine {

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
     * specified as a <code>IJsonObject</code>.
     * 
     * Uses the default Locale.
     */
    public String evaluate(String content, IJsonObject jsonObject);

    /**
     * Evaluates the content, using the given parameters.
     * specified as a <code>IJsonObject</code>.
     * 
     * Uses the specified Locale.
     */
    public String evaluate(String content, IJsonObject jsonObject, Locale locale);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the default Locale.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params);

    /**
     * Evaluates a template using the given parameters.
     * 
     * Uses the specified Locale.
     */
    public String fromTemplate(String templatePath, Map<String, Object> params, Locale locale);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>IJsonObject</code>.
     * 
     * Uses the default Locale.
     */
    public String fromTemplate(String templatePath, IJsonObject jsonObject);

    /**
     * Evaluates a template using the parameters specified 
     * as a <code>IJsonObject</code>.
     * 
     * Uses the specified Locale.
     */
    public String fromTemplate(String templatePath, IJsonObject jsonObject, Locale locale);

}

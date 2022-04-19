package org.spincast.plugins.pebble;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.text.StringEscapeUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.core.DefaultFilter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Spincast Main Pebble extension implementation.
 */
public class SpincastMainPebbleExtensionDefault extends AbstractExtension implements SpincastMainPebbleExtension {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastMainPebbleExtensionDefault.class);

    //==========================================
    // Filters names
    //==========================================
    public static final String FILTER_NAME_CHECKED = "checked";
    public static final String FILTER_NAME_SELECTED = "selected";
    public static final String FILTER_NAME_VALIDATION_MESSAGES = "validationMessages";
    public static final String FILTER_NAME_VALIDATION_GROUP_MESSAGES = "validationGroupMessages";
    public static final String FILTER_NAME_VALIDATION_CLASS = "validationClass";
    public static final String FILTER_NAME_VALIDATION_FRESH = "validationFresh";
    public static final String FILTER_NAME_VALIDATION_SUBMITTED = "validationSubmitted";
    public static final String FILTER_NAME_VALIDATION_IS_VALID = "validationIsValid";
    public static final String FILTER_NAME_VALIDATION_HAS_SUCCESSES = "validationHasSuccesses";
    public static final String FILTER_NAME_VALIDATION_HAS_WARNINGS = "validationHasWarnings";
    public static final String FILTER_NAME_VALIDATION_HAS_ERRORS = "validationHasErrors";
    public static final String FILTER_NAME_VALIDATION_GET = "get";
    public static final String FILTER_NAME_ESCAPE_BUT_REPLACE_NEWLINE_BY_BR = "newline2br";
    public static final String FILTER_NAME_BOOLEAN = "boolean";
    public static final String FILTER_NAME_EMPTY_OR_FALSE = "isEmptyOrFalse";
    public static final String FILTER_NAME_EMPTY_OR_TRUE = "isEmptyOrTrue";
    public static final String FILTER_NAME_NOT_EMPTY_AND_FALSE = "isNotEmptyAndFalse";
    public static final String FILTER_NAME_NOT_EMPTY_AND_TRUE = "isNotEmptyAndTrue";

    //==========================================
    // Fonctions names
    //==========================================
    public static final String FUNCTION_NAME_VALIDATION_GET = "get";
    public static final String FUNCTION_NAME_VALIDATION_JS_ONE_LINE = "jsOneLine";
    public static final String FUNCTION_NAME_MESSAGE = "msg";
    public static final String FUNCTION_NAME_QUERYSTRING_APPEND = "querystring";
    public static final String FUNCTION_NAME_QUERYSTRING_TO_HIDDEN_FIELDS = "querystringToHiddenFields";
    public static final String FUNCTION_NAME_ROUTE = "isRoute";
    public static final String FUNCTION_NAME_ROUTE_ID = "isRouteId";

    private final Provider<TemplatingEngine> templatingEngineProvider;
    private final SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;
    private TemplatingEngine templatingEngine;
    private final ObjectConverter objectConverter;
    private final SpincastUtils spincastUtils;
    private final Dictionary dictionary;
    private final Provider<RequestContext<?>> requestContextProvider;
    private final SpincastConfig spincastConfig;

    /**
     * Constructor
     */
    @Inject
    public SpincastMainPebbleExtensionDefault(Provider<TemplatingEngine> templatingEngineProvider,
                                              SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                              ObjectConverter objectConverter,
                                              SpincastUtils spincastUtils,
                                              Dictionary dictionary,
                                              Provider<RequestContext<?>> requestContextProvider,
                                              SpincastConfig spincastConfig) {
        this.templatingEngineProvider = templatingEngineProvider;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.objectConverter = objectConverter;
        this.spincastUtils = spincastUtils;
        this.dictionary = dictionary;
        this.requestContextProvider = requestContextProvider;
        this.spincastConfig = spincastConfig;
    }

    public TemplatingEngine getTemplatingEngine() {
        if (this.templatingEngine == null) {
            this.templatingEngine = this.templatingEngineProvider.get();
        }
        return this.templatingEngine;
    }

    protected ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
    }

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Override
    public List<AttributeResolver> getAttributeResolver() {

        List<AttributeResolver> attributeResolvers = new ArrayList<>();
        AttributeResolver defaultSpincastAttributeResolver = getDefaultSpincastAttributeResolver();
        if (defaultSpincastAttributeResolver != null) {
            attributeResolvers.add(defaultSpincastAttributeResolver);
        }
        return attributeResolvers;
    }

    protected AttributeResolver getDefaultSpincastAttributeResolver() {
        return new AttributeResolver() {

            @Override
            public ResolvedAttribute resolve(Object instance,
                                             Object attributeNameValue,
                                             Object[] argumentValues,
                                             ArgumentsNode args,
                                             EvaluationContextImpl context,
                                             String filename,
                                             int lineNumber) {
                if (instance == null || attributeNameValue == null) {
                    return null;
                }

                //==========================================
                // JsonObjects
                //==========================================
                if (instance instanceof JsonObject) {
                    return resolveJsonObjectAttribute((JsonObject)instance, String.valueOf(attributeNameValue), context);

                //==========================================@formatter:off
                // JsonArrays
                //==========================================@formatter:on
                } else if (instance instanceof JsonArray) {
                    int pos;
                    try {
                        pos = Integer.parseInt(String.valueOf(attributeNameValue));
                    } catch (Exception ex) {
                        logger.warn("Invalid 'attributeNameValue' for a JsonArray, returning null: " + attributeNameValue);
                        return null;
                    }

                    return resolveJsonArrayAttribute((JsonArray)instance, pos, context);
                }

                return null;
            }
        };
    }

    protected ResolvedAttribute resolveJsonObjectAttribute(JsonObject obj,
                                                           String attrName,
                                                           EvaluationContextImpl context) {
        Object result = obj.getObject(attrName, null);
        return new ResolvedAttribute(result);
    }

    protected ResolvedAttribute resolveJsonArrayAttribute(JsonArray arr,
                                                          int pos,
                                                          EvaluationContextImpl context) {
        Object result = arr.getObject(pos, null);
        return new ResolvedAttribute(result);
    }

    @Override
    public Map<String, Filter> getFilters() {

        Map<String, Filter> filters = new HashMap<String, Filter>();

        Filter filter = getCheckedFilter();
        if (filter != null) {
            filters.put(getCheckedFilterName(), filter);
        }

        filter = getSelectedFilter();
        if (filter != null) {
            filters.put(getSelectedFilterName(), filter);
        }

        filter = getValidationMessagesFilter(getValidationMessagesTemplatePath());
        if (filter != null) {
            filters.put(getValidationMessagesFilterName(), filter);
        }

        filter = getValidationMessagesFilter(getValidationGroupMessagesTemplatePath());
        if (filter != null) {
            filters.put(getValidationGroupMessagesFilterName(), filter);
        }

        filter = getValidationClassFilter();
        if (filter != null) {
            filters.put(getValidationClassFilterName(), filter);
        }

        filter = getValidationFreshFilter();
        if (filter != null) {
            filters.put(getValidationFreshFilterName(), filter);
        }

        filter = getValidationSubmittedFilter();
        if (filter != null) {
            filters.put(getValidationSubmittedFilterName(), filter);
        }

        filter = getValidationIsValidFilter();
        if (filter != null) {
            filters.put(getValidationIsValidFilterName(), filter);
        }

        filter = getValidationHasSuccessesFilter();
        if (filter != null) {
            filters.put(getValidationHasSuccessesFilterName(), filter);
        }

        filter = getValidationHasWarningsFilter();
        if (filter != null) {
            filters.put(getValidationHasWarningsFilterName(), filter);
        }

        filter = getValidationHasErrorsFilter();
        if (filter != null) {
            filters.put(getValidationHasErrorsFilterName(), filter);
        }

        filter = getGetFilter();
        if (filter != null) {
            filters.put(getGetFilterName(), filter);
        }

        filter = getNewlineToBrFilter();
        if (filter != null) {
            filters.put(getNewlineToBrFilterName(), filter);
        }

        filter = getBooleanFilter();
        if (filter != null) {
            filters.put(getBooleanFilterName(), filter);
        }

        filter = getIsEmptyOrFalseFilter();
        if (filter != null) {
            filters.put(getIsEmptyOrFalseFilterName(), filter);
        }

        filter = getIsEmptyOrTrueFilter();
        if (filter != null) {
            filters.put(getIsEmptyOrTrueFilterName(), filter);
        }

        filter = getIsNotEmptyAndFalseFilte();
        if (filter != null) {
            filters.put(getIsNotEmptyAndFalseFilterName(), filter);
        }

        filter = getIsNotEmptyAndTrueFilte();
        if (filter != null) {
            filters.put(getIsNotEmptyAndTrueFilterName(), filter);
        }

        return filters;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<String, Function>();

        functions.put(getGetFunctionName(), getGetFunction());
        functions.put(getJsOneLinerOutputFunctionName(), getJsOneLinerOutputFunction());
        functions.put(getMessageFunctionName(), getMessageFunction());
        functions.put(getQuerystringAppendFunctionName(), getQuerystringAppendFunction());
        functions.put(getQuerystringToHiddenFieldsFunctionName(), getQuerystringToHiddenFieldsFunction());
        functions.put(getRouteFunctionName(), getRouteFunction());
        functions.put(getRouteIdFunctionName(), getRouteIdFunction());

        return functions;
    }

    protected Set<Object> convertToSet(Object obj) {
        Set<Object> values = new HashSet<Object>();
        if (obj instanceof Collection) {
            values.addAll((Collection<?>)obj);
        } else if (obj instanceof Object[]) {
            Object[] arr = (Object[])obj;
            for (int i = 0; i < arr.length; i++) {
                values.add(arr[i]);
            }
        } else {
            values.add(obj);
        }

        return values;
    }

    protected String getValidationClassError() {
        return "has-error";
    }

    protected Object getValidationClassWarning() {
        return "has-warning";
    }

    protected Object getValidationClassSuccess() {
        return "has-success";
    }

    protected Object getValidationClassNoMessage() {
        return "has-no-message";
    }

    protected String getValidationMessagesTemplatePath() {
        return getSpincastPebbleTemplatingEngineConfig().getValidationMessagesTemplatePath();
    }

    protected String getValidationGroupMessagesTemplatePath() {
        return getSpincastPebbleTemplatingEngineConfig().getValidationGroupMessagesTemplatePath();
    }

    protected String getCheckedFilterName() {
        return FILTER_NAME_CHECKED;
    }

    protected Filter getCheckedFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("acceptableValues");
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                Set<Object> values = convertToSet(value);
                Set<Object> acceptableValues = convertToSet(args.get("acceptableValues"));

                if (getObjectConverter().isAtLeastOneEquivalentElementInCommon(values, acceptableValues)) {
                    return "checked";
                }

                return "";
            }
        };

        return filter;
    }

    protected String getSelectedFilterName() {
        return FILTER_NAME_SELECTED;
    }

    protected Filter getSelectedFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("acceptableValues");
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                Set<Object> values = convertToSet(value);
                Set<Object> acceptableValues = convertToSet(args.get("acceptableValues"));

                if (getObjectConverter().isAtLeastOneEquivalentElementInCommon(values, acceptableValues)) {
                    return "selected";
                }

                return "";
            }
        };

        return filter;
    }

    protected String getValidationMessagesFilterName() {
        return FILTER_NAME_VALIDATION_MESSAGES;
    }

    protected String getValidationGroupMessagesFilterName() {
        return FILTER_NAME_VALIDATION_GROUP_MESSAGES;
    }

    protected Filter getValidationMessagesFilter(final String templatePath) {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return "";
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                String html = getTemplatingEngine().fromTemplate(templatePath,
                                                                 SpincastStatics.params("validation", validationResultMap));

                return new SafeString(html);
            }
        };

        return filter;
    }

    protected String getValidationClassFilterName() {
        return FILTER_NAME_VALIDATION_CLASS;
    }

    protected Filter getValidationClassFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return getValidationClassNoMessage();
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                Boolean hasErrors = (Boolean)validationResultMap.get("hasErrors");
                if (hasErrors == null || hasErrors) {
                    return getValidationClassError();
                }

                Boolean hasWarnings = (Boolean)validationResultMap.get("hasWarnings");
                if (hasWarnings != null && hasWarnings) {
                    return getValidationClassWarning();
                }

                Boolean hasSuccesses = (Boolean)validationResultMap.get("hasSuccesses");
                if (hasSuccesses != null && hasSuccesses) {
                    return getValidationClassSuccess();
                }

                return getValidationClassNoMessage();
            }
        };

        return filter;
    }

    protected String getValidationFreshFilterName() {
        return FILTER_NAME_VALIDATION_FRESH;
    }

    protected Filter getValidationFreshFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null) {
                    return true;
                }
                return false;
            }
        };

        return filter;
    }

    protected String getValidationSubmittedFilterName() {
        return FILTER_NAME_VALIDATION_SUBMITTED;
    }

    protected Filter getValidationSubmittedFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null) {
                    return false;
                }
                return true;
            }
        };

        return filter;
    }

    protected String getValidationIsValidFilterName() {
        return FILTER_NAME_VALIDATION_IS_VALID;
    }

    protected Filter getValidationIsValidFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return true;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                Object isValid = validationResultMap.get("isValid");
                return isValid != null && isValid instanceof Boolean && (Boolean)isValid;
            }
        };

        return filter;
    }

    protected String getValidationHasSuccessesFilterName() {
        return FILTER_NAME_VALIDATION_HAS_SUCCESSES;
    }

    protected Filter getValidationHasSuccessesFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return false;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                Object hasSuccesses = validationResultMap.get("hasSuccesses");
                return hasSuccesses != null && hasSuccesses instanceof Boolean && (Boolean)hasSuccesses;
            }
        };

        return filter;
    }

    protected String getValidationHasWarningsFilterName() {
        return FILTER_NAME_VALIDATION_HAS_WARNINGS;
    }

    protected Filter getValidationHasWarningsFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return false;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                Object hasWarnings = validationResultMap.get("hasWarnings");
                return hasWarnings != null && hasWarnings instanceof Boolean && (Boolean)hasWarnings;
            }
        };

        return filter;
    }

    protected String getValidationHasErrorsFilterName() {
        return FILTER_NAME_VALIDATION_HAS_ERRORS;
    }

    protected Filter getValidationHasErrorsFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof Map)) {
                    return false;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> validationResultMap = (Map<String, Object>)value;

                Object hasErrors = validationResultMap.get("hasErrors");
                return hasErrors != null && hasErrors instanceof Boolean && (Boolean)hasErrors;
            }
        };

        return filter;
    }

    protected String getGetFilterName() {
        return FILTER_NAME_VALIDATION_GET;
    }

    protected Filter getGetFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || !(value instanceof String)) {
                    return null;
                }

                String key = (String)value;


                @SuppressWarnings("unchecked")
                Map<String, Object> spincastMap =
                        (Map<String, Object>)evaluationContext.getVariable(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
                if (spincastMap == null) {
                    return null;
                }

                JsonObject paramsAsJsonObject =
                        (JsonObject)spincastMap.get(SpincastPebbleTemplatingEngine.PEBBLE_PARAMS_AS_JSONOBJECT);
                if (paramsAsJsonObject == null) {
                    return null;
                }

                Object result = paramsAsJsonObject.getObject(key, "");

                if (result instanceof JsonObject) {
                    return ((JsonObject)result).convertToPlainMap();
                } else if (result instanceof JsonArray) {
                    return ((JsonArray)result).convertToPlainList();
                }

                return result;
            }
        };

        return filter;
    }

    protected String getNewlineToBrFilterName() {
        return FILTER_NAME_ESCAPE_BUT_REPLACE_NEWLINE_BY_BR;
    }

    protected Filter getNewlineToBrFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("escape");
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                boolean escape = true;
                Object escapeObj = args.get("escape");
                if (escapeObj != null && escapeObj instanceof Boolean) {
                    escape = (Boolean)escapeObj;
                }

                String text = "";
                if (value != null) {
                    text = value.toString();

                    if (escape) {
                        text = StringEscapeUtils.escapeHtml4(text);
                    }

                    text = text.replaceAll("(\r\n|\n)", "<br />\n");
                }

                return new SafeString(text);
            }
        };

        return filter;
    }

    protected String getBooleanFilterName() {
        return FILTER_NAME_BOOLEAN;
    }

    protected Filter getBooleanFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null) {
                    return false;
                }

                String str = value.toString();
                return Boolean.valueOf(str);
            }
        };

        return filter;
    }

    protected String getIsEmptyOrFalseFilterName() {
        return FILTER_NAME_EMPTY_OR_FALSE;
    }

    protected Filter getIsEmptyOrFalseFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || "".equals(value)) {
                    return true;
                }

                return value.toString().equalsIgnoreCase("false");
            }
        };

        return filter;
    }

    protected String getIsEmptyOrTrueFilterName() {
        return FILTER_NAME_EMPTY_OR_TRUE;
    }

    protected Filter getIsEmptyOrTrueFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || "".equals(value)) {
                    return true;
                }

                return value.toString().equalsIgnoreCase("true");
            }
        };

        return filter;
    }

    protected String getIsNotEmptyAndFalseFilterName() {
        return FILTER_NAME_NOT_EMPTY_AND_FALSE;
    }

    protected Filter getIsNotEmptyAndFalseFilte() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || "".equals(value)) {
                    return false;
                }

                return value.toString().equalsIgnoreCase("false");
            }
        };

        return filter;
    }

    protected String getIsNotEmptyAndTrueFilterName() {
        return FILTER_NAME_NOT_EMPTY_AND_TRUE;
    }

    protected Filter getIsNotEmptyAndTrueFilte() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {

                if (value == null || "".equals(value)) {
                    return false;
                }

                return value.toString().equalsIgnoreCase("true");
            }
        };

        return filter;
    }


    protected String getGetFunctionName() {
        return FUNCTION_NAME_VALIDATION_GET;
    }

    protected Function getGetFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {

                List<String> names = new ArrayList<>();
                names.add("jsonPath");
                return names;
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                String key = (String)args.get("jsonPath");
                if (StringUtils.isBlank(key)) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> spincastMap =
                        (Map<String, Object>)evaluationContext.getVariable(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
                if (spincastMap == null) {
                    return null;
                }

                JsonObject paramsAsJsonObject =
                        (JsonObject)spincastMap.get(SpincastPebbleTemplatingEngine.PEBBLE_PARAMS_AS_JSONOBJECT);
                if (paramsAsJsonObject == null) {
                    return null;
                }

                Object result = paramsAsJsonObject.getObject(key, "");

                if (result instanceof JsonObject) {
                    return ((JsonObject)result).convertToPlainMap();
                } else if (result instanceof JsonArray) {
                    return ((JsonArray)result).convertToPlainList();
                }

                return result;
            }
        };
    }

    protected String getJsOneLinerOutputFunctionName() {
        return FUNCTION_NAME_VALIDATION_JS_ONE_LINE;
    }

    protected Function getJsOneLinerOutputFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                Object codeObj = args.get("0");
                if (codeObj == null) {
                    return "";
                }

                String code = codeObj.toString();
                if (StringUtils.isBlank(code)) {
                    return "";
                }

                Boolean singleQuotesDelimiter = false;
                Object arg1 = args.get("1");
                if (arg1 != null && arg1 instanceof Boolean) {
                    singleQuotesDelimiter = (Boolean)args.get("1");
                }

                String codeFormatted = getSpincastUtils().inQuotesStringFormat(code, singleQuotesDelimiter);

                //==========================================
                // We should also break any potential
                // "</script>" tag...
                //==========================================
                char delimiter = singleQuotesDelimiter ? '\'' : '"';
                codeFormatted = codeFormatted.replace("</script>", "</s" + delimiter + " + " + delimiter + "cript>");

                return new SafeString(codeFormatted);

            }
        };
    }

    protected String getMessageFunctionName() {
        return FUNCTION_NAME_MESSAGE;
    }

    protected Function getMessageFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {
                if (args == null || args.size() == 0) {
                    return "";
                }

                //==========================================
                // If the arg after the message key is a boolean,
                // it represent the "forceEvaluation".
                //==========================================
                Object firstArg = args.get("1");

                boolean forceEvaluation = false;
                if (firstArg != null && firstArg instanceof Boolean) {
                    forceEvaluation = (Boolean)firstArg;
                    args.remove("1");
                }

                Map<String, Object> params = new HashMap<String, Object>();

                String msgKey = null;
                String paramKey = null;
                for (Entry<String, Object> entry : args.entrySet()) {

                    String key = entry.getKey();
                    String val = SpincastStatics.stringValueOrNull(entry.getValue());

                    if (!key.matches("^\\d+$")) {
                        continue;
                    }

                    if (msgKey == null) {
                        msgKey = val;
                        continue;
                    }

                    if (paramKey == null) {
                        paramKey = val;
                        continue;
                    }

                    params.put(paramKey, val);
                    paramKey = null;
                }
                if (paramKey != null) {
                    throw new RuntimeException("A value is missing for one of the key of the custom '" + FUNCTION_NAME_MESSAGE +
                                               "' Pebble function... The key without value is : \"" + paramKey + "\"");
                }

                if (params.size() == 0 && !forceEvaluation) {
                    params = null;
                }

                return getDictionary().get(msgKey, params);
            }
        };
    }

    protected String getQuerystringAppendFunctionName() {
        return FUNCTION_NAME_QUERYSTRING_APPEND;
    }

    protected Function getQuerystringAppendFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("querystringToAppend");
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                Object querystringToAppendObj = args.get("querystringToAppend");

                if (querystringToAppendObj == null) {
                    return "";
                }
                String querystring = querystringToAppendObj.toString();

                querystring = StringUtils.stripStart(querystring, "?&");

                if (StringUtils.isBlank(querystring)) {
                    return "";
                }

                //==========================================
                // This can be called outside of a Request scope, for
                // example by a scheduled task.
                //==========================================
                Map<String, List<String>> currentQueryStringParams = new HashMap<String, List<String>>();
                try {
                    RequestContext<?> context = getRequestContextProvider().get();

                    Map<String, List<String>> temp = new HashMap<String, List<String>>(context.request().getQueryStringParams());
                    if (temp != null) {
                        currentQueryStringParams = temp;
                    }
                } catch (OutOfScopeException | ProvisionException ex) {
                    // ok, not in the scope a a request
                }

                //==========================================
                // anchor to keep?
                //==========================================
                String anchor = "";
                int pos = querystring.indexOf("#");
                if (pos > -1) {
                    anchor = querystring.substring(pos);
                    querystring = querystring.substring(0, pos);
                }

                LinkedHashMap<String, List<String>> newQueryStringParams = new LinkedHashMap<String, List<String>>();
                String[] querystringTokens = querystring.split("&");
                for (String qsToken : querystringTokens) {
                    if (StringUtils.isBlank(qsToken)) {
                        continue;
                    }

                    String[] keyVal = qsToken.split("=");
                    if (keyVal == null || keyVal.length != 2) {
                        continue;
                    }
                    String key = keyVal[0];
                    String val = keyVal[1];
                    if (StringUtils.isBlank(key)) {
                        continue;
                    }

                    currentQueryStringParams.remove(key);
                    List<String> vals = newQueryStringParams.get(key);
                    if (vals == null) {
                        vals = new ArrayList<String>();
                        newQueryStringParams.put(key, vals);
                    }

                    val = encodeQuerystringValue(val);
                    vals.add(val);
                }

                LinkedHashMap<String, List<String>> finalQueryStringParams = new LinkedHashMap<String, List<String>>();

                for (Entry<String, List<String>> entry : currentQueryStringParams.entrySet()) {
                    String key = entry.getKey();

                    List<String> values = entry.getValue();
                    if (values == null || values.size() == 0) {
                        continue;
                    }

                    List<String> valuesEncoded = new ArrayList<String>();
                    for (String value : values) {
                        value = encodeQuerystringValue(value);
                        valuesEncoded.add(value);
                    }
                    finalQueryStringParams.put(key, valuesEncoded);
                }
                finalQueryStringParams.putAll(newQueryStringParams);

                StringBuilder builder = new StringBuilder("?");
                for (Entry<String, List<String>> entry : finalQueryStringParams.entrySet()) {
                    String key = entry.getKey();
                    List<String> vals = entry.getValue();

                    for (String val : vals) {
                        builder.append(key).append("=").append(val).append("&");
                    }
                }
                builder = builder.deleteCharAt(builder.length() - 1);

                if (!StringUtils.isBlank(anchor)) {
                    builder.append(anchor);
                }

                return new SafeString(builder.toString());
            }
        };
    }

    protected String getQuerystringToHiddenFieldsFunctionName() {
        return FUNCTION_NAME_QUERYSTRING_TO_HIDDEN_FIELDS;
    }

    protected Function getQuerystringToHiddenFieldsFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("ignoreFieldNames");
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                StringBuilder builder = new StringBuilder();

                //==========================================
                // This can be called outside of a Request scope, for
                // example by a scheduled task.
                //==========================================
                try {
                    RequestContext<?> context = getRequestContextProvider().get();

                    Map<String, List<String>> queryStringParams = context.request().getQueryStringParams();
                    if (queryStringParams != null && queryStringParams.size() > 0) {

                        //==========================================
                        // Fields to ignore?
                        //==========================================
                        Set<String> ignoreFieldNames = new HashSet<String>();
                        Object ignoreFieldNamesObj = args.get("ignoreFieldNames");
                        if (ignoreFieldNamesObj != null && ignoreFieldNamesObj instanceof List) {
                            @SuppressWarnings({"rawtypes", "unchecked"})
                            List<String> ignoreFieldNamesList = (List)ignoreFieldNamesObj;
                            ignoreFieldNames.addAll(ignoreFieldNamesList);
                        }

                        for (Entry<String, List<String>> entry : queryStringParams.entrySet()) {
                            List<String> values = entry.getValue();
                            if (values != null && values.size() > 0) {
                                String fieldName = entry.getKey();
                                if (ignoreFieldNames.contains(fieldName)) {
                                    continue;
                                }

                                if (values.size() > 1) {
                                    fieldName += "[]";
                                }
                                for (String value : values) {
                                    builder.append("<input type='hidden' name='")
                                           .append(fieldName)
                                           .append("' value='")
                                           .append(value).append("' />\n");
                                }
                            }
                        }
                    }

                } catch (OutOfScopeException | ProvisionException ex) {
                    // ok, not in the scope a a request
                }

                return new SafeString(builder.toString());
            }
        };
    }



    protected String encodeQuerystringValue(String value) {
        if (value == null) {
            return "";
        }

        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        return value;
    }

    protected String getRouteFunctionName() {
        return FUNCTION_NAME_ROUTE;
    }

    protected Function getRouteFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("path", "isRegEx", "subPathsToo");
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                Object pathToMatchObj = args.get("path");
                String pathToMatch = "";
                if (pathToMatchObj != null) {
                    pathToMatch = pathToMatchObj.toString();
                }
                pathToMatch = pathToMatch.trim();
                if (StringUtils.isBlank(pathToMatch)) {
                    pathToMatch = "";
                }

                Object isRegExObj = args.get("isRegEx");
                boolean isRegEx = false;
                if (isRegExObj != null) {
                    isRegEx = Boolean.parseBoolean(isRegExObj.toString());
                }

                Object subPathsTooObj = args.get("subPathsToo");
                boolean subPathsToo = false;
                if (subPathsTooObj != null) {
                    subPathsToo = Boolean.parseBoolean(subPathsTooObj.toString());
                }

                String currentRoutePath;
                try {
                    RequestContext<?> context = getRequestContextProvider().get();
                    currentRoutePath = context.request().getRequestPath();
                } catch (OutOfScopeException | ProvisionException ex) {
                    // Not in the scope a a request
                    return false;
                }

                currentRoutePath = currentRoutePath.toLowerCase();
                currentRoutePath = StringUtils.stripEnd(currentRoutePath, "/");

                //==========================================
                // RegEx
                //==========================================
                if (isRegEx) {

                    if (subPathsToo) {
                        if (pathToMatch.endsWith("/")) {
                            pathToMatch = pathToMatch.substring(0, pathToMatch.length() - 1);
                        }
                        pathToMatch += "(/?$|/.*)";
                    }


                    if (Pattern.matches(pathToMatch, currentRoutePath)) {
                        return true;
                    }
                //==========================================@formatter:off
                // Exact path
                //==========================================@formatter:on
                } else {
                    pathToMatch = StringUtils.stripEnd(pathToMatch, "/");
                    if (!pathToMatch.equals("") && !pathToMatch.startsWith("/")) {
                        pathToMatch = "/" + pathToMatch;
                    }

                    if (currentRoutePath.equals(pathToMatch)) {
                        return true;
                    }

                    if (subPathsToo) {
                        if (currentRoutePath.startsWith(pathToMatch + "/")) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    protected String getRouteIdFunctionName() {
        return FUNCTION_NAME_ROUTE_ID;
    }

    protected Function getRouteIdFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("id");
            }

            @Override
            public Object execute(Map<String, Object> args,
                                  PebbleTemplate self,
                                  EvaluationContext evaluationContext,
                                  int lineNumber) {

                Object idToMatchObj = args.get("id");
                String idToMatch = "";
                if (idToMatchObj == null) {
                    return false;
                }
                idToMatch = idToMatchObj.toString().trim();
                if (StringUtils.isBlank(idToMatch)) {
                    return false;
                }

                String currentRouteId;
                try {
                    RequestContext<?> context = getRequestContextProvider().get();
                    currentRouteId = context.routing().getCurrentRouteHandlerMatch().getSourceRoute().getId();
                } catch (OutOfScopeException | ProvisionException ex) {
                    // Not in the scope a a request
                    return false;
                }

                if (idToMatch.equals(currentRouteId)) {
                    return true;
                }

                return false;
            }
        };
    }

}

package org.spincast.plugins.pebble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spincast.core.config.SpincastConstants;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.core.DefaultFilter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.ScopeChain;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

/**
 * Spincast default Pebble extension implementation.
 */
public class SpincastPebbleExtensionDefault implements SpincastPebbleExtension {

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

    //==========================================
    // Fonctions names
    //==========================================
    public static final String FUNCTION_NAME_VALIDATION_GET = "get";
    public static final String FUNCTION_NAME_VALIDATION_JS_ONE_LINE = "jsOneLine";
    public static final String FUNCTION_NAME_MESSAGE = "msg";

    private final Provider<TemplatingEngine> templatingEngineProvider;
    private final SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;
    private TemplatingEngine templatingEngine;
    private final ObjectConverter objectConverter;
    private final SpincastUtils spincastUtils;
    private final Dictionary dictionary;

    /**
     * Constructor
     */
    @Inject
    public SpincastPebbleExtensionDefault(Provider<TemplatingEngine> templatingEngineProvider,
                                          SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                          ObjectConverter objectConverter,
                                          SpincastUtils spincastUtils,
                                          Dictionary dictionary) {
        this.templatingEngineProvider = templatingEngineProvider;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.objectConverter = objectConverter;
        this.spincastUtils = spincastUtils;
        this.dictionary = dictionary;
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

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Override
    public Map<String, Test> getTests() {
        return null;
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        return null;
    }

    @Override
    public List<BinaryOperator> getBinaryOperators() {
        return null;
    }

    @Override
    public List<UnaryOperator> getUnaryOperators() {
        return null;
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return null;
    }

    @Override
    public List<NodeVisitorFactory> getNodeVisitors() {
        return null;
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

        return filters;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<String, Function>();

        functions.put(getGetFunctionName(), getGetFunction());
        functions.put(getJsOneLinerOutputFunctionName(), getJsOneLinerOutputFunction());
        functions.put(getMessageFunctionName(), getMessageFunction());

        return functions;
    }

    protected EvaluationContext getEvaluationContext(Map<String, Object> args) {
        return (EvaluationContext)args.get("_context");
    }

    protected PebbleTemplate getPebbleTemplate(Map<String, Object> args) {
        return (PebbleTemplate)args.get("_self");
    }

    protected ScopeChain getScopeChain(Map<String, Object> args) {
        return getEvaluationContext(args).getScopeChain();
    }

    protected Object getModelElement(Map<String, Object> args, String property) {
        return getScopeChain(args).get(property);
    }

    protected String getPropertyAsString(Map<String, Object> args, String property) {
        return (String)getModelElement(args, property);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getPropertyAsMap(Map<String, Object> args, String property) {
        return (Map<String, Object>)getModelElement(args, property);
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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

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
            public Object apply(Object value, Map<String, Object> args) {

                if (value == null || !(value instanceof String)) {
                    return null;
                }

                String key = (String)value;

                @SuppressWarnings("unchecked")
                Map<String, Object> spincastMap =
                        (Map<String, Object>)getModelElement(args,
                                                             SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
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
            public Object execute(Map<String, Object> args) {

                String key = (String)args.get("jsonPath");
                if (StringUtils.isBlank(key)) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> spincastMap =
                        (Map<String, Object>)getModelElement(args,
                                                             SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_ROOT_SPINCAST_MAP);
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
            public Object execute(Map<String, Object> args) {

                Object codeObj = args.get("0");
                if (codeObj == null) {
                    return "";
                }

                String code = codeObj.toString();
                if (StringUtils.isBlank(code)) {
                    return "";
                }

                Boolean singleQuotesDelimiter = (Boolean)args.get("1");
                if (singleQuotesDelimiter == null) {
                    singleQuotesDelimiter = false;
                }

                String codeFormatted = getSpincastUtils().inQuotesStringFormat(code, singleQuotesDelimiter);
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
            public Object execute(Map<String, Object> args) {
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
                    String val = String.valueOf(entry.getValue());

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
}

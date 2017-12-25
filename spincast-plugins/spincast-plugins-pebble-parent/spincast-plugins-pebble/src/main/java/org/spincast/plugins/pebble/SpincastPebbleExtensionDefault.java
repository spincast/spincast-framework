package org.spincast.plugins.pebble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.config.SpincastConstants;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastStatics;
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

    private final Provider<TemplatingEngine> templatingEngineProvider;
    private final SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;
    private TemplatingEngine templatingEngine;
    private final ObjectConverter objectConverter;

    /**
     * Constructor
     */
    @Inject
    public SpincastPebbleExtensionDefault(Provider<TemplatingEngine> templatingEngineProvider,
                                          SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                          ObjectConverter objectConverter) {
        this.templatingEngineProvider = templatingEngineProvider;
        this.spincastPebbleTemplatingEngineConfig = spincastPebbleTemplatingEngineConfig;
        this.objectConverter = objectConverter;
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
        return "checked";
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
        return "selected";
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
        return "validationMessages";
    }

    protected String getValidationGroupMessagesFilterName() {
        return "validationGroupMessages";
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
        return "validationClass";
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
        return "validationFresh";
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
        return "validationSubmitted";
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
        return "validationIsValid";
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
        return "validationHasSuccesses";
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
        return "validationHasWarnings";
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
        return "validationHasErrors";
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
        return "get";
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
        return "get";
    }

    protected String getJsOneLinerOutputFunctionName() {
        return "jsOneLine";
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

                code = code.replace("\r", "");
                code = code.replace("\n", "");

                if (singleQuotesDelimiter) {
                    code = code.replace("'", "\\\'");
                } else {
                    code = code.replace("\"", "\\\"");
                }

                return new SafeString(code);

            }
        };
    }
}

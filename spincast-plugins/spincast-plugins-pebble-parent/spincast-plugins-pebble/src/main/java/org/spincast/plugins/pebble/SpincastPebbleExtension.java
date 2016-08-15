package org.spincast.plugins.pebble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.SpincastStatics;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

/**
 * Spincast default Peeble extension implementation.
 */
public class SpincastPebbleExtension implements ISpincastPebbleExtension {

    private final Provider<ITemplatingEngine> templatingEngineProvider;
    private ITemplatingEngine templatingEngine;

    /**
     * Constructor
     */
    @Inject
    public SpincastPebbleExtension(Provider<ITemplatingEngine> templatingEngineProvider) {
        this.templatingEngineProvider = templatingEngineProvider;
    }

    public ITemplatingEngine getTemplatingEngine() {
        if(this.templatingEngine == null) {
            this.templatingEngine = this.templatingEngineProvider.get();
        }
        return this.templatingEngine;
    }

    @Override
    public Map<String, Filter> getFilters() {
        return null;
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
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<String, Function>();

        Function function = getFieldErrorMessagesFunction();
        if(function != null) {
            functions.put(getFieldErrorMessagesFunctionName(), function);
        }

        function = getFieldValueFunction();
        if(function != null) {
            functions.put(getFieldValueFunctionName(), function);
        }

        function = getFieldCheckedFunction();
        if(function != null) {
            functions.put(getFieldCheckedFunctionName(), function);
        }

        function = getFieldGroupErrorClassFunction();
        if(function != null) {
            functions.put(getFieldGroupErrorClassFunctionName(), function);
        }

        function = getCheckedFunction();
        if(function != null) {
            functions.put(getCheckedFunctionName(), function);
        }

        function = getSelectedFunction();
        if(function != null) {
            functions.put(getSelectedFunctionName(), function);
        }

        return functions;
    }

    protected String getFieldErrorMessagesFunctionName() {
        return "fieldErrors";
    }

    protected String getFieldValueFunctionName() {
        return "fieldValue";
    }

    protected String getFieldCheckedFunctionName() {
        return "fieldChecked";
    }

    protected String getFieldGroupErrorClassFunctionName() {
        return "fieldSectionErrorClass";
    }

    protected String getCheckedFunctionName() {
        return "checked";
    }

    protected String getSelectedFunctionName() {
        return "selected";
    }

    /**
     * Warning! The resulting content will be a <code>SafeString</code>
     * so it won't be escaped!
     */
    protected Function getFieldErrorMessagesFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("fieldName", "valueOrIndex");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                EvaluationContext evaluationContext = (EvaluationContext)args.get("_context");

                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>)evaluationContext.getScopeChain().get("fields");
                if(fields == null || fields.size() == 0) {
                    return "";
                }

                String fieldName = (String)args.get("fieldName");
                if(fieldName == null) {
                    return "";
                }
                fieldName = fieldName.trim();

                @SuppressWarnings("unchecked")
                Map<String, Object> field = (Map<String, Object>)fields.get(fieldName);
                if(field == null) {
                    return "";
                }

                List<String> errors = null;
                Object valueOrIndex = args.get("valueOrIndex");
                if(valueOrIndex != null) {

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> valuesObjects = (List<Map<String, Object>>)field.get("values");
                    if(valuesObjects == null || valuesObjects.size() == 0) {
                        return "";
                    }

                    if(valueOrIndex instanceof Number) {

                        int valueIndex = Long.valueOf(valueOrIndex.toString()).intValue();
                        int pos = 0;
                        if(valueIndex > 0) {

                            if(valueIndex > (valuesObjects.size() - 1)) {
                                return "";
                            }
                            pos = valueIndex;
                        }

                        @SuppressWarnings("unchecked")
                        List<String> err = (List<String>)valuesObjects.get(pos).get("errors");
                        errors = err;
                    } else {
                        String valueStr = valueOrIndex.toString().trim();
                        for(Map<String, Object> valueObject : valuesObjects) {
                            Object value = valueObject.get("value");
                            if(valueStr.equals(value)) {
                                @SuppressWarnings("unchecked")
                                List<String> err = (List<String>)valueObject.get("errors");
                                errors = err;
                                break;
                            }
                        }
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    List<String> err = (List<String>)field.get("errors");
                    errors = err;
                }
                if(errors == null || errors.size() == 0) {
                    return "";
                }

                String html =
                        getTemplatingEngine().fromTemplate(getFieldErrorMessagesFunctionTemplatePath(),
                                                           SpincastStatics.params("errors", errors));

                return new SafeString(html);
            }
        };

        return function;
    }

    protected String getFieldErrorMessagesFunctionTemplatePath() {
        return "/spincast/spincast-plugins-pebble/spincastPebbleExtension/fieldErrorMessagesFunctionTemplate.html";
    }

    /**
     * The result *will* be escaped. If you don't want it to be escaped,
     * use the <code>| raw</code> filter after the function call.
     */
    protected Function getFieldValueFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("fieldName", "valueIndex");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                EvaluationContext evaluationContext = (EvaluationContext)args.get("_context");

                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>)evaluationContext.getScopeChain().get("fields");
                if(fields == null || fields.size() == 0) {
                    return "";
                }

                String fieldName = (String)args.get("fieldName");
                if(fieldName == null) {
                    return "";
                }
                fieldName = fieldName.trim();

                @SuppressWarnings("unchecked")
                Map<String, Object> field = (Map<String, Object>)fields.get(fieldName);
                if(field == null) {
                    return "";
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> valuesObjects = (List<Map<String, Object>>)field.get("values");
                if(valuesObjects == null || valuesObjects.size() == 0) {
                    return "";
                }

                Long valueIndex = (Long)args.get("valueIndex");
                int pos = 0;
                if(valueIndex != null) {
                    int valueIndexInt = valueIndex.intValue();
                    if(valueIndexInt > 0) {
                        if(valueIndexInt > (valuesObjects.size() - 1)) {
                            return "";
                        }
                        pos = valueIndexInt;
                    }
                }

                Map<String, Object> valueObject = valuesObjects.get(pos);
                Object value = valueObject.get("value");
                if(value == null) {
                    value = "";
                }

                return value;
            }
        };

        return function;
    }

    /**
     * Returns a boolean.
     */
    protected Function getFieldCheckedFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("fieldName", "option");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                EvaluationContext evaluationContext = (EvaluationContext)args.get("_context");

                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>)evaluationContext.getScopeChain().get("fields");
                if(fields == null || fields.size() == 0) {
                    return false;
                }

                String fieldName = (String)args.get("fieldName");
                if(fieldName == null) {
                    return false;
                }
                fieldName = fieldName.trim();

                String option = (String)args.get("option");
                if(option != null && fieldName.endsWith("[]")) {
                    option = option.trim();

                    fieldName = fieldName.substring(0, fieldName.length() - "[]".length());
                    fieldName = fieldName + "[" + option + "]";
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> field = (Map<String, Object>)fields.get(fieldName);
                if(field == null) {
                    return false;
                }

                String value = (String)field.get("value");
                if(value == null) {
                    return false;
                }

                if(option != null) {
                    return option.equals(value);
                } else {
                    return true;
                }
            }
        };

        return function;
    }

    /**
     * The result is a class name or an empty String and
     * will be escaped.
     */
    protected Function getFieldGroupErrorClassFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("fieldName");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                EvaluationContext evaluationContext = (EvaluationContext)args.get("_context");

                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>)evaluationContext.getScopeChain().get("fields");
                if(fields == null || fields.size() == 0) {
                    return "";
                }

                String fieldName = (String)args.get("fieldName");
                if(fieldName == null) {
                    return "";
                }
                fieldName = fieldName.trim();

                @SuppressWarnings("unchecked")
                Map<String, Object> field = (Map<String, Object>)fields.get(fieldName);
                if(field == null) {
                    return "";
                }

                Boolean value = (Boolean)field.get("isError");
                if(value == null || !value) {
                    return "";
                }

                return getFieldGroupErrorClassFunctionErrorClass();
            }
        };

        return function;
    }

    protected String getFieldGroupErrorClassFunctionErrorClass() {
        return "has-error";
    }

    protected Function getCheckedFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("bool");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                Object bool = args.get("bool");
                if(bool == null) {
                    return "";
                }

                if(Boolean.parseBoolean(bool.toString())) {
                    return "checked";
                } else {
                    return "";
                }
            }
        };

        return function;
    }

    protected Function getSelectedFunction() {

        Function function = new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("bool");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                Object bool = args.get("bool");
                if(bool == null) {
                    return "";
                }

                if(Boolean.parseBoolean(bool.toString())) {
                    return "selected";
                } else {
                    return "";
                }
            }
        };

        return function;
    }

}

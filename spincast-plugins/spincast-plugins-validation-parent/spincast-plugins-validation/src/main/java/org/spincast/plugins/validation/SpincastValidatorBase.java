package org.spincast.plugins.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.xml.IXmlManager;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * Validator base class implementation.
 */
public abstract class SpincastValidatorBase<T> implements IValidator {

    private final IValidationErrorFactory validationErrorFactory;
    private final ISpincastValidationConfig spincastBeanValidationConfig;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;

    private T objToValidate;
    private boolean validationDone = false;

    private final LinkedHashMap<String, List<IValidationError>> validationErrors = new LinkedHashMap<>();

    /**
     * Constructor
     */
    public SpincastValidatorBase(T objToValidate,
                                 SpincastValidatorBaseDeps spincastValidatorBaseDeps) {
        Objects.requireNonNull(objToValidate, "The object to validate can't be NULL");
        this.objToValidate = objToValidate;

        this.validationErrorFactory = spincastValidatorBaseDeps.getValidationErrorFactory();
        this.spincastBeanValidationConfig = spincastValidatorBaseDeps.getSpincastBeanValidationConfig();
        this.jsonManager = spincastValidatorBaseDeps.getJsonManager();
        this.xmlManager = spincastValidatorBaseDeps.getXmlManager();
    }

    protected T getObjToValidate() {
        return this.objToValidate;
    }

    protected IValidationErrorFactory getValidationErrorFactory() {
        return this.validationErrorFactory;
    }

    protected ISpincastValidationConfig getSpincastBeanValidationConfig() {
        return this.spincastBeanValidationConfig;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected boolean isValidationDone() {
        return this.validationDone;
    }

    /**
     * Override "validate()" instead of
     * this method, if you can.
     */
    protected void validateBase() {

        if(isValidationDone()) {
            return;
        }
        this.validationDone = true;

        validate();
    }

    @Override
    public boolean isValid() {
        validateBase();
        return getErrors().size() == 0;
    }

    @Override
    public void revalidate() {
        this.validationDone = false;
        this.validationErrors.clear();
    }

    @Override
    public Map<String, List<IValidationError>> getErrors() {
        validateBase();
        return this.validationErrors;
    }

    protected Map<String, List<IValidationError>> getErrorsMap() {
        return this.validationErrors;
    }

    @Override
    public List<IValidationError> getErrors(String fieldName) {
        return getErrorsMap().get(fieldName);
    }

    /**
     * Adds a validation error.
     */
    protected void addError(IValidationError error) {
        if(error == null) {
            return;
        }
        addErrors(Lists.newArrayList(error));
    }

    /**
     * Adds a validation error.
     */
    protected void addError(String fieldName, String errorType, String errorMessage) {

        Objects.requireNonNull(fieldName, "The field name can't be NULL");

        if(errorType == null) {
            errorType = "";
        }

        if(StringUtils.isBlank(errorMessage)) {
            errorMessage = getDefaultErrorMessage(fieldName);
        }

        IValidationError error = getValidationErrorFactory().create(fieldName, errorMessage, errorType);
        addError(error);
    }

    protected String getDefaultErrorMessage(String fieldName) {
        return getSpincastBeanValidationConfig().getDefaultGenericErrorMessage();
    }

    /**
     * Adds validation errors.
     */
    protected void addErrors(List<IValidationError> errors) {
        if(errors == null) {
            return;
        }
        for(IValidationError error : errors) {
            List<IValidationError> fieldErrors = getErrorsMap().get(error.getFieldName());
            if(fieldErrors == null) {
                fieldErrors = new ArrayList<IValidationError>();
                getErrorsMap().put(error.getFieldName(), fieldErrors);
            }
            fieldErrors.add(error);
        }
    }

    @Override
    public String getErrorsFormatted(FormatType formatType) {
        return getErrorsFormatted(null, formatType);
    }

    @Override
    public String getErrorsFormatted(String fieldName, FormatType formatType) {

        if(isValid()) {
            return null;
        }

        if(formatType == null) {
            formatType = FormatType.PLAIN_TEXT;
        }

        StringBuilder textBuilder = null;
        if(formatType == FormatType.PLAIN_TEXT || formatType == FormatType.HTML) {
            textBuilder = new StringBuilder();
        }

        IJsonObject jsonObject = null;
        if(formatType == FormatType.JSON || formatType == FormatType.XML) {
            jsonObject = getJsonManager().create();
        }

        if(fieldName == null) {
            Map<String, List<IValidationError>> errorsMap = getErrors();
            for(Entry<String, List<IValidationError>> entry : errorsMap.entrySet()) {
                addErrorsFormattedSpecificField(entry.getKey(), entry.getValue(), formatType, textBuilder, jsonObject);
            }
        } else {
            List<IValidationError> errorsMap = getErrors().get(fieldName);
            addErrorsFormattedSpecificField(fieldName, errorsMap, formatType, textBuilder, jsonObject);
        }

        if(formatType == FormatType.PLAIN_TEXT || formatType == FormatType.HTML) {
            return textBuilder.toString();
        } else if(formatType == FormatType.JSON) {
            return jsonObject.toJsonString(usePrettyJson());
        } else if(formatType == FormatType.XML) {
            return getXmlManager().toXml(jsonObject, usePrettyXml());
        } else {
            throw new RuntimeException("Unamanaged type: " + formatType);
        }
    }

    protected boolean usePrettyJson() {
        return true;
    }

    protected boolean usePrettyXml() {
        return true;
    }

    protected void addErrorsFormattedSpecificField(String fieldName,
                                                   List<IValidationError> errors,
                                                   FormatType formatType,
                                                   StringBuilder textBuilder,
                                                   IJsonObject jsonObject) {

        if(StringUtils.isBlank(fieldName) || errors == null || errors.size() == 0) {
            return;
        }

        if(formatType == FormatType.JSON || formatType == FormatType.XML) {
            jsonObject.put(fieldName, errors);
            return;
        }

        if(formatType == FormatType.PLAIN_TEXT) {

            addErrorsFormattedSpecificFieldTextPlain(fieldName, textBuilder, errors);

        } else if(formatType == FormatType.HTML) {

            addErrorsFormattedSpecificFieldHtml(fieldName, textBuilder, errors);

        } else {
            throw new RuntimeException("Type not managed here: " + formatType);
        }
    }

    protected void addErrorsFormattedSpecificFieldTextPlain(String fieldName,
                                                            StringBuilder textBuilder,
                                                            List<IValidationError> errors) {

        textBuilder.append("Field \"").append(fieldName).append("\"").append("\n");
        for(IValidationError error : errors) {
            textBuilder.append("    - ").append(error.getMessage()).append("\n");
        }
        textBuilder.append("\n");
    }

    protected void addErrorsFormattedSpecificFieldHtml(String fieldName,
                                                       StringBuilder textBuilder,
                                                       List<IValidationError> errors) {

        textBuilder.append("<li class=\"" + getCssClassForErrorField() + "\">").append(fieldName).append("\n");
        textBuilder.append("    <ul>\n");

        for(IValidationError error : errors) {
            textBuilder.append("        <li class=\"" + getCssClassForErrorMessage() + "\">").append(error.getMessage())
                       .append("</li>\n");
        }
        textBuilder.append("    </ul>\n");
        textBuilder.append("</li>\n");
    }

    /**
     * The css class to use for a field's &lt;li&gt; element.
     */
    protected String getCssClassForErrorField() {
        return "validationField";
    }

    /**
     * The css class to use for an error message's &lt;li&gt; element.
     */
    protected String getCssClassForErrorMessage() {
        return "validationError";
    }

    /**
     * Validates that a field is not null.
     */
    protected boolean validateNotNull(String fieldName, Object fieldValue) {
        return validateNotNull(fieldName, fieldValue, null);
    }

    /**
     * Validates that a field is not null.
     */
    protected boolean validateNotNull(String fieldName, Object fieldValue, String errorMessage) {

        if(fieldValue == null) {
            if(errorMessage == null) {
                errorMessage = getErrorMessageDefaultNotNull(fieldName);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_NOT_NULL);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultNotNull(String fieldName) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultNotNull();
    }

    /**
     * Validates that a field is not blank (null, empty or contain only spaces).
     */
    protected boolean validateNotBlank(String fieldName, String fieldValue) {
        return validateNotBlank(fieldName, fieldValue, null);
    }

    /**
     * Validates that a field is not null.
     */
    protected boolean validateNotBlank(String fieldName, String fieldValue, String errorMessage) {

        if(StringUtils.isBlank(fieldValue)) {
            if(errorMessage == null) {
                errorMessage = getErrorMessageDefaultNotBlank(fieldName);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_NOT_BLANK);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultNotBlank(String fieldName) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultNotBlank();
    }

    /**
     * Validates that a field has a minimum length.
     */
    protected boolean validateMinLength(String fieldName, String fieldValue, int minLength) {
        return validateMinLength(fieldName, fieldValue, Long.valueOf(minLength), null);
    }

    /**
     * Validates that a field has a minimum length.
     */
    protected boolean validateMinLength(String fieldName, String fieldValue, int minLength, String errorMessage) {
        return validateMinLength(fieldName,
                                 fieldValue,
                                 Long.valueOf(minLength),
                                 errorMessage);
    }

    /**
     * Validates that a field has a minimum length.
     */
    protected boolean validateMinLength(String fieldName, String fieldValue, long minLength) {
        return validateMinLength(fieldName, fieldValue, minLength, null);
    }

    /**
     * Validates that a field has a minimum length.
     */
    protected boolean validateMinLength(String fieldName, String fieldValue, long minLength, String errorMessage) {

        if(minLength < 0) {
            minLength = 0;
        }

        if(fieldValue == null || fieldValue.length() < minLength) {
            if(errorMessage == null) {
                errorMessage =
                        getErrorMessageDefaultMinLength(fieldName, minLength, (fieldValue == null ? 0 : fieldValue.length()));
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_MIN_LENGTH);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultMinLength(String fieldName, long minLength, long currentLength) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultMinLength(minLength, currentLength);
    }

    /**
     * Validates that a field has a maximum length.
     */
    protected boolean validateMaxLength(String fieldName, String fieldValue, int maxLength) {
        return validateMaxLength(fieldName, fieldValue, Long.valueOf(maxLength), null);
    }

    /**
     * Validates that a field has a maximum length.
     */
    protected boolean validateMaxLength(String fieldName, String fieldValue, int maxLength, String errorMessage) {
        return validateMaxLength(fieldName,
                                 fieldValue,
                                 Long.valueOf(maxLength),
                                 errorMessage);
    }

    /**
     * Validates that a field has a maximum length.
     */
    protected boolean validateMaxLength(String fieldName, String fieldValue, long maxLength) {
        return validateMaxLength(fieldName, fieldValue, maxLength, null);
    }

    /**
     * Validates that a field has a maximum length.
     */
    protected boolean validateMaxLength(String fieldName, String fieldValue, long maxLength, String errorMessage) {

        if(maxLength < 0) {
            maxLength = 0;
        }

        if(fieldValue != null && fieldValue.length() > maxLength) {
            if(errorMessage == null) {
                errorMessage = getErrorMessageDefaultMaxLength(fieldName, maxLength, fieldValue.length());
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_MAX_LENGTH);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultMaxLength(String fieldName, long maxLength, long currentLength) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultMaxLength(maxLength, currentLength);
    }

    /**
     * Validates that a field has a minimum size.
     */
    protected boolean validateMinSize(String fieldName, Integer fieldValue, int minSize) {
        return validateMinSize(fieldName, fieldValue == null ? null : Long.valueOf(fieldValue), Long.valueOf(minSize), null);
    }

    /**
     * Validates that a field has a minimum size.
     */
    protected boolean validateMinSize(String fieldName, Integer fieldValue, int minSize, String errorMessage) {
        return validateMinSize(fieldName,
                               fieldValue == null ? null : Long.valueOf(fieldValue),
                               Long.valueOf(minSize),
                               errorMessage);
    }

    /**
     * Validates that a field has a minimum size.
     */
    protected boolean validateMinSize(String fieldName, Long fieldValue, long minSize) {
        return validateMinSize(fieldName, fieldValue, minSize, null);
    }

    /**
     * Validates that a field has a minimum size.
     */
    protected boolean validateMinSize(String fieldName, Long fieldValue, long minSize, String errorMessage) {

        if(minSize < 0) {
            minSize = 0;
        }

        if(fieldValue == null || fieldValue < minSize) {
            if(errorMessage == null) {
                errorMessage =
                        getErrorMessageDefaultMinSize(fieldName, minSize, fieldValue);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_MIN_SIZE);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultMinSize(String fieldName, long minSize, Long currentSize) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultMinSize(minSize, (currentSize == null ? 0 : currentSize));
    }

    /**
     * Validates that a field has a maximum size.
     */
    protected boolean validateMaxSize(String fieldName, Integer fieldValue, int maxSize) {
        return validateMaxSize(fieldName, fieldValue == null ? null : Long.valueOf(fieldValue), Long.valueOf(maxSize), null);
    }

    /**
     * Validates that a field has a maximum size.
     */
    protected boolean validateMaxSize(String fieldName, Integer fieldValue, int maxSize, String errorMessage) {
        return validateMaxSize(fieldName,
                               fieldValue == null ? null : Long.valueOf(fieldValue),
                               Long.valueOf(maxSize),
                               errorMessage);
    }

    /**
     * Validates that a field has a maximum size.
     */
    protected boolean validateMaxSize(String fieldName, Long fieldValue, long maxSize) {
        return validateMinSize(fieldName, fieldValue, maxSize, null);
    }

    /**
     * Validates that a field has a minimum size.
     */
    protected boolean validateMaxSize(String fieldName, Long fieldValue, long maxSize, String errorMessage) {

        if(fieldValue != null && fieldValue > maxSize) {
            if(errorMessage == null) {
                errorMessage =
                        getErrorMessageDefaultMaxSize(fieldName, maxSize, fieldValue);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_MAX_SIZE);
            addError(error);
            return false;
        }
        return true;
    }

    protected String getErrorMessageDefaultMaxSize(String fieldName, long maxSize, Long currentSize) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultMaxSize(maxSize, (currentSize == null ? 0 : currentSize));
    }

    /**
     * Validates that a field matches a pattern.
     */
    protected boolean validatePattern(String fieldName, String fieldValue, String pattern) {
        return validatePattern(fieldName, fieldValue, pattern, true, null);
    }

    /**
     * Validates that a field matches a pattern or not.
     * 
     * @param mustMatch if <code>true</code>, the field must match the pattern, if
     * <code>false</code>, it must not match it.
     */
    protected boolean validatePattern(String fieldName, String fieldValue, String pattern, boolean mustMatch) {
        return validatePattern(fieldName, fieldValue, pattern, mustMatch, null);
    }

    /**
     * Validates that a field matches a pattern.
     */
    protected boolean validatePattern(String fieldName, String fieldValue, String pattern, String errorMessage) {
        return validatePattern(fieldName, fieldValue, pattern, true, errorMessage);
    }

    /**
     * Validates that a field matches a pattern or not.
     * 
     * @param mustMatch if <code>true</code>, the field must match the pattern, if
     * <code>false</code>, it must not match it.
     */
    protected boolean validatePattern(String fieldName, String fieldValue, String pattern, boolean mustMatch,
                                      String errorMessage) {

        Objects.requireNonNull(pattern, "The pattern can't be NULL");

        boolean patternError = false;
        if(fieldValue == null) {
            patternError = true;
        } else {
            boolean doesMatch = Pattern.matches(pattern, fieldValue);
            if((mustMatch && !doesMatch) || (!mustMatch && doesMatch)) {
                patternError = true;
            }
        }

        if(patternError) {
            if(errorMessage == null) {
                errorMessage = getErrorMessageDefaultPattern(fieldName, pattern);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_PATTERN);
            addError(error);
            return false;
        }

        return true;
    }

    protected String getErrorMessageDefaultPattern(String fieldName, String pattern) {
        return getSpincastBeanValidationConfig().getErrorMessageDefaultPattern(pattern);
    }

    /**
     * Validates that a field is a valid email address.
     */
    protected boolean validateEmail(String fieldName, String fieldValue) {
        return validateEmail(fieldName, fieldValue, null);
    }

    /**
     * Validates that a field is a valid email address.
     */
    protected boolean validateEmail(String fieldName, String fieldValue, String errorMessage) {

        EmailValidator emailValidator = getEmailValidator();
        if(fieldValue == null || !emailValidator.isValid(fieldValue)) {
            if(errorMessage == null) {
                errorMessage = getErrorMessageEmail(fieldName);
            }
            IValidationError error = getValidationErrorFactory().create(fieldName,
                                                                        errorMessage,
                                                                        IValidationError.VALIDATION_TYPE_EMAIL);
            addError(error);
            return false;
        }
        return true;
    }

    protected EmailValidator getEmailValidator() {
        return EmailValidator.getInstance();
    }

    protected String getErrorMessageEmail(String fieldName) {
        return getSpincastBeanValidationConfig().getErrorMessageEmail();
    }

    /**
     * The validation method to implement.
     */
    protected abstract void validate();

}

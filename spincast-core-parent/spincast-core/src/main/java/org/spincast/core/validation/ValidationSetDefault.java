package org.spincast.core.validation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exceptions.CantCompareException;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.collections.MapUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.validator.routines.EmailValidator;

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ValidationSetDefault implements ValidationSet {

    protected final Logger logger = LoggerFactory.getLogger(ValidationSetDefault.class);

    private final ValidationFactory validationFactory;
    private final SpincastDictionary spincastDictionary;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final ObjectConverter objectConverter;
    private boolean validationDone = false;
    private final LinkedHashMap<String, List<ValidationMessage>> messages;

    private SimpleValidator notBlankValidatorInfo;
    private SimpleValidator blankValidatorInfo;
    private SimpleValidator emailValidatorInfo;
    private SimpleValidator nullValidatorInfo;
    private SimpleValidator notNullValidatorInfo;

    /**
     * Constructor
     */
    @AssistedInject
    public ValidationSetDefault(@Assisted @Nullable LinkedHashMap<String, List<ValidationMessage>> messages,
                                ValidationFactory validationFactory,
                                SpincastDictionary spincastDictionary,
                                JsonManager jsonManager,
                                XmlManager xmlManager,
                                ObjectConverter objectConverter) {
        this.validationFactory = validationFactory;
        this.spincastDictionary = spincastDictionary;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.objectConverter = objectConverter;

        if (messages == null) {
            messages = new LinkedHashMap<String, List<ValidationMessage>>();
        }
        this.messages = messages;
    }

    /**
     * Constructor
     */
    @AssistedInject
    public ValidationSetDefault(ValidationFactory validationFactory,
                                SpincastDictionary spincastDictionary,
                                JsonManager jsonManager,
                                XmlManager xmlManager,
                                ObjectConverter objectConverter) {
        this((LinkedHashMap<String, List<ValidationMessage>>)null,
             validationFactory,
             spincastDictionary,
             jsonManager,
             xmlManager,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public ValidationSetDefault(@Assisted String key,
                                @Assisted List<ValidationMessage> messages,
                                ValidationFactory validationFactory,
                                SpincastDictionary spincastDictionary,
                                JsonManager jsonManager,
                                XmlManager xmlManager,
                                ObjectConverter objectConverter) {

        this(toLinkedHashMap(key, messages),
             validationFactory,
             spincastDictionary,
             jsonManager,
             xmlManager,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public ValidationSetDefault(@Assisted String key,
                                @Assisted ValidationMessage message,
                                ValidationFactory validationFactory,
                                SpincastDictionary spincastDictionary,
                                JsonManager jsonManager,
                                XmlManager xmlManager,
                                ObjectConverter objectConverter) {
        this(toLinkedHashMap(key, Lists.newArrayList(message)),
             validationFactory,
             spincastDictionary,
             jsonManager,
             xmlManager,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public ValidationSetDefault(@Assisted List<ValidationSet> existingResults,
                                ValidationFactory validationFactory,
                                SpincastDictionary spincastDictionary,
                                JsonManager jsonManager,
                                XmlManager xmlManager,
                                ObjectConverter objectConverter) {

        this(toMessages(existingResults),
             validationFactory,
             spincastDictionary,
             jsonManager,
             xmlManager,
             objectConverter);
    }

    protected static LinkedHashMap<String, List<ValidationMessage>> toLinkedHashMap(String key,
                                                                                    List<ValidationMessage> messages) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("The key can't be empty.");
        }

        LinkedHashMap<String, List<ValidationMessage>> map = new LinkedHashMap<String, List<ValidationMessage>>();
        map.put(key, messages);

        return map;
    }

    protected static LinkedHashMap<String, List<ValidationMessage>> toMessages(List<ValidationSet> existingResults) {

        if (existingResults == null) {
            return null;
        }

        LinkedHashMap<String, List<ValidationMessage>> map = new LinkedHashMap<String, List<ValidationMessage>>();

        if (existingResults != null) {
            for (ValidationSet validationResult : existingResults) {

                Map<String, List<ValidationMessage>> messagesMap = validationResult.getMessages();
                if (messagesMap != null) {
                    for (Entry<String, List<ValidationMessage>> entry : messagesMap.entrySet()) {

                        String key = entry.getKey();
                        List<ValidationMessage> messages = entry.getValue();
                        if (messages != null) {

                            List<ValidationMessage> messagesList = map.get(key);
                            if (messagesList == null) {
                                messagesList = new ArrayList<ValidationMessage>();
                                map.put(key, messagesList);
                            }
                            messagesList.addAll(messages);
                        }
                    }
                }
            }
        }

        return map;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    protected SpincastDictionary getSpincastDictionary() {
        return this.spincastDictionary;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }

    protected boolean isValidationDone() {
        return this.validationDone;
    }

    @Override
    public ValidationSet createValidationSet() {
        return getValidationFactory().createValidationSet();
    }

    protected String getDefaultErrorMessageText(String key) {
        return getSpincastDictionary().validation_generic_error_default_text();
    }

    @Override
    public ValidationSet addMessage(String validationKey, ValidationMessage message) {
        Objects.requireNonNull(message, "The message can't be NULL");

        List<ValidationMessage> fieldMessages = getMessages().get(validationKey);
        if (fieldMessages == null) {
            fieldMessages = new ArrayList<ValidationMessage>();
            getMessages().put(validationKey, fieldMessages);
        }
        fieldMessages.add(message);

        return getValidationFactory().createValidationSet(validationKey, message);
    }

    @Override
    public ValidationSet addMessage(String validationKey, ValidationLevel level, String code, String text) {

        Objects.requireNonNull(level, "The level can't be NULL");
        Objects.requireNonNull(validationKey, "The validation key can't be NULL");

        if (code == null) {
            code = "";
        }

        if (StringUtils.isBlank(text)) {
            text = getDefaultErrorMessageText(validationKey);
        }

        ValidationMessage message = getValidationFactory().createMessage(level, code, text);
        return addMessage(validationKey, message);
    }

    @Override
    public ValidationSet addError(String validationKey, String code, String text) {
        return addMessage(validationKey, ValidationLevel.ERROR, code, text);
    }

    @Override
    public ValidationSet addWarning(String validationKey, String code, String text) {
        return addMessage(validationKey, ValidationLevel.WARNING, code, text);
    }

    @Override
    public ValidationSet addSuccess(String validationKey, String code, String text) {
        return addMessage(validationKey, ValidationLevel.SUCCESS, code, text);
    }

    @Override
    public ValidationSet mergeValidationSet(ValidationSet set) {
        return mergeValidationSet(null, set);
    }

    @Override
    public ValidationSet mergeValidationSet(String validationKeyPrefix, ValidationSet set) {
        Objects.requireNonNull(set, "The validation set can't be NULL");

        Map<String, List<ValidationMessage>> messagesByKey = set.getMessages();
        if (messagesByKey != null) {
            for (Entry<String, List<ValidationMessage>> entry : messagesByKey.entrySet()) {
                List<ValidationMessage> messages = entry.getValue();
                if (messages != null) {
                    for (ValidationMessage message : messages) {
                        String validationKey = (validationKeyPrefix != null ? validationKeyPrefix : "") + entry.getKey();
                        addMessage(validationKey, message);
                    }
                }
            }
        }

        return this;
    }

    @Override
    public boolean hasMessages() {
        return getMessages().size() > 0;
    }

    @Override
    public boolean hasMessages(String key) {
        return getMessages(key).size() > 0;
    }

    @Override
    public boolean isWarning() {

        boolean hasWarnings = false;
        boolean hasErrors = false;

        for (List<ValidationMessage> messagesList : getMessages().values()) {

            if (messagesList != null) {
                for (ValidationMessage message : messagesList) {
                    if (message.getValidationLevel() == ValidationLevel.WARNING) {
                        hasWarnings = true;
                    } else if (message.getValidationLevel() == ValidationLevel.ERROR) {
                        hasErrors = true;
                    }
                }
            }
        }

        return hasWarnings && !hasErrors;
    }

    @Override
    public boolean isWarning(String key) {
        boolean hasWarnings = false;
        boolean hasErrors = false;

        for (ValidationMessage message : getMessages(key)) {
            if (message.getValidationLevel() == ValidationLevel.WARNING) {
                hasWarnings = true;
            } else if (message.getValidationLevel() == ValidationLevel.ERROR) {
                hasErrors = true;
            }
        }

        return hasWarnings && !hasErrors;
    }

    @Override
    public boolean isSuccess() {

        for (List<ValidationMessage> messagesList : getMessages().values()) {

            if (messagesList != null) {
                for (ValidationMessage message : messagesList) {
                    if (message.getValidationLevel() != ValidationLevel.SUCCESS) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean isSuccess(String key) {

        for (ValidationMessage message : getMessages(key)) {
            if (message.getValidationLevel() != ValidationLevel.SUCCESS) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isError() {

        for (List<ValidationMessage> messagesList : getMessages().values()) {

            if (messagesList != null) {
                for (ValidationMessage message : messagesList) {
                    if (message.getValidationLevel() == ValidationLevel.ERROR) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isError(String key) {

        for (ValidationMessage message : getMessages(key)) {
            if (message.getValidationLevel() == ValidationLevel.ERROR) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return !isError();
    }

    @Override
    public boolean isValid(String key) {
        return !isError(key);
    }

    @Override
    public Map<String, List<ValidationMessage>> getMessages() {
        return this.messages;
    }

    @Override
    public List<ValidationMessage> getMessages(String key) {

        List<ValidationMessage> messages = getMessages().get(key);
        if (messages == null) {
            messages = new ArrayList<ValidationMessage>();
        }

        return messages;
    }

    @Override
    public String getMessagesFormatted(ValidationMessageFormatType formatType) {
        return getMessagesFormatted(null, formatType);
    }

    @Override
    public String getMessagesFormatted(String jsonPath, ValidationMessageFormatType formatType) {

        if (getMessages().size() == 0) {
            return null;
        }

        if (formatType == null) {
            formatType = ValidationMessageFormatType.PLAIN_TEXT;
        }

        StringBuilder textBuilder = null;
        if (formatType == ValidationMessageFormatType.PLAIN_TEXT || formatType == ValidationMessageFormatType.HTML) {
            textBuilder = new StringBuilder();
        }

        JsonObject jsonObject = null;
        if (formatType == ValidationMessageFormatType.JSON || formatType == ValidationMessageFormatType.XML) {
            jsonObject = getJsonManager().create();
        }

        if (jsonPath == null) {
            Map<String, List<ValidationMessage>> messagesMap = getMessages();
            for (Entry<String, List<ValidationMessage>> entry : messagesMap.entrySet()) {
                addMessageFormattedSpecifickey(entry.getKey(), entry.getValue(), formatType, textBuilder, jsonObject);
            }
        } else {
            List<ValidationMessage> messages = getMessages().get(jsonPath);
            addMessageFormattedSpecifickey(jsonPath, messages, formatType, textBuilder, jsonObject);
        }

        if (formatType == ValidationMessageFormatType.PLAIN_TEXT || formatType == ValidationMessageFormatType.HTML) {
            return textBuilder.toString();
        } else if (formatType == ValidationMessageFormatType.JSON) {
            return jsonObject.toJsonString(usePrettyJson());
        } else if (formatType == ValidationMessageFormatType.XML) {
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

    protected void addMessageFormattedSpecifickey(String key,
                                                  List<ValidationMessage> messages,
                                                  ValidationMessageFormatType formatType,
                                                  StringBuilder textBuilder,
                                                  JsonObject jsonObject) {

        if (StringUtils.isBlank(key) || messages == null || messages.size() == 0) {
            return;
        }

        if (formatType == ValidationMessageFormatType.JSON || formatType == ValidationMessageFormatType.XML) {
            jsonObject.put(key, messages);
            return;
        }

        if (formatType == ValidationMessageFormatType.PLAIN_TEXT) {

            addMessagesFormattedSpecifickeyTextPlain(key, textBuilder, messages);

        } else if (formatType == ValidationMessageFormatType.HTML) {

            addMessagesFormattedSpecifickeyHtml(key, textBuilder, messages);

        } else {
            throw new RuntimeException("Type not managed here: " + formatType);
        }
    }

    protected void addMessagesFormattedSpecifickeyTextPlain(String key,
                                                            StringBuilder textBuilder,
                                                            List<ValidationMessage> messages) {

        textBuilder.append("key \"").append(key).append("\"").append("\n");
        for (ValidationMessage message : messages) {
            textBuilder.append("    - ").append(message.getText()).append("\n");
        }
        textBuilder.append("\n");
    }

    protected void addMessagesFormattedSpecifickeyHtml(String key,
                                                       StringBuilder textBuilder,
                                                       List<ValidationMessage> messages) {

        textBuilder.append("<li class=\"" + getCssClassForErrorkey() + "\">").append(key).append("\n");
        textBuilder.append("    <ul>\n");

        for (ValidationMessage message : messages) {

            String cssClass;
            if (message.getValidationLevel() == ValidationLevel.SUCCESS) {
                cssClass = getCssClassForSuccessMessage();
            } else if (message.getValidationLevel() == ValidationLevel.WARNING) {
                cssClass = getCssClassForWarningMessage();
            } else {
                cssClass = getCssClassForErrorMessage();
            }

            textBuilder.append("        <li class=\"" + cssClass + "\">").append(message.getText())
                       .append("</li>\n");
        }
        textBuilder.append("    </ul>\n");
        textBuilder.append("</li>\n");
    }

    /**
     * The css class to use for a key's &lt;li&gt; element.
     */
    protected String getCssClassForErrorkey() {
        return "validationKey";
    }

    /**
     * The css class to use for an error message's &lt;li&gt; element.
     */
    protected String getCssClassForErrorMessage() {
        return "validationError";
    }

    protected String getCssClassForSuccessMessage() {
        return "validationSuccess";
    }

    protected String getCssClassForWarningMessage() {
        return "validationWarning";
    }

    @Override
    public ValidationBuilderKey validation(SimpleValidator validator) {
        return getValidationFactory().createValidationBuilderKey(this, validator);
    }

    protected String getArrayItselfDefaultValidationKey(String baseKey) {

        if (baseKey == null) {
            baseKey = "";
        }

        return "_" + baseKey;
    }

    protected String getArrayItselfDefaultSuccessMessageText() {
        return getSpincastDictionary().validation_array_itself_success_message_default_text();
    }

    protected String getArrayItselfDefaultValidationCode() {
        return ValidationSet.VALIDATION_CODE_WHOLE_ARRAY_VALIDATION_RESULT_TYPE;
    }

    protected SimpleValidator getNotBlankValidatorInfo() {

        if (this.notBlankValidatorInfo == null) {

            this.notBlankValidatorInfo = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = elementToValidate != null &&
                                    (!(elementToValidate instanceof String) ||
                                     !StringUtils.isBlank((String)elementToValidate));
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_NOT_BLANK;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_not_blank_default_text();
                }
            };
        }

        return this.notBlankValidatorInfo;
    }

    @Override
    public ValidationBuilderKey validationNotBlank() {
        return getValidationFactory().createValidationBuilderKey(this, getNotBlankValidatorInfo());
    }

    protected SimpleValidator getBlankValidatorInfo() {

        if (this.blankValidatorInfo == null) {

            this.blankValidatorInfo = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = elementToValidate == null ||
                                    (elementToValidate instanceof String && StringUtils.isBlank((String)elementToValidate));
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_BLANK;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_blank_default_text();
                }
            };
        }

        return this.blankValidatorInfo;
    }

    @Override
    public ValidationBuilderKey validationBlank() {
        return getValidationFactory().createValidationBuilderKey(this, getBlankValidatorInfo());
    }

    protected SimpleValidator getEmailValidatorInfo() {

        if (this.emailValidatorInfo == null) {

            this.emailValidatorInfo = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = elementToValidate != null &&
                                    elementToValidate instanceof String &&
                                    EmailValidator.getInstance().isValid((String)elementToValidate);
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_EMAIL;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_email_default_text();
                }
            };
        }

        return this.emailValidatorInfo;
    }

    @Override
    public ValidationBuilderKey validationEmail() {
        return getValidationFactory().createValidationBuilderKey(this, getEmailValidatorInfo());
    }

    protected SimpleValidator getNullValidatorInfo() {

        if (this.nullValidatorInfo == null) {

            this.nullValidatorInfo = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = (elementToValidate == null);
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_NULL;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_null_default_text();
                }
            };
        }

        return this.nullValidatorInfo;
    }

    @Override
    public ValidationBuilderKey validationNull() {
        return getValidationFactory().createValidationBuilderKey(this, getNullValidatorInfo());
    }

    protected SimpleValidator getNotNullValidatorInfo() {

        if (this.notNullValidatorInfo == null) {

            this.notNullValidatorInfo = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = (elementToValidate != null);
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_NOT_NULL;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_not_null_default_text();
                }
            };
        }

        return this.notNullValidatorInfo;
    }

    @Override
    public ValidationBuilderKey validationNotNull() {
        return getValidationFactory().createValidationBuilderKey(this, getNotNullValidatorInfo());
    }

    protected SimpleValidator getPatternValidator(final String pattern) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                boolean valid = elementToValidate != null &&
                                Pattern.matches(pattern, elementToValidate.toString());
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_PATTERN;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_pattern_default_text(pattern);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationPattern(String pattern) {
        return getValidationFactory().createValidationBuilderKey(this, getPatternValidator(pattern));
    }

    protected SimpleValidator getNotPatternValidator(final String pattern) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                boolean valid = elementToValidate == null ||
                                !Pattern.matches(pattern, elementToValidate.toString());
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_NOT_PATTERN;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_not_pattern_default_text(pattern);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationNotPattern(String pattern) {
        return getValidationFactory().createValidationBuilderKey(this, getNotPatternValidator(pattern));
    }

    protected Integer getSize(Object element, boolean ignoreNullValues) {

        if (element == null) {
            return null;
        } else if (element instanceof JsonObject) {

            if (!ignoreNullValues) {
                return ((JsonObject)element).size();
            }

            int size = 0;
            for (Entry<String, Object> entry : ((JsonObject)element)) {
                if (entry != null && entry.getValue() != null) {
                    size++;
                }
            }

            return size;

        } else if (element instanceof JsonArray) {

            if (!ignoreNullValues) {
                return ((JsonArray)element).size();
            }

            int size = 0;
            for (Object oneElement : ((JsonArray)element)) {
                if (oneElement != null) {
                    size++;
                }
            }
            return size;

        } else if (element instanceof byte[]) {

            if (!ignoreNullValues) {
                return ((byte[])element).length;
            }

            int size = 0;
            for (Object oneElement : ((byte[])element)) {
                if (oneElement != null) {
                    size++;
                }
            }
            return size;

        } else if (element instanceof Object[]) {

            if (!ignoreNullValues) {
                return ((Object[])element).length;
            }

            int size = 0;
            for (Object oneElement : ((Object[])element)) {
                if (oneElement != null) {
                    size++;
                }
            }
            return size;

        } else if (element instanceof Collection<?>) {

            if (!ignoreNullValues) {
                return ((Collection<?>)element).size();
            }

            int size = 0;
            for (Object oneElement : ((Collection<?>)element)) {
                if (oneElement != null) {
                    size++;
                }
            }
            return size;
        } else {
            return null;
        }
    }

    protected SimpleValidator getSizeValidator(final int size, final boolean ignoreNullValues) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                if (elementToValidate == null) {
                    return false;
                }

                Integer actualSize = getSize(elementToValidate, ignoreNullValues);
                boolean valid = actualSize != null && actualSize.equals(size);
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_SIZE;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                Integer actualSize = getSize(elementToValidate, ignoreNullValues);
                return getSpincastDictionary().validation_size_default_text(size, actualSize != null ? actualSize : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationSize(int size, boolean ignoreNullValues) {
        return getValidationFactory().createValidationBuilderKey(this, getSizeValidator(size, ignoreNullValues));
    }

    protected SimpleValidator getMinSizeValidator(final int minSize, final boolean ignoreNullValues) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object element) {

                if (element == null) {
                    return false;
                }

                Integer size = getSize(element, ignoreNullValues);
                boolean valid = size != null && size >= minSize;
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_MIN_SIZE;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                Integer size = getSize(elementToValidate, ignoreNullValues);
                return getSpincastDictionary().validation_min_size_default_text(minSize, size != null ? size : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues) {
        return getValidationFactory().createValidationBuilderKey(this, getMinSizeValidator(minSize, ignoreNullValues));
    }

    protected SimpleValidator getMaxSizeValidator(final int maxSize, final boolean ignoreNullValues) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object element) {

                if (element == null) {
                    return true;
                }

                Integer size = getSize(element, ignoreNullValues);
                boolean valid = size != null && size <= maxSize;
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_MAX_SIZE;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                Integer size = getSize(elementToValidate, ignoreNullValues);
                return getSpincastDictionary().validation_max_size_default_text(maxSize, size != null ? size : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues) {
        return getValidationFactory().createValidationBuilderKey(this, getMaxSizeValidator(maxSize, ignoreNullValues));
    }

    protected SimpleValidator getLengthValidator(final int length) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object element) {

                boolean valid = element != null && element.toString().length() == length;
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_LENGTH;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary()
                                              .validation_length_default_text(length,
                                                                              elementToValidate != null ? elementToValidate.toString()
                                                                                                                           .length()
                                                                                                        : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationLength(int length) {
        return getValidationFactory().createValidationBuilderKey(this, getLengthValidator(length));
    }

    protected SimpleValidator getMinLengthValidator(final int minLength) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object element) {

                boolean valid = element != null && element.toString().length() >= minLength;
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_MIN_LENGTH;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary()
                                              .validation_min_length_default_text(minLength,
                                                                                  elementToValidate != null ? elementToValidate.toString()
                                                                                                                               .length()
                                                                                                            : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationMinLength(int minLength) {
        return getValidationFactory().createValidationBuilderKey(this, getMinLengthValidator(minLength));
    }

    protected SimpleValidator getMaxLengthValidator(final int maxLength) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object element) {

                boolean valid = element == null || element.toString().length() <= maxLength;
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_MAX_LENGTH;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary()
                                              .validation_max_length_default_text(maxLength,
                                                                                  elementToValidate != null ? elementToValidate.toString()
                                                                                                                               .length()
                                                                                                            : 0);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationMaxLength(int maxLength) {
        return getValidationFactory().createValidationBuilderKey(this, getMaxLengthValidator(maxLength));
    }

    protected SimpleValidator getEquivalentValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                boolean valid = getObjectConverter().isEquivalent(elementToValidate, reference);
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_EQUIVALENT;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_equivalent_default_text(elementToValidate, reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationEquivalent(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getEquivalentValidator(reference));
    }

    protected SimpleValidator getNotEquivalentValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                boolean valid = !getObjectConverter().isEquivalent(elementToValidate, reference);
                return valid;
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_NOT_EQUIVALENT;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_not_equivalent_default_text(elementToValidate, reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationNotEquivalent(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getNotEquivalentValidator(reference));
    }

    protected SimpleValidator getLessValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                try {
                    int result = getObjectConverter().compareTo(elementToValidate, reference);
                    return result < 0;
                } catch (CantCompareException ex) {
                    return false;
                }
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_LESS;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_less_default_text(elementToValidate, reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationLess(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getLessValidator(reference));
    }

    protected SimpleValidator getGreaterValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                try {
                    int result = getObjectConverter().compareTo(elementToValidate, reference);
                    return result > 0;
                } catch (CantCompareException ex) {
                    return false;
                }
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_GREATER;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_greater_default_text(elementToValidate, reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationGreater(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getGreaterValidator(reference));
    }

    protected SimpleValidator getEquivalentOrLessValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                try {
                    int result = getObjectConverter().compareTo(elementToValidate, reference);
                    return result <= 0;
                } catch (CantCompareException ex) {
                    return false;
                }
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_EQUIVALENT_OR_LESS;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_equivalent_or_less_default_text(elementToValidate, reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationEquivalentOrLess(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getEquivalentOrLessValidator(reference));
    }

    protected SimpleValidator getEquivalentOrGreaterValidator(final Object reference) {

        return new SimpleValidator() {

            @Override
            public boolean validate(Object elementToValidate) {

                try {
                    int result = getObjectConverter().compareTo(elementToValidate, reference);
                    return result >= 0;
                } catch (CantCompareException ex) {
                    return false;
                }
            }

            @Override
            public String getCode() {
                return VALIDATION_CODE_EQUIVALENT_OR_GREATER;
            }

            @Override
            public String getSuccessMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_success_message_default_text();
            }

            @Override
            public String getFailMessage(Object elementToValidate) {
                return getSpincastDictionary().validation_equivalent_or_greater_default_text(elementToValidate,
                                                                                             reference);
            }
        };
    }

    @Override
    public ValidationBuilderKey validationEquivalentOrGreater(Object reference) {
        return getValidationFactory().createValidationBuilderKey(this, getEquivalentOrGreaterValidator(reference));
    }

    @Override
    public JsonObject convertToJsonObject() {

        JsonObject obj = getJsonManager().create();

        boolean formIsValid = true;
        boolean formHasSuccesses = false;
        boolean formHasWarnings = false;
        boolean formHasErrors = false;

        for (Entry<String, List<ValidationMessage>> entry : getMessages().entrySet()) {

            JsonObject field = getJsonManager().create();

            boolean keyIsValid = true;
            boolean keyHasSuccessMessages = false;
            boolean keyHasWarningMessages = false;
            boolean keyHasErrorMessages = false;

            JsonArray messages = getJsonManager().createArray();
            for (ValidationMessage validationMessage : entry.getValue()) {

                JsonObject message = validationMessage.convertToJsonObject();

                if (validationMessage.getValidationLevel() == ValidationLevel.ERROR) {
                    keyIsValid = false;
                    keyHasErrorMessages = true;
                    formIsValid = false;
                    formHasErrors = true;
                } else if (validationMessage.getValidationLevel() == ValidationLevel.WARNING) {
                    keyHasWarningMessages = true;
                    formHasWarnings = true;
                } else if (validationMessage.getValidationLevel() == ValidationLevel.SUCCESS) {
                    keyHasSuccessMessages = true;
                    formHasSuccesses = true;
                } else {
                    throw new RuntimeException("Level not managed : " + validationMessage.getValidationLevel());
                }
                messages.add(message);
            }
            field.put("messages", messages);
            field.put("isValid", keyIsValid);
            field.put("hasErrors", keyHasErrorMessages);
            field.put("hasWarnings", keyHasWarningMessages);
            field.put("hasSuccesses", keyHasSuccessMessages);

            obj.putNoKeyParsing(entry.getKey(), field);
        }

        //==========================================
        // Results for the Validation Set itself.
        //==========================================
        JsonObject setItself = getJsonManager().create();
        setItself.put("isValid", formIsValid);
        setItself.put("hasSuccesses", formHasSuccesses);
        setItself.put("hasWarnings", formHasWarnings);
        setItself.put("hasErrors", formHasErrors);
        obj.putNoKeyParsing(getSetItselfValidationKey(), setItself);

        // We make it immutable
        return obj.clone(false);
    }

    protected String getSetItselfValidationKey() {
        return "_";
    }

    @Override
    public String toString() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        MapUtils.debugPrint(ps, "messages", getMessages());
        String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        return content;
    }

    @Override
    public ValidationSet prefixValidationKeys(String prefix) {

        if (prefix == null || "".equals(prefix)) {
            return this;
        }

        Map<String, List<ValidationMessage>> messagesPrefixed = new HashMap<>(getMessages());
        this.messages.clear();

        for (Entry<String, List<ValidationMessage>> entry : messagesPrefixed.entrySet()) {
            this.messages.put(prefix + entry.getKey(), entry.getValue());
        }

        return this;
    }

}

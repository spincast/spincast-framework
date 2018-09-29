package org.spincast.core.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationLevel;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationMessageFormatType;
import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.Validators;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class FormDefault extends JsonObjectDefault implements Form {

    public static final String ELEMENT_KEYS_IS_VALID = "isValid";
    public static final String ELEMENT_KEYS_HAS_SUCCESSES = "hasSuccesses";
    public static final String ELEMENT_KEYS_HAS_WARNINGS = "hasWarnings";
    public static final String ELEMENT_KEYS_HAS_ERRORS = "hasErrors";

    private final String formName;
    private final JsonObject initialFormData;
    private final Dictionary dictionary;
    private LinkedHashMap<String, List<ValidationMessage>> messages;
    private final XmlManager xmlManager;
    private final SpincastConfig spincastConfig;
    private final Validators validators;
    private JsonObject validationJsonObject;
    private final FormFactory formFactory;
    private final ValidationFactory validationFactory;

    @AssistedInject
    public FormDefault(@Assisted String formName,
                       @Assisted @Nullable JsonObject initialFormData,
                       JsonManager jsonManager,
                       SpincastUtils spincastUtils,
                       ObjectConverter objectConverter,
                       Dictionary dictionary,
                       XmlManager xmlManager,
                       SpincastConfig spincastConfig,
                       Validators validators,
                       FormFactory formFactory,
                       ValidationFactory validationFactory) {
        super((Map<String, Object>)null, true, jsonManager, spincastUtils, objectConverter);

        formName = formName.trim();

        this.formName = formName;
        this.initialFormData = initialFormData;
        this.dictionary = dictionary;
        this.xmlManager = xmlManager;
        this.spincastConfig = spincastConfig;
        this.validators = validators;
        this.formFactory = formFactory;
        this.validationFactory = validationFactory;
    }

    @Inject
    protected void init() {
        if (this.initialFormData != null) {
            merge(this.initialFormData.clone(true));
        }
    }

    @Override
    public String getFormName() {
        return this.formName;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    @Override
    public Validators validators() {
        return this.validators;
    }

    protected String getDefaultErrorMessageText(String key) {
        return getDictionary().get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_VALIDATION_GENERICERROR_DEFAULTTEXT);
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

    protected String getWholeValidationKey() {

        if (StringUtils.isBlank(getFormName())) {
            return "_";
        }
        return getFormName() + "._";
    }

    @Override
    public JsonObject getValidationResultAsJsonObject() {
        if (this.validationJsonObject == null) {
            this.validationJsonObject = getJsonManager().create();

            JsonObject wholeValidation = getJsonManager().create();
            this.validationJsonObject.setNoKeyParsing(getWholeValidationKey(), wholeValidation);
            wholeValidation.set(ELEMENT_KEYS_IS_VALID, true);
            wholeValidation.set(ELEMENT_KEYS_HAS_SUCCESSES, false);
            wholeValidation.set(ELEMENT_KEYS_HAS_WARNINGS, false);
            wholeValidation.set(ELEMENT_KEYS_HAS_ERRORS, false);
        }
        return this.validationJsonObject;
    }

    @Override
    public void setValidationObject(JsonObject validationObject) {

        if (validationObject == null) {
            validationObject = getJsonManager().create();
        }

        JsonObject currentValidationObject = getValidationResultAsJsonObject();
        validationObject.merge(currentValidationObject);
        this.validationJsonObject = validationObject;
    }

    @Override
    public void addMessage(String validationKey, ValidationMessage message) {
        Objects.requireNonNull(message, "The message can't be NULL");

        //==========================================
        // Adds as typed object
        //==========================================
        List<ValidationMessage> messages = getMessages().get(validationKey);
        if (messages == null) {
            messages = new ArrayList<ValidationMessage>();
            getMessages().put(validationKey, messages);
        }
        messages.add(message);

        //==========================================
        // Adds as JsonObject
        //==========================================
        String validationKeyPrefixed = validationKey;
        if (!StringUtils.isBlank(getFormName())) {
            validationKeyPrefixed = getFormName() + "." + validationKeyPrefixed;
        }

        JsonObject validationForKey = getValidationResultAsJsonObject().getJsonObjectNoKeyParsing(validationKeyPrefixed);
        if (validationForKey == null) {
            validationForKey = getJsonManager().create();
            getValidationResultAsJsonObject().setNoKeyParsing(validationKeyPrefixed, validationForKey);
        }

        JsonArray messagesForKey = validationForKey.getJsonArrayOrEmpty("messages", true);
        messagesForKey.add(message.convertToJsonObject());
        validationForKey.set(ELEMENT_KEYS_IS_VALID,
                             validationForKey.getBoolean(ELEMENT_KEYS_IS_VALID, true) && !message.isError());
        validationForKey.set(ELEMENT_KEYS_HAS_SUCCESSES,
                             validationForKey.getBoolean(ELEMENT_KEYS_HAS_SUCCESSES, false) || message.isSuccess());
        validationForKey.set(ELEMENT_KEYS_HAS_WARNINGS,
                             validationForKey.getBoolean(ELEMENT_KEYS_HAS_WARNINGS, false) || message.isWarning());
        validationForKey.set(ELEMENT_KEYS_HAS_ERRORS,
                             validationForKey.getBoolean(ELEMENT_KEYS_HAS_ERRORS, false) || message.isError());

        //==========================================
        // Results for the whole validation.
        //==========================================
        JsonObject wholeValidation = getValidationResultAsJsonObject().getJsonObjectNoKeyParsing(getWholeValidationKey());
        wholeValidation.set(ELEMENT_KEYS_IS_VALID, wholeValidation.getBoolean(ELEMENT_KEYS_IS_VALID, true) && !message.isError());
        wholeValidation.set(ELEMENT_KEYS_HAS_SUCCESSES,
                            wholeValidation.getBoolean(ELEMENT_KEYS_HAS_SUCCESSES, false) || message.isSuccess());
        wholeValidation.set(ELEMENT_KEYS_HAS_WARNINGS,
                            wholeValidation.getBoolean(ELEMENT_KEYS_HAS_WARNINGS, false) || message.isWarning());
        wholeValidation.set(ELEMENT_KEYS_HAS_ERRORS,
                            wholeValidation.getBoolean(ELEMENT_KEYS_HAS_ERRORS, false) || message.isError());
    }

    @Override
    public void addMessage(String validationKey, ValidationLevel level, String code, String text) {
        addMessage(validationKey, level, code, text, true);
    }

    @Override
    public void addMessage(String validationKey, ValidationLevel level, String code, String text, boolean htmlEscape) {

        Objects.requireNonNull(level, "The level can't be NULL");
        Objects.requireNonNull(validationKey, "The validation key can't be NULL");

        if (code == null) {
            code = "";
        }

        if (StringUtils.isBlank(text)) {
            text = getDefaultErrorMessageText(validationKey);
        }

        ValidationMessage message = getValidationFactory().createMessage(level, code, text, htmlEscape);
        addMessage(validationKey, message);
    }

    @Override
    public void addError(String validationKey, String code, String text) {
        addError(validationKey, code, text, true);
    }

    @Override
    public void addError(String validationKey, String code, String text, boolean htmlEscape) {
        addMessage(validationKey, ValidationLevel.ERROR, code, text, htmlEscape);
    }

    @Override
    public void addWarning(String validationKey, String code, String text) {
        addWarning(validationKey, code, text, true);
    }

    @Override
    public void addWarning(String validationKey, String code, String text, boolean htmlEscape) {
        addMessage(validationKey, ValidationLevel.WARNING, code, text, htmlEscape);
    }

    @Override
    public void addSuccess(String validationKey, String code, String text) {
        addSuccess(validationKey, code, text, true);
    }

    @Override
    public void addSuccess(String validationKey, String code, String text, boolean htmlEscape) {
        addMessage(validationKey, ValidationLevel.SUCCESS, code, text, htmlEscape);
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
    public boolean isValid(String... validationKeys) {

        if (validationKeys == null || validationKeys.length == 0) {
            return isValid();
        }

        for (String key : validationKeys) {
            if (isError(key)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Map<String, List<ValidationMessage>> getMessages() {
        if (this.messages == null) {
            this.messages = new LinkedHashMap<String, List<ValidationMessage>>();
        }
        return this.messages;
    }

    @Override
    public List<ValidationMessage> getMessages(String key) {

        List<ValidationMessage> messages = getMessages().get(key);
        if (messages == null) {
            messages = Collections.emptyList();
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
            jsonObject.set(key, messages);
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
    public ValidationSet createNewValidationSet() {
        return getValidationFactory().createValidationSet();
    }

    @Override
    public ValidationSet mergeValidationSet(ValidationSet validationSet) {
        return mergeValidationSet("", validationSet);
    }

    @Override
    public ValidationSet mergeValidationSet(String validationKeyPrefix, ValidationSet validationSet) {

        if (validationSet == null || validationSet.getMessages() == null) {
            return this;
        }

        if (StringUtils.isBlank(validationKeyPrefix)) {
            validationKeyPrefix = "";
        }

        for (Entry<String, List<ValidationMessage>> entry : validationSet.getMessages().entrySet()) {

            String validationKey = entry.getKey();

            //==========================================
            // A Validation message may declare its validation
            // key as been empty, in case it want itself to be merge
            // at the parent's level.
            //==========================================
            if (StringUtils.isBlank(validationKey)) {
                validationKey = validationKeyPrefix + validationKey;
            } else {
                if (validationKeyPrefix.endsWith(".")) {
                    validationKey = validationKeyPrefix + validationKey;

                } else {
                    validationKey = validationKeyPrefix + "." + validationKey;
                }
            }

            List<ValidationMessage> messages = entry.getValue();
            if (messages != null) {
                for (ValidationMessage message : messages) {
                    addMessage(validationKey, message);
                }
            }
        }

        return this;
    }

    @Override
    public int size() {
        return getMessages().size();
    }

}

package org.spincast.core.validation;

import java.util.List;

import javax.annotation.Nullable;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonManager;
import org.spincast.shaded.org.apache.commons.collections.CollectionUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public abstract class ValidationBuilderCoreBaseDefault<T extends ValidationBuilderCoreBase<?>> {

    private final SpincastDictionary spincastDictionary;
    private final ValidationSet validationSet;
    private final SimpleValidator simpleValidator;
    private final String validationKey;
    private final Object elementToValidate;
    private final ValidationFactory validationFactory;
    private final JsonManager jsonManager;

    private String code;
    private boolean addMessageOnSuccess = false;
    private String successMessageText;
    private boolean treatErrorAsWarning = false;
    private String errorMessageText;

    @AssistedInject
    public ValidationBuilderCoreBaseDefault(@Assisted ValidationSet validationSet,
                                            @Assisted SimpleValidator validator,
                                            @Assisted String validationKey,
                                            @Assisted @Nullable Object elementToValidate,
                                            ValidationFactory validationFactory,
                                            SpincastDictionary spincastDictionary,
                                            JsonManager jsonManager) {
        this.validationSet = validationSet;
        this.simpleValidator = validator;
        this.validationKey = validationKey;
        this.elementToValidate = elementToValidate;
        this.validationFactory = validationFactory;
        this.spincastDictionary = spincastDictionary;
        this.jsonManager = jsonManager;
    }

    protected ValidationSet getValidationSet() {
        return this.validationSet;
    }

    protected SimpleValidator getSimpleValidator() {
        return this.simpleValidator;
    }

    protected String getValidationKey() {
        return this.validationKey;
    }

    protected Object getElementToValidate() {
        return this.elementToValidate;
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

    public T code(String code) {
        this.code = code;

        @SuppressWarnings("unchecked")
        T asT = (T)this;
        return asT;
    }

    protected String getCode() {
        return this.code;
    }

    public T addMessageOnSuccess() {
        this.addMessageOnSuccess = true;

        @SuppressWarnings("unchecked")
        T asT = (T)this;
        return asT;
    }

    protected boolean isAddMessageOnSuccess() {
        return this.addMessageOnSuccess;
    }

    public T addMessageOnSuccess(String customSuccessMessageText) {
        this.addMessageOnSuccess = true;
        this.successMessageText = customSuccessMessageText;

        @SuppressWarnings("unchecked")
        T asT = (T)this;
        return asT;
    }

    protected String getSuccessMessageText() {
        return this.successMessageText;
    }

    public T treatErrorAsWarning() {
        this.treatErrorAsWarning = true;

        @SuppressWarnings("unchecked")
        T asT = (T)this;
        return asT;
    }

    protected ValidationLevel getFailLevel() {
        return isTreatErrorAsWarning() ? ValidationLevel.WARNING : ValidationLevel.ERROR;
    }

    protected boolean isTreatErrorAsWarning() {
        return this.treatErrorAsWarning;
    }

    public T failMessageText(String customErrorMessageText) {
        this.errorMessageText = customErrorMessageText;

        @SuppressWarnings("unchecked")
        T asT = (T)this;
        return asT;
    }

    protected String getErrorMessageText() {
        return this.errorMessageText;
    }

    public ValidationSet validate() {
        return validate(null);
    }

    public ValidationSet validate(boolean onlyIfNoMessageYet) {
        return validate(ValidationLevel.SUCCESS);
    }

    public ValidationSet validate(ValidationLevel onlyIfNoMessageAtThisLevelOrHigherYet) {
        return validateElement(getValidationKey(), getElementToValidate(), onlyIfNoMessageAtThisLevelOrHigherYet);
    }

    protected ValidationSet validateElement(String validationKey, Object elementToValidate,
                                            ValidationLevel onlyIfNoMessageAtThisLevelOrHigherYet) {

        if(onlyIfNoMessageAtThisLevelOrHigherYet != null) {

            List<ValidationMessage> messages = getValidationSet().getMessages(validationKey);

            if(!CollectionUtils.isEmpty(messages)) {

                if(onlyIfNoMessageAtThisLevelOrHigherYet == ValidationLevel.SUCCESS) {
                    return getValidationFactory().createValidationSet();
                }

                for(ValidationMessage message : messages) {
                    ValidationLevel level = message.getValidationLevel();
                    if(level == ValidationLevel.ERROR) {
                        return getValidationFactory().createValidationSet();
                    } else if(level == ValidationLevel.WARNING &&
                              onlyIfNoMessageAtThisLevelOrHigherYet == ValidationLevel.WARNING) {
                        return getValidationFactory().createValidationSet();
                    }
                }
            }
        }

        boolean valid = getSimpleValidator().validate(elementToValidate);

        if(valid && !isAddMessageOnSuccess()) {
            return getValidationFactory().createValidationSet();
        }

        String code = getCode() != null ? getCode() : getSimpleValidator().getCode();

        ValidationLevel level = valid ? ValidationLevel.SUCCESS : getFailLevel();

        String messageText;
        if(valid) {
            messageText =
                    getSuccessMessageText() != null ? getSuccessMessageText()
                                                    : getSimpleValidator().getSuccessMessage(elementToValidate);
        } else {
            messageText =
                    getErrorMessageText() != null ? getErrorMessageText()
                                                  : getSimpleValidator().getFailMessage(elementToValidate);
        }

        ValidationMessage message = getValidationFactory().createMessage(level,
                                                                         code,
                                                                         messageText);

        return getValidationSet().addMessage(validationKey, message);
    }
}

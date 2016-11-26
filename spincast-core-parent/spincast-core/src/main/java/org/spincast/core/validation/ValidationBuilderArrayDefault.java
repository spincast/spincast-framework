package org.spincast.core.validation;

import javax.annotation.Nullable;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ValidationBuilderArrayDefault extends ValidationBuilderCoreBaseDefault<ValidationBuilderArray>
                                           implements ValidationBuilderArray {

    private boolean arrayItselfAddSuccessMessage = false;
    private boolean arrayItselfAddFailMessage = false;
    private String arrayItselfAddSuccessMessageValidationKey;
    private String arrayItselfAddFailMessageValidationKey;
    private String arrayItselfCustomFailMessageText;
    private String arrayItselfCustomSuccessMessageText;

    private final boolean elementWasNotAnArray;

    @AssistedInject
    public ValidationBuilderArrayDefault(@Assisted ValidationSet validationSet,
                                         @Assisted SimpleValidator validator,
                                         @Assisted String validationKey,
                                         @Assisted @Nullable JsonArray array,
                                         ValidationFactory validationFactory,
                                         SpincastDictionary spincastDictionary,
                                         JsonManager jsonManager) {
        this(validationSet,
             validator,
             validationKey,
             array,
             false,
             validationFactory,
             spincastDictionary,
             jsonManager);
    }

    @AssistedInject
    public ValidationBuilderArrayDefault(@Assisted ValidationSet validationSet,
                                         @Assisted SimpleValidator validator,
                                         @Assisted String validationKey,
                                         @Assisted @Nullable JsonArray array,
                                         @Assisted boolean elementWasNotAnArray,
                                         ValidationFactory validationFactory,
                                         SpincastDictionary spincastDictionary,
                                         JsonManager jsonManager) {
        super(validationSet,
              validator,
              validationKey,
              array,
              validationFactory,
              spincastDictionary,
              jsonManager);
        this.elementWasNotAnArray = elementWasNotAnArray;
    }

    protected boolean isElementWasNotAnArray() {
        return this.elementWasNotAnArray;
    }

    protected String getArrayItselfCustomErrorMessageText() {
        return this.arrayItselfCustomFailMessageText;
    }

    protected String getArrayItselfAddSuccessMessageValidationKey() {
        return this.arrayItselfAddSuccessMessageValidationKey;
    }

    protected String getArrayItselfAddFailMessageValidationKey() {
        return this.arrayItselfAddFailMessageValidationKey;
    }

    @Override
    public ValidationBuilderArray arrayItselfAddSuccessMessage() {
        return arrayItselfAddSuccessMessage(getValidationKey(), null);
    }

    @Override
    public ValidationBuilderArray arrayItselfAddSuccessMessage(String customSuccessMessageText) {
        return arrayItselfAddSuccessMessage(getValidationKey(), customSuccessMessageText);
    }

    @Override
    public ValidationBuilderArray arrayItselfAddSuccessMessage(String validationKey, String customSuccessMessageText) {
        this.arrayItselfAddSuccessMessageValidationKey = validationKey;
        this.arrayItselfAddSuccessMessage = true;
        this.arrayItselfCustomSuccessMessageText = customSuccessMessageText;
        return this;
    }

    @Override
    public ValidationBuilderArray arrayItselfAddFailMessage() {
        return arrayItselfAddFailMessage(getValidationKey(), null);
    }

    @Override
    public ValidationBuilderArray arrayItselfAddFailMessage(String customSuccessMessageText) {
        return arrayItselfAddFailMessage(getValidationKey(), customSuccessMessageText);
    }

    @Override
    public ValidationBuilderArray arrayItselfAddFailMessage(String validationKey, String customFailMessageText) {
        this.arrayItselfAddFailMessageValidationKey = validationKey;
        this.arrayItselfAddFailMessage = true;
        this.arrayItselfCustomFailMessageText = customFailMessageText;
        return this;
    }

    protected boolean isArrayItselfAddSuccessMessage() {
        return this.arrayItselfAddSuccessMessage;
    }

    protected boolean isArrayItselfAddFailMessage() {
        return this.arrayItselfAddFailMessage;
    }

    protected String getArrayItselfCustomSuccessMessageText() {
        return this.arrayItselfCustomSuccessMessageText;
    }

    protected String getArrayItselfCustomFailMessageText() {
        return this.arrayItselfCustomFailMessageText;
    }

    protected String getArrayItselfDefaultSuccessMessageText() {
        return getSpincastDictionary().validation_array_itself_success_message_default_text();
    }

    protected String getArrayItselfDefaultWarningMessageText() {
        return getSpincastDictionary().validation_array_itself_warning_message_default_text();
    }

    protected String getArrayItselfDefaultErrorMessageText() {
        return getSpincastDictionary().validation_array_itself_error_message_default_text();
    }

    protected String getNotArrayDefaultErrorMessageText() {
        return getSpincastDictionary().validation_not_an_array_error_message_default_text();
    }

    @Override
    public ValidationSet validate(ValidationLevel onlyIfNoMessageAtThisLevelOrHigherYet) {

        //==========================================
        // If a JsonPath was used on a JsonObject to get
        // the array to validate the elements of, but the
        // element at that path was not an array...
        //==========================================
        if(isElementWasNotAnArray()) {

            String message = getArrayItselfCustomErrorMessageText() != null ? getArrayItselfCustomErrorMessageText()
                                                                            : getNotArrayDefaultErrorMessageText();

            ValidationMessage notArrayMessage = getValidationFactory().createMessage(ValidationLevel.ERROR,
                                                                                     ValidationSet.VALIDATION_CODE_NOT_AN_ARRAY,
                                                                                     message);

            if(getValidationSet() != null) {
                getValidationSet().addMessage(getValidationKey(), notArrayMessage);
            }

            return getValidationFactory().createValidationSet(getValidationKey(), notArrayMessage);
        }

        JsonArray array = (JsonArray)getElementToValidate();
        if(array == null) {
            array = getJsonManager().createArray();
        }

        ValidationSet finalResult = getValidationFactory().createValidationSet();
        int pos = 0;
        for(Object element : array) {
            ValidationSet result =
                    validateElement(getValidationKey() + "[" + pos++ + "]", element, onlyIfNoMessageAtThisLevelOrHigherYet);
            finalResult.mergeValidationSet(result);
        }

        //==========================================
        // Array itself : Success message?
        //==========================================
        if(finalResult.isSuccess()) {

            if(isArrayItselfAddSuccessMessage()) {

                String message =
                        getArrayItselfCustomSuccessMessageText() != null ? getArrayItselfCustomSuccessMessageText()
                                                                         : getArrayItselfDefaultSuccessMessageText();

                String code = getCode() != null ? getCode() : getSimpleValidator().getCode();

                ValidationMessage arrayMessage = getValidationFactory().createMessage(ValidationLevel.SUCCESS,
                                                                                      code,
                                                                                      message);

                getValidationSet().addMessage(getArrayItselfAddSuccessMessageValidationKey(), arrayMessage);
                finalResult.addMessage(getArrayItselfAddSuccessMessageValidationKey(), arrayMessage);
            }

        //==========================================@formatter:off 
        // Array itself : Fail message?
        //==========================================@formatter:on
        } else {

            if(isArrayItselfAddFailMessage()) {

                ValidationLevel level =
                        finalResult.isWarning() ? ValidationLevel.WARNING : ValidationLevel.ERROR;

                String message = getArrayItselfCustomErrorMessageText();
                if(message == null) {
                    if(isTreatErrorAsWarning()) {
                        message = getArrayItselfDefaultWarningMessageText();
                    } else {
                        message = getArrayItselfDefaultErrorMessageText();
                    }
                }

                String code = getCode() != null ? getCode() : getSimpleValidator().getCode();

                ValidationMessage arrayMessage = getValidationFactory().createMessage(level,
                                                                                      code,
                                                                                      message);

                getValidationSet().addMessage(getArrayItselfAddFailMessageValidationKey(), arrayMessage);
                finalResult.addMessage(getArrayItselfAddFailMessageValidationKey(), arrayMessage);
            }
        }

        return finalResult;
    }
}

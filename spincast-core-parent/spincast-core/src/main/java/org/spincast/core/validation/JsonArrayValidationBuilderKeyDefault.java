package org.spincast.core.validation;

import org.spincast.core.json.JsonArray;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class JsonArrayValidationBuilderKeyDefault extends JsonObjectValidationBuilderKeyDefault
                                                  implements JsonArrayValidationBuilderKey {

    @AssistedInject
    public JsonArrayValidationBuilderKeyDefault(@Assisted ValidationSet validationSet,
                                                @Assisted JsonArray validatedObject,
                                                @Assisted SimpleValidator validator,
                                                ValidationFactory validationFactory) {
        super(validationSet, validatedObject, validator, validationFactory);
    }

    @Override
    protected JsonArray getValidatedObject() {
        return (JsonArray)super.getValidatedObject();
    }

    @Override
    public ValidationBuilderCore jsonPath(String jsonPath) {

        if(StringUtils.isBlank(jsonPath)) {
            throw new RuntimeException("The JsonPath can't be empty.");
        }

        Object elementToValidate = getValidatedObject().getObject(jsonPath);
        return getValidationFactory().createValidationBuilderCore(getValidationSet(),
                                                                  getValidator(),
                                                                  jsonPath,
                                                                  elementToValidate);
    }

    @Override
    public ValidationBuilderArray jsonPathAll(String jsonPath) {
        return jsonPathAll(jsonPath, jsonPath);
    }

    @Override
    public ValidationBuilderArray jsonPathAll(String jsonPath, String validationKey) {

        if(StringUtils.isBlank(jsonPath)) {
            throw new RuntimeException("The JsonPath can't be empty.");
        }
        if(StringUtils.isBlank(validationKey)) {
            throw new RuntimeException("The validationKey can't be empty.");
        }

        boolean elemenseWasNotAnArray = false;
        Object obj = getValidatedObject().getObject(jsonPath);
        if(obj != null && !(obj instanceof JsonArray)) {
            elemenseWasNotAnArray = true;
            obj = null;

        }
        JsonArray array = (JsonArray)obj;

        return getValidationFactory().createValidationBuilderArray(getValidationSet(),
                                                                   getValidator(),
                                                                   validationKey,
                                                                   array,
                                                                   elemenseWasNotAnArray);
    }

    @Override
    public ValidationBuilderCore index(int index) {
        return super.jsonPath("[" + index + "]");
    }

    @Override
    public ValidationBuilderArray indexAll(int index) {
        return super.jsonPathAll("[" + index + "]");
    }

}

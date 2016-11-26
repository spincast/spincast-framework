package org.spincast.core.validation;

import org.spincast.core.json.JsonArray;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ValidationBuilderTargetDefault implements ValidationBuilderTarget {

    private final ValidationSet validationSet;
    private final SimpleValidator validator;
    private final String validationKey;
    private final ValidationFactory validationFactory;

    @AssistedInject
    public ValidationBuilderTargetDefault(@Assisted ValidationSet validationSet,
                                          @Assisted SimpleValidator validator,
                                          @Assisted String validationKey,
                                          ValidationFactory validationFactory) {
        this.validationSet = validationSet;
        this.validator = validator;
        this.validationKey = validationKey;
        this.validationFactory = validationFactory;
    }

    protected ValidationSet getValidationSet() {
        return this.validationSet;
    }

    protected SimpleValidator getValidator() {
        return this.validator;
    }

    protected String getValidationKey() {
        return this.validationKey;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    @Override
    public ValidationBuilderCore element(Object element) {

        return getValidationFactory().createValidationBuilderCore(getValidationSet(),
                                                                  getValidator(),
                                                                  getValidationKey(),
                                                                  element);
    }

    @Override
    public ValidationBuilderArray all(JsonArray array) {

        return getValidationFactory().createValidationBuilderArray(getValidationSet(),
                                                                   getValidator(),
                                                                   getValidationKey(),
                                                                   array);
    }

}

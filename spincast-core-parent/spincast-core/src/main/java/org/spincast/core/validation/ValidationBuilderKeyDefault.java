package org.spincast.core.validation;

import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ValidationBuilderKeyDefault implements ValidationBuilderKey {

    private final ValidationSet validationSet;
    private final SimpleValidator validator;
    private final ValidationFactory validationFactory;

    @AssistedInject
    public ValidationBuilderKeyDefault(@Assisted ValidationSet validationSet,
                                       @Assisted SimpleValidator validator,
                                       ValidationFactory validationFactory) {
        this.validationSet = validationSet;
        this.validator = validator;
        this.validationFactory = validationFactory;
    }

    protected ValidationSet getValidationSet() {
        return this.validationSet;
    }

    protected SimpleValidator getValidator() {
        return this.validator;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    @Override
    public ValidationBuilderTarget key(String validationKey) {

        if(StringUtils.isBlank(validationKey)) {
            throw new RuntimeException("The validation key can't be empty.");
        }

        return getValidationFactory().createValidationBuilderTarget(getValidationSet(), getValidator(), validationKey);
    }

}

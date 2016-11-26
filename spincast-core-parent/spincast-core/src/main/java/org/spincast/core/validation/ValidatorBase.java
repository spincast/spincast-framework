package org.spincast.core.validation;

import com.google.inject.Inject;

/**
 * Class that can be used as a base for validators.
 */
public abstract class ValidatorBase {

    private ValidationFactory validationFactory;

    protected ValidationFactory getValidationBFactory() {
        return this.validationFactory;
    }

    /**
     * We inject the dependencies using setters since this class
     * will probly be extended frequently... It's easier that
     * way for the children classes.
     */
    @Inject
    protected void setValidationBuilderFactory(ValidationFactory validationFactory) {
        this.validationFactory = validationFactory;
    }

    protected ValidationSet newValidationBuilder() {
        return getValidationBFactory().createValidationSet();
    }

}

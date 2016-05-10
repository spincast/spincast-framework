package org.spincast.plugins.validation;

/**
 * Validator factory.
 */
public interface IValidatorFactory<T> {

    /**
     * Creates a validator.
     */
    public IValidator create(T objectToValidate);

}

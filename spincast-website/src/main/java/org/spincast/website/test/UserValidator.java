package org.spincast.website.test;

import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.Validators;

import com.google.inject.Inject;

public class UserValidator {

    private final ValidationFactory validationFactory;
    private final Validators validators;

    @Inject
    public UserValidator(ValidationFactory validationFactory,
                         Validators validators) {
        this.validationFactory = validationFactory;
        this.validators = validators;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    protected Validators getValidators() {
        return this.validators;
    }

    public ValidationSet validate(User user) {

        ValidationSet validation = getValidationFactory().createValidationSet();

        if (!getValidators().isEmailValid(user.getEmail())) {
            validation.addError("email", "email_invalid", "The email is invalid");
        }

        return validation;
    }

}

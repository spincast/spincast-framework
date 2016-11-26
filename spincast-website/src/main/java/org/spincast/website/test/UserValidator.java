package org.spincast.website.test;

import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.ValidatorBase;

public class UserValidator extends ValidatorBase {

    public ValidationSet validate(User user) {

        ValidationSet validation = newValidationBuilder();

        ValidationSet result = validateEmail("email", user.getEmail());
        validation.mergeValidationSet(result);

        return validation;

    }

    public ValidationSet validateEmail(String fieldName, String email) {
        return null;

    }

}

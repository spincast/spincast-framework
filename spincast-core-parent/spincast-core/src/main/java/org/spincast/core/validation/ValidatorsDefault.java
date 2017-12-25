package org.spincast.core.validation;


import org.spincast.shaded.org.apache.commons.validator.routines.EmailValidator;

public class ValidatorsDefault implements Validators {

    @Override
    public boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

}

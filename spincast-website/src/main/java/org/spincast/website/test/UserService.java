package org.spincast.website.test;

import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.ValidationMessageFormatType;

public class UserService {

    private UserValidator userValidator = null;

    public void saveUser(User user) {

        ValidationSet result = this.userValidator.validate(user);
        if (result.isValid()) {

            throw new PublicExceptionDefault("The user to save contains errors :\n" +
                                             result.getMessagesFormatted(ValidationMessageFormatType.PLAIN_TEXT));
        }

        saveUserIdRepository(user);
    }

    private void saveUserIdRepository(User user) {
    }

}

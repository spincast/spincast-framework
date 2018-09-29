package org.spincast.core.validation;

import com.google.inject.assistedinject.Assisted;

public interface ValidationFactory {

    public ValidationSet createValidationSet();

    public ValidationMessage createMessage(@Assisted("validationLevel") ValidationLevel level,
                                           @Assisted("code") String code,
                                           @Assisted("text") String text,
                                           @Assisted("htmlEscape") boolean htmlEscape);
}

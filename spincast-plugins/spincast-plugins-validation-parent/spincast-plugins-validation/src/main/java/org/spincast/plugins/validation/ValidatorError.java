package org.spincast.plugins.validation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Validation error implementation.
 */
public class ValidatorError implements IValidationError {

    private final String fieldName;
    private final String errorMessage;
    private final String errorType;

    @AssistedInject
    public ValidatorError(@Assisted("fieldName") String fieldName,
                          @Assisted("errorMessage") String errorMessage,
                          @Assisted("errorType") String errorType) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    @Override
    public String getType() {
        return this.errorType;
    }

}

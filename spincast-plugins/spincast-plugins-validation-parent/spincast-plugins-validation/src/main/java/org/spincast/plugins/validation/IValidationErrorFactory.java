package org.spincast.plugins.validation;

import com.google.inject.assistedinject.Assisted;

/**
 * Validation error factory.
 */
public interface IValidationErrorFactory {

    /**
     * Creates a validation error.
     */
    public IValidationError create(@Assisted("fieldName") String fieldName,
                                   @Assisted("errorMessage") String errorMessage,
                                   @Assisted("errorType") String errorType);

}

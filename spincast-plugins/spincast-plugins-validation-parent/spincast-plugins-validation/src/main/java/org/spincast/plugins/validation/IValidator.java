package org.spincast.plugins.validation;

import java.util.List;
import java.util.Map;

/**
 * Object validator main interface.
 */
public interface IValidator {

    /**
     * Is the object valid?
     */
    public boolean isValid();

    /**
     * Revalidates the object. 
     * The validation result is cached until this is called.
     */
    public void revalidate();

    /**
     * Gets the validation errors. The keys are the
     * field names.
     * <p>
     * If the validation hasn't been called yet, calls it.
     * </p>
     */
    public Map<String, List<IValidationError>> getErrors();

    /**
     * Gets the validation errors for the specified field.
     * <p>
     * If the validation hasn't been called yet, calls it.
     * </p>
     */
    public List<IValidationError> getErrors(String fieldName);

    /**
     * Gets the formatted errors, if there are any.
     * <p>
     * If the validation hasn't been called yet, calls it.
     * </p>
     * 
     * @param formatType The type of output for the errors (Text, HTML, Json or XML).
     * 
     * @return the formatted errors or <code>null</code> if
     * there are no validation errors.
     */
    public String getErrorsFormatted(FormatType formatType);

    /**
     * Gets the formatted errors of a specific field, if there are any.
     * <p>
     * If the validation hasn't been called yet, calls it.
     * </p>
     * 
     * @param fieldName The field to get errors for.
     * @param formatType The type of output for the errors (Text, HTML, Json or XML).
     * 
     * @return the formatted errors or <code>null</code> if
     * there are no validation errors.
     */
    public String getErrorsFormatted(String fieldName, FormatType formatType);

}

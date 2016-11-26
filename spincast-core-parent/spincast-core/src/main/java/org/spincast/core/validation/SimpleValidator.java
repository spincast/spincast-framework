package org.spincast.core.validation;

/**
 * A simple validator is a validator that only returns
 * <em>one</em> validation result, as a boolean. The calling code
 * is responsible to add the required validation Message
 * resulting from that validation to a Set.
 */
public interface SimpleValidator {

    /**
     * Validates the element.
     */
    public boolean validate(Object element);

    /**
     * The validation code.
     */
    public String getCode();

    /**
     * The success message to use.
     */
    public String getSuccessMessage(Object element);

    /**
     * The fail message to use.
     */
    public String getFailMessage(Object element);

}

package org.spincast.core.config;

/**
 * Labels required by Spincast.
 */
public interface SpincastDictionary {

    /**
     * The message to display if the default <code>Not Found</code> 
     * route is used.
     * 
     * @return the message to display
     */
    public String route_notFound_default_message();

    /**
     * The message to display if the default <code>Exception</code> 
     * route is used.
     * 
     * @return the message to display
     */
    public String exception_default_message();

    /**
     * Default text for successful validation Message.
     */
    public String validation_success_message_default_text();

    /**
     * Default Message when an array is expected but was not
     */
    public String validation_not_an_array_error_message_default_text();

    /**
     * Default message on an array when at least one
     * element is in error.
     */
    public String validation_array_itself_error_message_default_text();

    /**
     * Default message on an array when ll elements are success.
     */
    public String validation_array_itself_success_message_default_text();

    /**
     * Default message on an array when at least one
     * element is a warning.
     */
    public String validation_array_itself_warning_message_default_text();

    /**
     * Default message for a Not Null validation.
     */
    public String validation_not_null_default_text();

    /**
     * Default message for a Null validation.
     */
    public String validation_null_default_text();

    /**
     * Default message for a Not Blank validation.
     */
    public String validation_not_blank_default_text();

    /**
     * Default message for a Blank validation.
     */
    public String validation_blank_default_text();

    /**
     * Default message for an equivalence validation.
     */
    public String validation_equivalent_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a "not equal" validation.
     */
    public String validation_not_equivalent_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a "equal or greater than" validation.
     */
    public String validation_equivalent_or_greater_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a "greater than" validation.
     */
    public String validation_greater_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a "equal or less than" validation.
     */
    public String validation_equivalent_or_less_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a "less than" validation.
     */
    public String validation_less_default_text(Object elementToValidate, Object reference);

    /**
     * Default message for a Length validation.
     */
    public String validation_length_default_text(long length, long currentLength);

    /**
     * Default message for a Minimum Length validation.
     */
    public String validation_min_length_default_text(long minLength, long currentLength);

    /**
     * Default message for a Maximum Length validation.
     */
    public String validation_max_length_default_text(long maxLength, long currentLength);

    /**
     * Default generic error message.
     */
    public String validation_generic_error_default_text();

    /**
     * Default message for a Pattern validation.
     */
    public String validation_pattern_default_text(String pattern);

    /**
     * Default message for a "doesn't match the Pattern" validation.
     */
    public String validation_not_pattern_default_text(String pattern);

    /**
     * Default message for a Email validation.
     */
    public String validation_email_default_text();

    /**
     * Default message for a Size validation.
     */
    public String validation_size_default_text(long size, long currentSize);

    /**
     * Default message for a Minimum Size validation.
     */
    public String validation_min_size_default_text(long minSize, long currentSize);

    /**
     * Default message for a Maximum Size validation.
     */
    public String validation_max_size_default_text(long maxSize, long currentSize);

    /**
     * Default message for a "can be converted to" validation.
     */
    public String validation_can_be_converted_to_default_text(String type);

    /**
     * Default message for a "is of type" validation.
     */
    public String validation_is_of_type_default_text(String type);

}

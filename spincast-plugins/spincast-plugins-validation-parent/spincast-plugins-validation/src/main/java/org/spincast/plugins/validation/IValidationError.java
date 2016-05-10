package org.spincast.plugins.validation;

/**
 * A validation error.
 */
public interface IValidationError {

    public static final String VALIDATION_TYPE_NOT_NULL = "VALIDATION_TYPE_NOT_NULL";
    public static final String VALIDATION_TYPE_NOT_BLANK = "VALIDATION_TYPE_NOT_BLANK";
    public static final String VALIDATION_TYPE_MIN_LENGTH = "VALIDATION_TYPE_MIN_LENGTH";
    public static final String VALIDATION_TYPE_MAX_LENGTH = "VALIDATION_TYPE_MAX_LENGTH";
    public static final String VALIDATION_TYPE_PATTERN = "VALIDATION_TYPE_PATTERN";
    public static final String VALIDATION_TYPE_EMAIL = "VALIDATION_TYPE_EMAIL";
    public static final String VALIDATION_TYPE_MIN_SIZE = "VALIDATION_TYPE_MIN_SIZE";
    public static final String VALIDATION_TYPE_MAX_SIZE = "VALIDATION_TYPE_MAX_SIZE";

    /**
     * The error type
     */
    public String getType();

    /**
     * The name of the field with an error.
     */
    public String getFieldName();

    /**
     * The error message.
     */
    public String getMessage();

}

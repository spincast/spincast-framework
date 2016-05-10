package org.spincast.plugins.validation;

import com.google.inject.ImplementedBy;

/**
 * Configurations requried by the Spincast bean validation plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastValidationConfigDefault.class)
public interface ISpincastValidationConfig {

    /**
     * Default message for a Not Null validation.
     * 
     */
    public String getErrorMessageDefaultNotNull();

    /**
     * Default message for a Not Blank validation.
     */
    public String getErrorMessageDefaultNotBlank();

    /**
     * Default message for a Minimum Length validation.
     */
    public String getErrorMessageDefaultMinLength(long minLength, long currentLength);

    /**
     * Default message for a Maximum Length validation.
     */
    public String getErrorMessageDefaultMaxLength(long maxLength, long currentLength);

    /**
     * Default generic error message.
     */
    public String getDefaultGenericErrorMessage();

    /**
     * Default message for a Pattern validation.
     */
    public String getErrorMessageDefaultPattern(String pattern);

    /**
     * Default message for a Email validation.
     */
    public String getErrorMessageEmail();

    /**
     * Default message for a Minimum Size validation.
     */
    public String getErrorMessageDefaultMinSize(long minSize, long currentSize);

    /**
     * Default message for a Maximum Size validation.
     */
    public String getErrorMessageDefaultMaxSize(long maxSize, long currentSize);

}

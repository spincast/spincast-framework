package org.spincast.plugins.validation;

import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

/**
 * Default configurations for the Spincast bean validation plugin.
 */
public class SpincastValidationConfigDefault implements ISpincastValidationConfig {

    private final ITemplatingEngine templatingEngine;

    /**
     * Constructor
     */
    @Inject
    public SpincastValidationConfigDefault(ITemplatingEngine templatingEngine) {
        this.templatingEngine = templatingEngine;
    }

    protected ITemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Override
    public String getErrorMessageDefaultNotNull() {
        return getErrorMessageDefaultNotNullRaw();
    }

    protected String getErrorMessageDefaultNotNullRaw() {
        return "Can't be null.";
    }

    @Override
    public String getErrorMessageDefaultNotBlank() {
        return getErrorMessageDefaultNotBlankRaw();
    }

    protected String getErrorMessageDefaultNotBlankRaw() {
        return "Can't be null, empty or only contain spaces.";
    }

    @Override
    public String getErrorMessageDefaultMinLength(long minLength, long currentLength) {
        return getTemplatingEngine().evaluate(getErrorMessageDefaultMinLengthRaw(),
                                              SpincastStatics.params("minLength",
                                                                     minLength,
                                                                     "currentLength",
                                                                     currentLength));
    }

    protected String getErrorMessageDefaultMinLengthRaw() {
        return "Minimum length of {{minLength}} characters. Currently: {{currentLength}} characters.";
    }

    @Override
    public String getErrorMessageDefaultMaxLength(long maxLength, long currentLength) {
        return getTemplatingEngine().evaluate(getErrorMessageDefaultMaxLengthRaw(),
                                              SpincastStatics.params("maxLength",
                                                                     maxLength,
                                                                     "currentLength",
                                                                     currentLength));
    }

    protected String getErrorMessageDefaultMaxLengthRaw() {
        return "Maximum length of {{maxLength}} characters. Currently: {{currentLength}} characters.";
    }

    @Override
    public String getDefaultGenericErrorMessage() {
        return getDefaultGenericErrorMessageRaw();
    }

    protected String getDefaultGenericErrorMessageRaw() {
        return "Invalid value.";
    }

    @Override
    public String getErrorMessageDefaultPattern(String pattern) {
        return getTemplatingEngine().evaluate(getErrorMessageDefaultPatternRaw(),
                                              SpincastStatics.params("pattern",
                                                                     pattern));
    }

    protected String getErrorMessageDefaultPatternRaw() {
        return "Doesn't match the pattern: \"{{pattern}}\".";
    }

    @Override
    public String getErrorMessageEmail() {
        return getErrorMessageEmailRaw();
    }

    protected String getErrorMessageEmailRaw() {
        return "Not a valid email address.";
    }

    @Override
    public String getErrorMessageDefaultMinSize(long minSize, long currentSize) {
        return getTemplatingEngine().evaluate(getErrorMessageDefaultMinSizeRaw(),
                                              SpincastStatics.params("minSize",
                                                                     minSize,
                                                                     "currentSize",
                                                                     currentSize));
    }

    protected String getErrorMessageDefaultMinSizeRaw() {
        return "The minimum value is {{minSize}}. Currently: {{currentSize}}.";
    }

    @Override
    public String getErrorMessageDefaultMaxSize(long maxSize, long currentSize) {
        return getTemplatingEngine().evaluate(getErrorMessageDefaultMinSizeRaw(),
                                              SpincastStatics.params("maxSize",
                                                                     maxSize,
                                                                     "currentSize",
                                                                     currentSize));
    }

    protected String getErrorMessageDefaultMaxSizeRaw() {
        return "The maximum value is {{maxSize}}. Currently: {{currentSize}}.";
    }

}

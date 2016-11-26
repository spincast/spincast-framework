package org.spincast.plugins.dictionary;

import java.util.Locale;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.locale.LocaleResolver;

import com.google.inject.Inject;

public class SpincastDictionaryDefault implements SpincastDictionary {

    private final LocaleResolver localeResolver;

    @Inject
    public SpincastDictionaryDefault(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    protected Locale getLocale() {
        return this.localeResolver.getLocaleToUse();
    }

    @Override
    public String route_notFound_default_message() {

        @SuppressWarnings("unused")
        Locale locale = getLocale();

        // No i18n for now...
        return "Not found";
    }

    @Override
    public String exception_default_message() {
        return "Internal Error";
    }

    @Override
    public String validation_not_null_default_text() {
        return "Can't be null";
    }

    @Override
    public String validation_null_default_text() {
        return "Must be null";
    }

    @Override
    public String validation_not_blank_default_text() {
        return "Can't be empty";
    }

    @Override
    public String validation_array_itself_error_message_default_text() {
        return "At least one element is invalid";
    }

    @Override
    public String validation_array_itself_success_message_default_text() {
        return "All elements are valid";
    }

    @Override
    public String validation_array_itself_warning_message_default_text() {
        return "Contains at least one warning";
    }

    @Override
    public String validation_blank_default_text() {
        return "Must be empty";
    }

    @Override
    public String validation_equivalent_default_text(Object elementToValidate, Object reference) {
        return "The value \"" + elementToValidate + "\" is not equivalent to \"" + reference + "\"";
    }

    @Override
    public String validation_not_equivalent_default_text(Object valueToValidate, Object compareTo) {
        return "The value \"" + valueToValidate + "\" must not be equivalent to \"" + compareTo + "\"";
    }

    @Override
    public String validation_equivalent_or_greater_default_text(Object valueToValidate, Object compareTo) {
        return "The value \"" + valueToValidate + "\" must be equivalent or greater than \"" + compareTo + "\"";
    }

    @Override
    public String validation_greater_default_text(Object valueToValidate, Object compareTo) {
        return "The value \"" + valueToValidate + "\" must be greater than \"" + compareTo + "\"";
    }

    @Override
    public String validation_equivalent_or_less_default_text(Object valueToValidate, Object compareTo) {
        return "The value \"" + valueToValidate + "\" must be equivalent or less than \"" + compareTo + "\"";
    }

    @Override
    public String validation_less_default_text(Object valueToValidate, Object compareTo) {
        return "The value \"" + valueToValidate + "\" must be less than \"" + compareTo + "\"";
    }

    @Override
    public String validation_min_length_default_text(long minLength, long currentLength) {
        return "Minimum length of " + minLength + " characters (currently " + currentLength + ")";
    }

    @Override
    public String validation_length_default_text(long length, long currentLength) {
        return "The llength must be " + length + " characters (currently " + currentLength + ")";
    }

    @Override
    public String validation_max_length_default_text(long maxLength, long currentLength) {
        return "Maximum length of " + maxLength + " characters (currently " + currentLength + ")";
    }

    @Override
    public String validation_generic_error_default_text() {
        return "Invalid value";
    }

    @Override
    public String validation_pattern_default_text(String pattern) {
        return "Doesn't match the pattern : \"" + pattern + "\"";
    }

    @Override
    public String validation_not_pattern_default_text(String pattern) {
        return "Must not match the pattern : \"" + pattern + "\"";
    }

    @Override
    public String validation_email_default_text() {
        return "Invalid email address";
    }

    @Override
    public String validation_size_default_text(long size, long currentSize) {
        return "The size must be " + size + " (currently " + currentSize + ")";
    }

    @Override
    public String validation_min_size_default_text(long minSize, long currentSize) {
        return "The minimum size is " + minSize + " (currently " + currentSize + ")";
    }

    @Override
    public String validation_max_size_default_text(long maxSize, long currentSize) {
        return "The maximum size is " + maxSize + " (currently " + currentSize + ")";
    }

    @Override
    public String validation_can_be_converted_to_default_text(String type) {
        return "Can't be converted to a \"" + type + "\"";
    }

    @Override
    public String validation_is_of_type_default_text(String type) {
        return "Is not of type \"" + type + "\"";
    }

    @Override
    public String validation_success_message_default_text() {
        return "Valid";
    }

    @Override
    public String validation_not_an_array_error_message_default_text() {
        return "Expecting an array";
    }

}

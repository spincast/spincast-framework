package org.spincast.plugins.formsprotection.exceptions;

/**
 * Thrown when a form is submitted with an invalid CRSF token.
 */
public class FormInvalidCsrfTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String invalidCrsfToken;

    public FormInvalidCsrfTokenException(String invalidCrsfToken, String message) {
        super(message);
        this.invalidCrsfToken = invalidCrsfToken;
    }

    public String getFormInvalidCrsfToken() {
        return this.invalidCrsfToken;
    }
}

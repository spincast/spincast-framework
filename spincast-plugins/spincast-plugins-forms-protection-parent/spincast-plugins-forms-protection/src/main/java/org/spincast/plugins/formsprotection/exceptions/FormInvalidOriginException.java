package org.spincast.plugins.formsprotection.exceptions;

/**
 * Thrown when a form is submitted from an invalid orgin.
 */
public class FormInvalidOriginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String invalidOrigin;

    public FormInvalidOriginException(String invalidOrigin, String message) {
        super(message);
        this.invalidOrigin = invalidOrigin;
    }

    public String getFormInvalidOrigine() {
        return this.invalidOrigin;
    }
}

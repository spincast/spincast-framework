package org.spincast.plugins.formsprotection.exceptions;

import org.spincast.core.exceptions.PublicExceptionDefault;

/**
 * Thrown when a submitted form is too old.
 */
public class FormTooOldException extends PublicExceptionDefault {

    private static final long serialVersionUID = 1L;

    private final String formProtectedId;

    public FormTooOldException(String formProtectedId, String message) {
        super(message);
        this.formProtectedId = formProtectedId;
    }

    public String getFormProtectionId() {
        return this.formProtectedId;
    }
}

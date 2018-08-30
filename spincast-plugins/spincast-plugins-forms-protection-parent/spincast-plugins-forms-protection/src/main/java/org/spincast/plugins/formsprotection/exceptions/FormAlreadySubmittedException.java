package org.spincast.plugins.formsprotection.exceptions;

import org.spincast.core.exceptions.PublicExceptionDefault;

/**
 * Thrown when a form is double submitted.
 */
public class FormAlreadySubmittedException extends PublicExceptionDefault {

    private static final long serialVersionUID = 1L;

    private final String formProtectedId;

    public FormAlreadySubmittedException(String formProtectedId, String message) {
        super(message);
        this.formProtectedId = formProtectedId;
    }

    public String getFormProtectionId() {
        return this.formProtectedId;
    }
}

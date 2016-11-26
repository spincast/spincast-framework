package org.spincast.core.exceptions;

public class PublicExceptionDefault extends CustomStatusCodeExceptionDefault implements PublicException {

    private static final long serialVersionUID = 1L;

    public PublicExceptionDefault() {
        super();
    }

    public PublicExceptionDefault(String message) {
        super(message);
    }

    public PublicExceptionDefault(String message, int statusCode) {
        super(message, statusCode);
    }

    public PublicExceptionDefault(String message, boolean resetResponse) {
        super(message, resetResponse);
    }

    public PublicExceptionDefault(String message, int statusCode, boolean resetResponse) {
        super(message, statusCode, resetResponse);
    }

}

package org.spincast.core.exceptions;

public class PublicException extends CustomStatusCodeException implements IPublicException {

    private static final long serialVersionUID = 1L;

    public PublicException() {
        super();
    }

    public PublicException(String message) {
        super(message);
    }

    public PublicException(String message, int statusCode) {
        super(message, statusCode);
    }

    public PublicException(String message, boolean resetResponse) {
        super(message, resetResponse);
    }

    public PublicException(String message, int statusCode, boolean resetResponse) {
        super(message, statusCode, resetResponse);
    }

}

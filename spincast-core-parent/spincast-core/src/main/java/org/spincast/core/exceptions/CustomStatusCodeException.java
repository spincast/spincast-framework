package org.spincast.core.exceptions;

import org.spincast.shaded.org.apache.http.HttpStatus;

public class CustomStatusCodeException extends ResponseResetableException implements ICustomStatusCodeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public CustomStatusCodeException() {
        super();
        this.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public CustomStatusCodeException(String message) {
        super(message);
        this.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public CustomStatusCodeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public CustomStatusCodeException(boolean resetResponse) {
        super(resetResponse);
        this.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public CustomStatusCodeException(String message, boolean resetResponse) {
        super(message, resetResponse);
        this.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public CustomStatusCodeException(String message, int statusCode, boolean resetResponse) {
        super(message, resetResponse);
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

}

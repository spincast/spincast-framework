package org.spincast.core.exceptions;

public class PublicExceptionNoLog extends PublicExceptionDefault {

    private static final long serialVersionUID = 1L;

    public PublicExceptionNoLog() {
        super();
    }

    public PublicExceptionNoLog(String message) {
        super(message);
    }

    public PublicExceptionNoLog(String message, int statusCode) {
        super(message, statusCode);
    }

    public PublicExceptionNoLog(String message, boolean resetResponse) {
        super(message, resetResponse);
    }

    public PublicExceptionNoLog(String message, int statusCode, boolean resetResponse) {
        super(message, statusCode, resetResponse);
    }

    @Override
    public boolean isLogTheException() {
        return false;
    }

}

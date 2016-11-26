package org.spincast.core.exceptions;

/**
 * Exception to throw to trigger the "Not Found" route.
 */
public class NotFoundException extends ResponseResetableExceptionDefault {

    private static final long serialVersionUID = 1L;

    /**
     * If not already flushed, the response will be
     * reset before running the "Not Found" routing process.
     */
    public NotFoundException() {
        super();
    }

    /**
     * If not already flushed, the response will be
     * reset before running the "Not Found" routing process.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * @param resetResponse If not already flushed, should the response be
     * reset before running the "Not Found" routing process?
     */
    public NotFoundException(boolean resetResponse) {
        super(resetResponse);
    }

    /**
     * @param resetResponse If not already flushed, should the response be
     * reset before running the "Not Found" routing process?
     */
    public NotFoundException(String message, boolean resetResponse) {
        super(message, resetResponse);
    }

}

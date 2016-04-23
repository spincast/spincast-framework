package org.spincast.core.exceptions;

/**
 * An exception which allows to control if the
 * response should be reset (its buffer, headers, etc.) before running
 * the "Exception" routing process.
 * Note that the response can't be reset if its headers have 
 * already been sent.
 */
public class ResponseResetableException extends RuntimeException implements IResponseResetableException {

    private static final long serialVersionUID = 1L;

    private final boolean resetResponse;

    /**
     * If not already flushed, the response will be
     * reset before running the new route
     */
    public ResponseResetableException() {
        super();
        this.resetResponse = true;
    }

    /**
     * If not already flushed, the response will be
     * reset before running the new route
     */
    public ResponseResetableException(String message) {
        super(message);
        this.resetResponse = true;
    }

    /**
     * If not already flushed, the response will be
     * reset before running the new route
     */
    public ResponseResetableException(boolean resetResponse) {
        super();
        this.resetResponse = resetResponse;
    }

    /**
     * If not already flushed, the response will be
     * reset before running the new route
     */
    public ResponseResetableException(String message, boolean resetResponse) {
        super(message);
        this.resetResponse = resetResponse;
    }

    @Override
    public boolean isResetResponse() {
        return this.resetResponse;
    }

}

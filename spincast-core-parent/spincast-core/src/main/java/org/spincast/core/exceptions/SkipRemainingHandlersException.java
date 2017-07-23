package org.spincast.core.exceptions;

/**
 * Exception that will simply stop the current routing process
 * without starting any new one. No more
 * route handlers will be called. The response will be sent as is, 
 * without anything more added.
 * <p>
 * <strong>WARNING</strong> : even the *after* filters won't be
 * run when this expcetion is thrown... Use with care!
 */
public class SkipRemainingHandlersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SkipRemainingHandlersException() {
        super();
    }
}

package org.spincast.core.exceptions;

/**
 * An interface to be implemented by exceptions to indicate that 
 * their "message" can be displayed to the end user.
 */
public interface PublicException extends CustomStatusCodeException {

    /**
     * Should the exception be logged?
     */
    public boolean isLogTheException();
}

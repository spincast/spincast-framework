package org.spincast.core.exceptions;

/**
 * An interface to be implemented by exceptions to indicate that 
 * their "message" can be displayed to the end user.
 */
public interface IPublicException extends ICustomStatusCodeException {
    // nothing required
}

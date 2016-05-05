package org.spincast.core.exceptions;

/**
 * An exception on which it is possible to specify the
 * response <code>status code</code> to send to the user.
 */
public interface ICustomStatusCodeException {

    /**
     * The status code to use for the response.
     */
    public int getStatusCode();

}

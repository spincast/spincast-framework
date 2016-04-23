package org.spincast.core.exceptions;

/**
 * An interface for exceptions which allows them to control if the
 * response should be reset (its buffer, headers, etc.) before running
 * the "Exception" routing process.
 * Note that the response can't be reset if its headers have 
 * already been sent.
 */
public interface IResponseResetableException {

    /**
     * Should the the response be reset before starting the 
     * "Exception" routing process?
     */
    public boolean isResetResponse();
}

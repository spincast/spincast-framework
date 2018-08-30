package org.spincast.core.server;

/**
 * Some utilities for server implementation.
 */
public interface ServerUtils {

    /**
     * This must be called by a {@link Server}
     * implementation, after it has been successfully
     * started!
     * 
     */
    public void callServerStartedListeners();

}

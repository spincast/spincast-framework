package org.spincast.core.server;

/**
 * To implement to be informed after the application server
 * is started succesfully.
 * <p>
 * The {@link #serverStartedSuccessfully()} method
 * will be called asynchronously, in a new Thread.
 */
public interface ServerStartedListener {

    /**
     * Called after the server ha been started 
     * successfully.
     */
    public void serverStartedSuccessfully();

}

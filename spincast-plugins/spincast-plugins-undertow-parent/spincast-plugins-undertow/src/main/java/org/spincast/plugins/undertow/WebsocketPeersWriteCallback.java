package org.spincast.plugins.undertow;

import java.util.Set;

/**
 * Callback to handle the errors found after writing
 * to some peers.
 */
public interface WebsocketPeersWriteCallback {

    /**
     * The ids of the peers for which a connection
     * has been found as closed.
     * 
     * The Set will be empty if all connection are alive.
     */
    public void connectionClosed(Set<String> peerIds);

}

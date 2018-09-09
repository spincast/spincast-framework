package org.spincast.plugins.hotswap.fileswatcher;

import org.spincast.core.server.ServerStartedListener;

public interface HotSwapFilesModificationsWatcher extends ServerStartedListener {

    /**
     * Add a new listener for modifications to file(s)
     * to watch.
     * <p>
     * More than one listeners can be added for the same files!
     */
    public void registerListener(HotSwapFilesModificationsListener listener);

    /**
     * Removes a file modifications listener.
     */
    public void removeListener(HotSwapFilesModificationsListener listener);

    /**
     * Removes all listeners.
     */
    public void removeAllListeners();

    /**
     * Stops the files watching completly.
     * Can't be restarted.
     */
    public void stopWatching();

}

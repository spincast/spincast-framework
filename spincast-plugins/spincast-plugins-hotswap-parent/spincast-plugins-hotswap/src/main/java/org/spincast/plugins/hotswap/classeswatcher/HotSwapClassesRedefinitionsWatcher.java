package org.spincast.plugins.hotswap.classeswatcher;

public interface HotSwapClassesRedefinitionsWatcher {

    /**
     * Add a new listener for modifications to class
     * redefinitions.
     * <p>
     * More than one listeners can be added for the same class!
     */
    public void registerListener(HotSwapClassesRedefinitionsListener listener);

    /**
     * Removes a listener.
     */
    public void removeListener(HotSwapClassesRedefinitionsListener listener);

    /**
     * Removes all listeners.
     */
    public void removeAllListeners();
}

package org.spincast.plugins.hotswap.classeswatcher;

/**
 * Dummy implementation of HotSwapClassesRedefinitionsWatcher
 * used when Hotswap Agent is not available.
 */
public class HotSwapClassesRedefinitionsWatcherDummy implements HotSwapClassesRedefinitionsWatcher {

    @Override
    public void registerListener(HotSwapClassesRedefinitionsListener listener) {
        // nothing required
    }

    @Override
    public void removeListener(HotSwapClassesRedefinitionsListener listener) {
        // nothing required
    }

    @Override
    public void removeAllListeners() {
        // nothing required
    }
}

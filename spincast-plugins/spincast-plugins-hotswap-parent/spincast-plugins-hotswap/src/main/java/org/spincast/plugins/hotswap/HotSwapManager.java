package org.spincast.plugins.hotswap;

import org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcher;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsWatcher;

public interface HotSwapManager {

    /**
     * Gets the {@link HotSwapFilesModificationsWatcher}.
     */
    public HotSwapFilesModificationsWatcher getFilesModificationsWatcher();

    /**
     * Gets the {@link HotSwapFilesModificationsWatcher}.
     */
    public HotSwapClassesRedefinitionsWatcher getClassesRedefinitionsWatcher();
}

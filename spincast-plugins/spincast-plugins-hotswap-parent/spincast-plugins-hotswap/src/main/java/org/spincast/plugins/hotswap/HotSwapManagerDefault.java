package org.spincast.plugins.hotswap;

import org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcher;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsWatcher;

import com.google.inject.Inject;

public class HotSwapManagerDefault implements HotSwapManager {

    private final HotSwapFilesModificationsWatcher hotSwapFilesModificationsWatcher;
    private final HotSwapClassesRedefinitionsWatcher hotSwapClassesRedefinitionsWatcher;

    @Inject
    public HotSwapManagerDefault(HotSwapFilesModificationsWatcher hotSwapFilesModificationsWatcher,
                                 HotSwapClassesRedefinitionsWatcher hotSwapClassesRedefinitionWatcher) {
        this.hotSwapFilesModificationsWatcher = hotSwapFilesModificationsWatcher;
        this.hotSwapClassesRedefinitionsWatcher = hotSwapClassesRedefinitionWatcher;
    }

    @Override
    public HotSwapFilesModificationsWatcher getFilesModificationsWatcher() {
        return this.hotSwapFilesModificationsWatcher;
    }

    @Override
    public HotSwapClassesRedefinitionsWatcher getClassesRedefinitionsWatcher() {
        return this.hotSwapClassesRedefinitionsWatcher;
    }

}

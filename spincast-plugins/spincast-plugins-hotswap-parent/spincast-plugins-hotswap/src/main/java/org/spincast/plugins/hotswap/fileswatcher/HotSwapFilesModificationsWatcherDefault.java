package org.spincast.plugins.hotswap.fileswatcher;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.hotswap.HotSwapManagerDefault;

public class HotSwapFilesModificationsWatcherDefault implements HotSwapFilesModificationsWatcher {

    protected final static Logger logger = LoggerFactory.getLogger(HotSwapManagerDefault.class);

    private Map<String, Set<HotSwapFilesModificationsListener>> listenersByDirAbsolutePath;
    private Map<String, WatchKey> watchKeysByDirAbsolutePaths;
    private Map<WatchKey, String> dirAbsolutePathsByWatchKey;
    private Map<WatchKey, Set<HotSwapFilesModificationsListener>> listenersByWatchKey;
    private volatile boolean stopWatching = false;
    private Thread filesModificationsWatcherThread;

    private WatchService fileModificationsWatcherService;

    /**
     * Starts watching registered files when
     * the server is started.
     */
    @Override
    public void serverStartedSuccessfully() {
        startFilesModificationsWatcher();
    }

    protected Map<String, Set<HotSwapFilesModificationsListener>> getListenersByDirAbsolutePaths() {
        if (this.listenersByDirAbsolutePath == null) {
            this.listenersByDirAbsolutePath = new HashMap<String, Set<HotSwapFilesModificationsListener>>();
        }
        return this.listenersByDirAbsolutePath;
    }

    protected Map<String, WatchKey> getWatchKeysByDirAbsolutePaths() {
        if (this.watchKeysByDirAbsolutePaths == null) {
            this.watchKeysByDirAbsolutePaths = new HashMap<String, WatchKey>();
        }
        return this.watchKeysByDirAbsolutePaths;
    }

    protected Map<WatchKey, String> getDirAbsolutePathsByWatchKey() {
        if (this.dirAbsolutePathsByWatchKey == null) {
            this.dirAbsolutePathsByWatchKey = new HashMap<WatchKey, String>();
        }
        return this.dirAbsolutePathsByWatchKey;
    }

    protected WatchService getFileModificationsWatcherService() {
        if (this.fileModificationsWatcherService == null) {
            this.fileModificationsWatcherService = createFileModificationsWatcherService();
        }
        return this.fileModificationsWatcherService;
    }

    protected Map<WatchKey, Set<HotSwapFilesModificationsListener>> getListenersByWatchKey() {
        if (this.listenersByWatchKey == null) {
            this.listenersByWatchKey = new HashMap<WatchKey, Set<HotSwapFilesModificationsListener>>();
        }
        return this.listenersByWatchKey;
    }

    protected WatchService createFileModificationsWatcherService() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void registerListener(HotSwapFilesModificationsListener listener) {
        try {

            Objects.requireNonNull(listener, "The listener can't be NULL");

            if (!listener.isEnabled()) {
                logger.info("Listener '" + listener.getClass().getSimpleName() + "' not enabled, returning...");
                return;
            }

            Set<FileToWatch> filesToWatch = listener.getFilesToWatch();
            if (filesToWatch == null || filesToWatch.size() == 0) {
                logger.warn("No files to watch provided!");
                return;
            }

            for (FileToWatch fileToWatch : filesToWatch) {

                String dirAbsolutePath = fileToWatch.getDir().getAbsolutePath();

                Set<HotSwapFilesModificationsListener> listeners = getListenersByDirAbsolutePaths().get(dirAbsolutePath);
                if (listeners == null) {
                    listeners = new HashSet<HotSwapFilesModificationsListener>();
                    getListenersByDirAbsolutePaths().put(dirAbsolutePath, listeners);
                } else if (listeners.contains(listener)) {
                    continue;
                }
                listeners.add(listener);

                //==========================================
                // We register a directory to be watched
                // one time only...
                //==========================================
                Map<String, WatchKey> watchKeysByDirAbsolutePath = getWatchKeysByDirAbsolutePaths();
                WatchKey watchKey = null;
                if (watchKeysByDirAbsolutePath.containsKey(dirAbsolutePath)) {
                    watchKey = watchKeysByDirAbsolutePath.get(dirAbsolutePath);
                } else {
                    Path dirPathObj = Paths.get(dirAbsolutePath);
                    watchKey = dirPathObj.register(getFileModificationsWatcherService(), StandardWatchEventKinds.ENTRY_MODIFY);
                    getWatchKeysByDirAbsolutePaths().put(dirAbsolutePath, watchKey);
                    getDirAbsolutePathsByWatchKey().put(watchKey, dirAbsolutePath);
                }

                Set<HotSwapFilesModificationsListener> existingListeners = getListenersByWatchKey().get(watchKey);
                if (existingListeners == null) {
                    existingListeners = new HashSet<HotSwapFilesModificationsListener>();
                    getListenersByWatchKey().put(watchKey, existingListeners);
                }
                existingListeners.add(listener);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void removeAllListeners() {
        for (WatchKey watchKey : getListenersByWatchKey().keySet()) {
            watchKey.cancel();
        }
        getListenersByDirAbsolutePaths().clear();
        getWatchKeysByDirAbsolutePaths().clear();
        getListenersByWatchKey().clear();
        getDirAbsolutePathsByWatchKey().clear();
    }

    @Override
    public void removeListener(HotSwapFilesModificationsListener listener) {
        Objects.requireNonNull(listener, "The listener can't be NULL");

        Set<FileToWatch> filesToWatch = listener.getFilesToWatch();
        if (filesToWatch == null) {
            return;
        }

        boolean listenerRemoved = false;
        for (FileToWatch fileToWatch : filesToWatch) {

            String dirAbsolutePath = fileToWatch.getDir().getAbsolutePath();
            Set<HotSwapFilesModificationsListener> listenersByDirAbsolutePath =
                    getListenersByDirAbsolutePaths().get(dirAbsolutePath);
            if (listenersByDirAbsolutePath != null) {
                listenersByDirAbsolutePath.remove(listener);
                if (listenersByDirAbsolutePath.size() == 0) {
                    getListenersByDirAbsolutePaths().remove(dirAbsolutePath);
                }
            }

            if (!listenerRemoved) {
                listenerRemoved = true;

                Map<String, WatchKey> watchKeysByDirAbsolutePath = getWatchKeysByDirAbsolutePaths();
                WatchKey watchKey = watchKeysByDirAbsolutePath.get(dirAbsolutePath);

                Map<WatchKey, Set<HotSwapFilesModificationsListener>> listenersByWatchKey = getListenersByWatchKey();
                Set<HotSwapFilesModificationsListener> listenersForWatchKey = listenersByWatchKey.get(watchKey);
                listenersForWatchKey.remove(listener);

                //==========================================
                // No more listeners associated with this 
                // watchKey? We stop watching the directory.
                //==========================================
                if (listenersForWatchKey.size() == 0) {
                    watchKey.cancel();
                    getWatchKeysByDirAbsolutePaths().remove(dirAbsolutePath);
                    getListenersByWatchKey().remove(watchKey);
                    getDirAbsolutePathsByWatchKey().remove(watchKey);
                }
            }
        }
    }

    protected void startFilesModificationsWatcher() {

        try {

            this.filesModificationsWatcherThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        WatchService watcher = getFileModificationsWatcherService();

                        while (!HotSwapFilesModificationsWatcherDefault.this.stopWatching) {

                            WatchKey watchKey = watcher.take();

                            //==========================================
                            // @see https://stackoverflow.com/a/25221600/843699
                            //==========================================
                            Thread.sleep(50);

                            String dirAbsolutePath = getDirAbsolutePathsByWatchKey().get(watchKey);

                            List<WatchEvent<?>> events = watchKey.pollEvents();
                            for (WatchEvent<?> event : events) {

                                Path path = (Path)event.context();
                                if (path == null) {
                                    continue;
                                }

                                File file = new File(dirAbsolutePath, path.toString());

                                Set<HotSwapFilesModificationsListener> listeners =
                                        getListenersByDirAbsolutePaths().get(dirAbsolutePath);

                                if (listeners != null && listeners.size() > 0) {
                                    for (HotSwapFilesModificationsListener listener : listeners) {
                                        if (modifiedFileMatchs(file, listener)) {
                                            Thread listenerThread = new Thread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    listener.fileModified(file);
                                                }
                                            });
                                            listenerThread.start();
                                        }
                                    }
                                }
                            }

                            @SuppressWarnings("unused")
                            boolean valid = watchKey.reset();
                        }

                    } catch (InterruptedException ex) {
                        logger.info("Fiels modifications watcher thread stopped by an InterruptedException.");
                        return;
                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            });
            this.filesModificationsWatcherThread.start();

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean modifiedFileMatchs(File modifiedFile, HotSwapFilesModificationsListener listener) {

        String modifiedFileName = modifiedFile.getName();

        Set<FileToWatch> filesToWatch = listener.getFilesToWatch();
        for (FileToWatch fileToWatch : filesToWatch) {
            if (fileToWatch.getDir().equals(modifiedFile.getParentFile())) {
                if (!fileToWatch.isRegEx()) {
                    if (modifiedFileName.equals(fileToWatch.getFileName())) {
                        return true;
                    }
                } else if (fileToWatch.getRegExPattern().matcher(modifiedFileName).matches()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void stopWatching() {
        this.stopWatching = true;
        try {
            if (this.filesModificationsWatcherThread != null) {
                this.filesModificationsWatcherThread.interrupt();
            }
        } catch (Exception ex) {
            // ok
        }
    }

}

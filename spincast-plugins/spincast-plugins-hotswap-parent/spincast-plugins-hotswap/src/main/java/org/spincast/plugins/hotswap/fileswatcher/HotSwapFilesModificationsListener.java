package org.spincast.plugins.hotswap.fileswatcher;

import java.io.File;
import java.util.Set;

/**
 * A listener for file(s) that are watched for
 * modifications.
 */
public interface HotSwapFilesModificationsListener {

    /**
     * Should this listener be enabled?
     * <p>
     * You often want to use such file modifications
     * listeners only during development, for autoreload
     * purposes. In production, you would disable it.
     * <p>
     * When this method return <code>false</code>, the
     * listener is not registered, at all.
     * <p>
     * A common pattern for this method is to use:
     * <pre>
     * return getSpincastConfig().isDevelopmentMode();
     * </pre>
     */
    public boolean isEnabled();

    /**
     * The file(s) to watch.
     */
    public Set<FileToWatch> getFilesToWatch();

    /**
     * Called when the watched file(s) are modified.
     * <p>
     * Receives the file that was modified.
     * <p>
     * The method is called in a new Thread.
     */
    public void fileModified(File modifiedFile);
}

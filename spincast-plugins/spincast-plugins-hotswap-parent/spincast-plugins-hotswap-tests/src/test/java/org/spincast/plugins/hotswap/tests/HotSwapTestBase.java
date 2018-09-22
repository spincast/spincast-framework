package org.spincast.plugins.hotswap.tests;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.nio.file.WatchKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.hotswap.HotSwapManager;
import org.spincast.plugins.hotswap.SpincastHotSwapPlugin;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsListener;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsWatcherDefault;

import com.google.inject.Inject;

public abstract class HotSwapTestBase extends NoAppStartHttpServerTestingBase {

    @Inject
    private HotSwapManager hotSwapManager;

    protected HotSwapManager getHotSwapManager() {
        return this.hotSwapManager;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHotSwapPlugin());
        return extraPlugins;
    }

    @Override
    public void afterClass() {
        super.afterClass();
        getHotSwapManager().getFilesModificationsWatcher().stopWatching();
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        getHotSwapManager().getFilesModificationsWatcher().removeAllListeners();
    }

    @SuppressWarnings("unchecked")
    protected Map<WatchKey, Set<HotSwapFilesModificationsListener>> getListenersByWatchKey() {
        try {
            Method getListenersByWatchKey =
                    HotSwapFilesModificationsWatcherDefault.class.getDeclaredMethod("getListenersByWatchKey");
            getListenersByWatchKey.setAccessible(true);
            Object resultObj = getListenersByWatchKey.invoke(getHotSwapManager().getFilesModificationsWatcher());
            assertTrue(resultObj instanceof Map);
            return (Map<WatchKey, Set<HotSwapFilesModificationsListener>>)resultObj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }



}

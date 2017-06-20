package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.guice.SpincastCorePlugin;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Module;

public class GetDefaultModuleTest {

    private static boolean initCalled = false;

    @Test
    public void getDefaultModuleAndCore() throws Exception {

        Module defaultModuleWithCore = Spincast.getDefaultModule(true);
        assertNotNull(defaultModuleWithCore);

        Spincast.configure()
                .disableCorePlugin()
                .disableAllDefaultPlugins()
                .module(defaultModuleWithCore)
                .init(new String[]{});
        assertTrue(initCalled);
        initCalled = false;
    }

    @Test
    public void getDefaultModuleOnly() throws Exception {

        Module defaultModuleOnly = Spincast.getDefaultModule(false);
        assertNotNull(defaultModuleOnly);

        // Core missing
        try {
            Spincast.configure()
                    .disableAllDefaultPlugins()
                    .module(defaultModuleOnly)
                    .init(new String[]{});
            fail();
        } catch (Exception ex) {
        }
        assertFalse(initCalled);

        Spincast.configure()
                .disableAllDefaultPlugins()
                .module(defaultModuleOnly)
                .plugin(new SpincastCorePlugin())
                .init(new String[]{});
        assertTrue(initCalled);
        initCalled = false;
    }

    @Inject
    protected void init() {
        initCalled = true;
    }

}

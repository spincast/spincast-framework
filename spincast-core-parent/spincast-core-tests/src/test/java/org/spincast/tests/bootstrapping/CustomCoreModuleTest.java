package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastCorePluginModule;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.SpincastUtilsDefault;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Scopes;

public class CustomCoreModuleTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        CustomCoreModuleTest.main(null);
    }

    /**
     * Testing SpincastUtils module
     */
    protected static class SpincastUtilsTesting extends SpincastUtilsDefault {

        @Inject
        public SpincastUtilsTesting(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public String getCacheBusterCode() {
            return "42";
        }
    }

    /**
     * Testing Core module
     */
    protected static class CoreModuleTesting extends SpincastCorePluginModule {

        @Override
        protected void bindSpincastUtilsClass() {
            bind(SpincastUtils.class).to(SpincastUtilsTesting.class).in(Scopes.SINGLETON);
        }
    }

    public static void main(String[] args) {

        try {
            Spincast.configure()
                    .disableCorePlugin()
                    .init(args);
            fail();
        } catch (Exception ex) {
        }
        assertFalse(initCalled);

        Spincast.configure()
                .disableCorePlugin()
                .module(new CoreModuleTesting())
                .init(args);
        assertTrue(initCalled);
    }

    @Inject
    protected void init(SpincastUtils spincastUtils) {
        initCalled = true;
        assertEquals("42", spincastUtils.getCacheBusterCode());
    }
}

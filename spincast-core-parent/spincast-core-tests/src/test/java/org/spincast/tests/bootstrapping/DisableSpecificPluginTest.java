package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigPluginModule;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class DisableSpecificPluginTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        DisableSpecificPluginTest.main(null);
    }

    /**
     * Testing module
     */
    protected static class SpincastConfigModuleTesting extends SpincastConfigPluginModule {

        @Override
        protected void configure() {
            super.configure();
            bind(String.class).annotatedWith(Names.named("testing")).toInstance("42");
        }
    }

    public static void main(String[] args) {

        try {
            Spincast.configure()
                    .disableDefaultConfigPlugin()
                    .init();
            fail();
        } catch(Exception ex) {
        }
        assertFalse(initCalled);

        Spincast.configure()
                .disableDefaultConfigPlugin()
                .module(new SpincastConfigModuleTesting())
                .init();
        assertTrue(initCalled);
    }

    @Inject
    protected void init(@Named("testing") String var) {
        initCalled = true;
        assertEquals("42", var);
    }
}

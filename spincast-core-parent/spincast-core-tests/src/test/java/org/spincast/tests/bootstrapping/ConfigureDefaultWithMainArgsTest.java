package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.guice.MainArgs;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class ConfigureDefaultWithMainArgsTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        ConfigureDefaultWithMainArgsTest.main(new String[]{"titi", "toto"});
    }

    public static void main(String[] args) {

        Spincast.configure()
                .mainArgs(args)
                .init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init(@MainArgs String[] mainArgs) {
        initCalled = true;

        assertNotNull(mainArgs);
        assertEquals(2, mainArgs.length);
        assertEquals("titi", mainArgs[0]);
        assertEquals("toto", mainArgs[1]);
    }
}

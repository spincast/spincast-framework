package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class AppClassTest {

    private static boolean initCalled = false;
    private static boolean initNewCalled = false;

    public static class AppClass {

        @Inject
        protected void init(Server server) {
            initNewCalled = true;
        }
    }

    @Test
    public void test() throws Exception {
        AppClassTest.main(null);
    }

    public static void main(String[] args) {

        Spincast.configure()
                .appClass(AppClass.class)
                .init();

        assertFalse(initCalled);
        assertTrue(initNewCalled);
    }

    @Inject
    protected void init(Server server) {
        initCalled = true;
    }
}

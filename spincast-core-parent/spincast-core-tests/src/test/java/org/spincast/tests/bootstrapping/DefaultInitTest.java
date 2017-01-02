package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class DefaultInitTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        DefaultInitTest.main(null);
    }

    public static void main(String[] args) {

        Spincast.init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init(Server server) {
        initCalled = true;
    }
}

package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class ConfigureDefaultTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        ConfigureDefaultTest.main(null);
    }

    public static void main(String[] args) {

        Spincast.configure()
                .init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init() {
        initCalled = true;
    }
}

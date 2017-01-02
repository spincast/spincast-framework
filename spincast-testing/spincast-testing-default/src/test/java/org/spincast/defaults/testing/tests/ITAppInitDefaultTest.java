package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestAppDefaultContextsBase;

import com.google.inject.Inject;

public class ITAppInitDefaultTest extends IntegrationTestAppDefaultContextsBase {

    protected static boolean initCalled = false;

    /**
     * Testing App
     */
    public static class App {

        public static void main(String[] args) {
            Spincast.init();
        }

        @Inject
        public void init() {
            initCalled = true;
        }
    }

    @Override
    protected void initApp() {
        App.main(null);
    }

    @Test
    public void initProperly() throws Exception {
        assertTrue(initCalled);
    }

}

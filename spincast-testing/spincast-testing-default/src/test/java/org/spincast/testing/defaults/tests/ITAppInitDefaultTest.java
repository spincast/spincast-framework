package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class ITAppInitDefaultTest extends IntegrationConfigTestClassBase {

    protected static boolean initCalled = false;

    /**
     * Testing App
     */
    public static class App {

        public static void main(String[] args) {
            Spincast.init(args);
        }

        @Inject
        public void init() {
            initCalled = true;
        }
    }

    @Override
    protected void callAppMainMethod() {
        App.main(null);
    }

    @Test
    public void initProperly() throws Exception {
        assertTrue(initCalled);
    }

}

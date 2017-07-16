package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

public class ITAppTestingConfigAreUsedTest extends IntegrationConfigTestClassBase {

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
    }

    /**
     * Testing App
     */
    public static class App {

        private SpincastConfig spincastConfig;

        public SpincastConfig getSpincastConfig() {
            return this.spincastConfig;
        }

        public static void main(String[] args) {
            Spincast.init(args);
        }

        @Inject
        public void init(SpincastConfig spincastConfig) {
            this.spincastConfig = spincastConfig;
        }
    }

    @Override
    protected void startApp() {
        App.main(null);
    }

    @Test
    public void test() throws Exception {
        assertNotNull(getApp());
        assertNotNull(getApp().getSpincastConfig());
        assertNotEquals(44419, getApp().getSpincastConfig().getHttpServerPort());
        assertFalse(getApp().getSpincastConfig().isDebugEnabled());
    }

}

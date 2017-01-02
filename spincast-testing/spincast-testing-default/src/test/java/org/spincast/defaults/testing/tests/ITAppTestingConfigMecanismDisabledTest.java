package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestAppDefaultContextsBase;

import com.google.inject.Inject;

public class ITAppTestingConfigMecanismDisabledTest extends IntegrationTestAppDefaultContextsBase {

    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

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
            Spincast.init();
        }

        @Inject
        public void init(SpincastConfig spincastConfig) {
            this.spincastConfig = spincastConfig;
        }
    }

    @Override
    protected void initApp() {
        App.main(null);
    }

    //==========================================
    // The configs should be the default, 
    // non testing, ones.
    //==========================================
    @Test
    public void test() throws Exception {
        assertNotNull(getSpincastConfig());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
        assertTrue(getSpincastConfig().isDebugEnabled());
    }

}

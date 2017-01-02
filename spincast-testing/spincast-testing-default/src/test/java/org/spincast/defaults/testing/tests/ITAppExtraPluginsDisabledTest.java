package org.spincast.defaults.testing.tests;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestAppDefaultContextsBase;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;

@ExpectingBeforeClassException
public class ITAppExtraPluginsDisabledTest extends IntegrationTestAppDefaultContextsBase {

    @Override
    protected boolean isEnableGuiceTweakerExtraPlugins() {
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

    @Test
    public void testEmpty() throws Exception {
    }

}
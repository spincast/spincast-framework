package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class UnitTestCustomConfigClassTest extends NoAppTestingBase {

    /**
     * Custom Spincast Config class
     */
    public static class SpincastConfigCustom extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected SpincastConfigCustom(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return 42;
        }
    }

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        return SpincastConfigCustom.class;
    }

    @Test
    public void test() throws Exception {
        assertEquals(42, getSpincastConfig().getHttpServerPort());
    }
}

package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class PebbleDebugModeTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Class<? extends SpincastConfig> getGuiceTweakerConfigImplementationClass() {
        return SpincastTestConfigTest.class;
    }

    public static class SpincastTestConfigTest extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected SpincastTestConfigTest(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }
    }

    @Inject
    protected SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Test
    public void cacheValuesDebugMode() throws Exception {
        assertEquals(0, getSpincastPebbleTemplatingEngineConfig().getTemplateCacheItemNbr());
        assertEquals(0, getSpincastPebbleTemplatingEngineConfig().getTagCacheTypeItemNbr());
        assertTrue(getSpincastPebbleTemplatingEngineConfig().isStrictVariablesEnabled());
    }

}

package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.pebble.ISpincastPebbleTemplatingEngineConfig;
import org.spincast.testing.core.SpincastTestConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class PebbleDebugModeTest extends SpincastDefaultNoAppIntegrationTestBase {

    public static class SpincastTestConfigTest extends SpincastTestConfig {

        @Override
        public boolean isDebugEnabled() {
            return true;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return SpincastTestConfigTest.class;
            }
        };
    }

    @Inject
    protected ISpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;

    protected ISpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Test
    public void cacheValuesDebugMode() throws Exception {
        assertEquals(0, getSpincastPebbleTemplatingEngineConfig().getTemplateCacheItemNbr());
        assertEquals(0, getSpincastPebbleTemplatingEngineConfig().getTagCacheTypeItemNbr());
        assertTrue(getSpincastPebbleTemplatingEngineConfig().isStrictVariablesEnabled());
    }

}

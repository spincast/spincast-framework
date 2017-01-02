package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public class PebbleDebugModeTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               bind(SpincastConfig.class).to(SpincastTestConfigTest.class)
                                                         .in(Scopes.SINGLETON);
                               return;
                           }
                       })
                       .init();
    }

    public static class SpincastTestConfigTest extends SpincastConfigTestingDefault {

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

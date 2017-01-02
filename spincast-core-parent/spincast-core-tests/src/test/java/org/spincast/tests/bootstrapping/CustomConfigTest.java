package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Scopes;

/**
 * Spincast should automatically detect that an implementation
 * has been bound for SpincastConfig.
 */
public class CustomConfigTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        CustomConfigTest.main(null);
    }

    public static interface SpincastConfigTest extends SpincastConfig {

        public String testing_config();
    }

    public static class SpincastConfigTestDefault extends SpincastConfigDefault implements SpincastConfigTest {

        @Override
        public int getHttpServerPort() {
            return 42;
        }

        @Override
        public String testing_config() {
            return "my app config";
        }
    }

    public static void main(String[] args) {

        Spincast.configure()
                .module(new SpincastGuiceModuleBase() {

                    @Override
                    protected void configure() {
                        bind(SpincastConfigTest.class).to(SpincastConfigTestDefault.class).in(Scopes.SINGLETON);
                    }
                })
                .init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init(SpincastConfigTest spincastConfigTest, SpincastConfig spincastConfig) {
        assertEquals("my app config", spincastConfigTest.testing_config());
        assertEquals(42, spincastConfigTest.getHttpServerPort());
        assertEquals(42, spincastConfig.getHttpServerPort());
        initCalled = true;
    }
}

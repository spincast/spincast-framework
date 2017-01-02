package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPlugin;
import org.spincast.plugins.config.SpincastConfigPluginModule;

import com.google.inject.Inject;
import com.google.inject.Module;

public class ReplaceDefaultPluginTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        ReplaceDefaultPluginTest.main(null);
    }

    public static class SpincastConfigTest extends SpincastConfigDefault {

        @Override
        public int getHttpServerPort() {
            return 4242;
        }
    }

    public static void main(String[] args) {

        Spincast.configure()
                .plugin(new SpincastConfigPlugin() {

                    @Override
                    protected Module getPluginModule(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
                        return new SpincastConfigPluginModule(specificSpincastConfigImplClass) {

                            @Override
                            protected Class<? extends SpincastConfig> getDefaultSpincastConfigImplClass() {
                                return SpincastConfigTest.class;
                            }
                        };
                    }
                }).init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init(SpincastConfig spincastConfig) {
        assertEquals(4242, spincastConfig.getHttpServerPort());
        initCalled = true;
    }
}

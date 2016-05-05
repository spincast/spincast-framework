package org.spincast.tests.cors;

import org.spincast.defaults.guice.DefaultSpincastRouterConfig;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.testing.utils.ExpectingInstanciationException;

import com.google.inject.Module;

@ExpectingInstanciationException
public class InvalidFilterPosition1Test extends DefaultIntegrationTestingBase {

    protected static class TestRoutingConfig extends DefaultSpincastRouterConfig {

        @Override
        public int getCorsFilterPosition() {
            return 1;
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected Class<? extends ISpincastRouterConfig> getSpincastRoutingPluginConfigClass() {
                return TestRoutingConfig.class;
            }
        };
    }

}

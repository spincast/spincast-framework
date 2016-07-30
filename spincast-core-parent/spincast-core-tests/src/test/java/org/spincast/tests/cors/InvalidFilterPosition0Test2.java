package org.spincast.tests.cors;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.plugins.routing.SpincastRouterConfigDefault;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException
public class InvalidFilterPosition0Test2 extends SpincastDefaultNoAppIntegrationTestBase {

    protected static class TestRoutingConfig extends SpincastRouterConfigDefault {

        @Inject
        public TestRoutingConfig(ISpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public int getCorsFilterPosition() {
            return 0;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bind(ISpincastRouterConfig.class).to(TestRoutingConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

}

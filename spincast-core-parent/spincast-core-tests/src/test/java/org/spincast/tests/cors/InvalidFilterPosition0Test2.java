package org.spincast.tests.cors;

import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.plugins.routing.SpincastRouterConfigDefault;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException
public class InvalidFilterPosition0Test2 extends DefaultIntegrationTestingBase {

    protected static class TestRoutingConfig extends SpincastRouterConfigDefault {

        @Override
        public int getCorsFilterPosition() {
            return 0;
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bind(ISpincastRouterConfig.class).to(TestRoutingConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

}

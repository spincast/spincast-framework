package org.spincast.tests.cors;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.routing.SpincastRouterConfig;
import org.spincast.plugins.routing.SpincastRouterConfigDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException
public class InvalidFilterPosition0Test2 extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastRouterConfig.class).to(TestRoutingConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected static class TestRoutingConfig extends SpincastRouterConfigDefault {

        @Inject
        public TestRoutingConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public int getCorsFilterPosition() {
            return 0;
        }
    }
}

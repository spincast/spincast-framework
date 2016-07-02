package org.spincast.tests;

import org.spincast.core.utils.ISpincastUtils;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Key;
import com.google.inject.Module;

@ExpectingBeforeClassException // Expect an exception!
public class InvalidCustomRouterConfigKeyTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void bindRoutingPlugin() {
                install(new SpincastRoutingPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

                    @Override
                    protected Key<?> getRouterImplementationKey() {
                        return Key.get(ISpincastUtils.class); // Invalid!
                    }
                });
            }
        };
    }

}

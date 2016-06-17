package org.spincast.tests;

import org.spincast.core.utils.ISpincastUtils;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.testing.utils.ExpectingInstanciationException;

import com.google.inject.Key;
import com.google.inject.Module;

@ExpectingInstanciationException // Expect an exception!
public class InvalidCustomRouterConfigKeyTest extends DefaultIntegrationTestingBase {

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

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

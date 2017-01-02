package org.spincast.tests;

import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.routing.SpincastRoutingPluginModule;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Injector;
import com.google.inject.Key;

@ExpectingBeforeClassException // Expect an exception!
public class InvalidCustomRouterConfigKeyTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .disableDefaultRoutingPlugin()
                       .module(new SpincastRoutingPluginModule() {

                           @Override
                           protected Key<?> getRouterImplementationKey() {
                               return Key.get(SpincastUtils.class);
                           }
                       })
                       .init();
    }

}

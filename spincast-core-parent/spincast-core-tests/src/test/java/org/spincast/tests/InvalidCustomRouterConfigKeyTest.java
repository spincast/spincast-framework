package org.spincast.tests;

import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.routing.SpincastRoutingPluginModule;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Key;

@ExpectingBeforeClassException // Expect an exception!
public class InvalidCustomRouterConfigKeyTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected SpincastBootstrapper createBootstrapper() {

        SpincastBootstrapper bootstrapper = super.createBootstrapper();

        return bootstrapper.disableDefaultRoutingPlugin()
                           .module(new SpincastRoutingPluginModule() {

                               @Override
                               protected Key<?> getRouterImplementationKey() {
                                   return Key.get(SpincastUtils.class);
                               }
                           });
    }

}

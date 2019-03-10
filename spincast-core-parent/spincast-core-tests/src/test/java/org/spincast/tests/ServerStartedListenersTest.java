package org.spincast.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class ServerStartedListenersTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override().with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                Multibinder<ServerStartedListener> serverStartedListenerMultibinder =
                        Multibinder.newSetBinder(binder(), ServerStartedListener.class);
                serverStartedListenerMultibinder.addBinding().to(TestLister.class).asEagerSingleton();
            }
        });
    }

    protected static final boolean[] called = new boolean[]{false};

    public static class TestLister implements ServerStartedListener {

        @Override
        public void serverStartedSuccessfully() {
            called[0] = true;
        }
    }

    @Test
    public void listenerWasCalled() throws Exception {

        //==========================================
        // Sleep since the listeners are called
        // asynchronously
        //==========================================
        Thread.sleep(500);
        assertTrue(called[0]);
    }

}

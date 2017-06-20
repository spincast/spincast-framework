package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Module;

public class ITAddPluginTest extends NoAppConfigTestClassBase {

    public static boolean inited = false;
    public static boolean applied = false;

    public static class TestPlugin extends SpincastPluginBase {

        @Override
        public String getId() {
            return TestPlugin.class.getName();
        }

        @Override
        public Module apply(Module currentModule) {
            applied = true;
            return currentModule;
        }
    }

    @Override
    protected GuiceTweaker createGuiceTweaker() {
        GuiceTweaker tweaker = super.createGuiceTweaker();
        tweaker.plugin(new TestPlugin());
        return tweaker;
    }

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
    }

    /**
     * Testing App
     */
    public static class App {

        public static void main(String[] args) {
            Spincast.init(args);
        }

        @Inject
        public void init(Server server) {
            inited = true;
        }
    }

    @Override
    protected void initApp() {
        App.main(null);
    }

    @Test
    public void test() throws Exception {
        assertTrue(inited);
        assertTrue(applied);
    }

}

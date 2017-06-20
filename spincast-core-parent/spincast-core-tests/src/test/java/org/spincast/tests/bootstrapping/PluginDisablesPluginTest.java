package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class PluginDisablesPluginTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        PluginDisablesPluginTest.main(null);
    }

    public static class SpincastDictionaryTest1 extends SpincastDictionaryDefault {

        @Inject
        public SpincastDictionaryTest1(LocaleResolver localeResolver) {
            super(localeResolver);
        }

        @Override
        public String exception_default_message() {
            return "111";
        }
    }

    public static class SpincastDictionaryTest2 extends SpincastDictionaryDefault {

        @Inject
        public SpincastDictionaryTest2(LocaleResolver localeResolver) {
            super(localeResolver);
        }

        @Override
        public String exception_default_message() {
            return "222";
        }
    }

    public static class TestPlugin1 extends SpincastPluginBase {

        public static final String PLUGIN_ID = TestPlugin1.class.getName();

        @Override
        public String getId() {
            return PLUGIN_ID;
        }

        @Override
        public Module apply(Module currentModule) {

            return Modules.combine(currentModule, new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    bind(SpincastDictionary.class).to(SpincastDictionaryTest1.class).in(Scopes.SINGLETON);
                }
            });
        }
    }

    public static class TestPlugin2 extends SpincastPluginBase {

        public static final String PLUGIN_ID = TestPlugin2.class.getName();

        @Override
        public String getId() {
            return PLUGIN_ID;
        }

        //==========================================
        // Disable the first plugin
        //==========================================
        @Override
        public Set<String> getPluginsToDisable() {
            return Sets.newHashSet(TestPlugin1.PLUGIN_ID);
        }

        @Override
        public Module apply(Module currentModule) {

            return Modules.combine(currentModule, new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    bind(SpincastDictionary.class).to(SpincastDictionaryTest2.class).in(Scopes.SINGLETON);
                }
            });
        }
    }

    public static void main(String[] args) {

        Spincast.configure()
                .plugin(new TestPlugin1())
                .plugin(new TestPlugin2())
                .init(args);
        assertTrue(initCalled);
    }

    @Inject
    protected void init(SpincastDictionary spincastDictionary) {
        assertEquals("222", spincastDictionary.exception_default_message());
        initCalled = true;
    }
}

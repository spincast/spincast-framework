package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class PluginConflictTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        PluginConflictTest.main(null);
    }

    public static class SpincastDictionaryTest1 extends SpincastDictionaryDefault {

        @Inject
        public SpincastDictionaryTest1(LocaleResolver localeResolver,
                                       TemplatingEngine templatingEngine,
                                       SpincastConfig spincastConfig,
                                       @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {
            super.addMessages();

            key(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE,
                msg("en", "111"));
        }
    }

    public static class SpincastDictionaryTest2 extends SpincastDictionaryDefault {

        @Inject
        public SpincastDictionaryTest2(LocaleResolver localeResolver,
                                       TemplatingEngine templatingEngine,
                                       SpincastConfig spincastConfig,
                                       @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {
            super.addMessages();

            key(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE,
                msg("en", "222"));
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
                    bind(Dictionary.class).to(SpincastDictionaryTest1.class).in(Scopes.SINGLETON);
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

        @Override
        public Module apply(Module currentModule) {

            return Modules.combine(currentModule, new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    bind(Dictionary.class).to(SpincastDictionaryTest2.class).in(Scopes.SINGLETON);
                }
            });
        }
    }

    public static void main(String[] args) {

        try {
            Spincast.configure()
                    .plugin(new TestPlugin1())
                    .plugin(new TestPlugin2())
                    .init(args);
            fail();
        } catch (Exception ex) {
        }
        assertFalse(initCalled);
    }

    @Inject
    protected void init(Dictionary spincastDictionary) {
        initCalled = true;
    }
}

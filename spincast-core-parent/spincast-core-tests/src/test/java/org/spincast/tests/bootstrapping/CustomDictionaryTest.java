package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;

import com.google.inject.Inject;
import com.google.inject.Scopes;

/**
 * Spincast should automatically detect that an implementation
 * has been bound for SpincastDictionary.
 */
public class CustomDictionaryTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        CustomDictionaryTest.main(null);
    }

    public static class SpincastDictionaryTestDefault extends SpincastDictionaryDefault {

        @Inject
        public SpincastDictionaryTestDefault(LocaleResolver localeResolver,
                                             TemplatingEngine templatingEngine,
                                             SpincastConfig spincastConfig,
                                             @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {
            super.addMessages();

            // Overrides a Spincast message
            key(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE,
                msg("en", "42"));

            key("testing.customMsg",
                msg("en", "my app message"));
        };
    }

    public static void main(String[] args) {

        Spincast.configure()
                .module(new SpincastGuiceModuleBase() {

                    @Override
                    protected void configure() {
                        bind(Dictionary.class).to(SpincastDictionaryTestDefault.class).in(Scopes.SINGLETON);
                    }
                })
                .init(args);

        assertTrue(initCalled);
    }

    @Inject
    protected void init(Dictionary dictionary) {
        assertEquals("my app message", dictionary.get("testing.customMsg"));
        assertEquals("42", dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE));
        initCalled = true;
    }
}

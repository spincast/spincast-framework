package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
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

    public static interface SpincastDictionaryTest extends SpincastDictionary {

        public String testing_custom_msg();
    }

    public static class SpincastDictionaryTestDefault extends SpincastDictionaryDefault implements SpincastDictionaryTest {

        @Inject
        public SpincastDictionaryTestDefault(LocaleResolver localeResolver) {
            super(localeResolver);
        }

        @Override
        public String exception_default_message() {
            return "42";
        }

        @Override
        public String testing_custom_msg() {
            return "my app message";
        }
    }

    public static void main(String[] args) {

        Spincast.configure()
                .module(new SpincastGuiceModuleBase() {

                    @Override
                    protected void configure() {
                        bind(SpincastDictionaryTest.class).to(SpincastDictionaryTestDefault.class).in(Scopes.SINGLETON);
                    }
                })
                .init();

        assertTrue(initCalled);
    }

    @Inject
    protected void init(SpincastDictionaryTest spincastDictionaryTest, SpincastDictionary spincastDictionary) {
        assertEquals("my app message", spincastDictionaryTest.testing_custom_msg());
        assertEquals("42", spincastDictionaryTest.exception_default_message());
        assertEquals("42", spincastDictionary.exception_default_message());
        initCalled = true;
    }
}

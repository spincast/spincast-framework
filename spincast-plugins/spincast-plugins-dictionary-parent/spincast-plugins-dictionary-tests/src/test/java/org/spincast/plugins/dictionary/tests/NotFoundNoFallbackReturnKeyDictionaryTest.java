package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.DictionaryEntryNotFoundBehavior;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class NotFoundNoFallbackReturnKeyDictionaryTest extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Dictionary.class).to(AppDictionary.class).in(Scopes.SINGLETON);
            }
        };
    }

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        return CustomTestingDefault.class;
    }

    /**
     * Custom configs
     */
    protected static class CustomTestingDefault extends SpincastConfigTestingDefault {

        @Inject
        protected CustomTestingDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public DictionaryEntryNotFoundBehavior getDictionaryEntryNotFoundBehavior() {
            return DictionaryEntryNotFoundBehavior.RETURN_KEY;
        }
    }

    /**
     * Custom dictionary
     */
    protected static class AppDictionary extends SpincastDictionaryDefault {

        @Inject
        public AppDictionary(LocaleResolver localeResolver,
                             TemplatingEngine templatingEngine,
                             SpincastConfig spincastConfig,
                             @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {

            key("my.custom.msg.1",
                msg("en", "custom 111"));

            key("my.custom.msg.2",
                msg("en", "custom 222"),
                msg("fr", "custom 222 fr"));
        }
    }

    @Inject
    Dictionary dictionary;

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    @Test
    public void inEnglish() throws Exception {
        assertEquals("custom 111", getDictionary().get("my.custom.msg.1", Locale.ENGLISH));
        assertEquals("custom 222", getDictionary().get("my.custom.msg.2", Locale.ENGLISH));
    }

    @Test
    public void inFrenchNoFallbackToEnglish() throws Exception {

        assertEquals("custom 222 fr", getDictionary().get("my.custom.msg.2", Locale.FRENCH));

        // return key
        assertEquals("my.custom.msg.1", getDictionary().get("my.custom.msg.1", Locale.FRENCH));
    }

    @Test
    public void notFoundReturnKey() throws Exception {
        assertEquals("nope.nono", getDictionary().get("nope.nono"));
    }

    @Test
    public void nullReturnsNull() throws Exception {
        assertEquals(null, getDictionary().get(null));
    }

}





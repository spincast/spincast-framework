package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.DictionaryKeyNotFoundException;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntries;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.Pair;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class DefaultDictionaryTest extends NoAppTestingBase {

    @Inject
    SpincastCoreDictionaryEntries spincastCoreDictionaryEntries;

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Dictionary.class).to(AppDictionary.class).in(Scopes.SINGLETON);
            }
        };
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

            // Override Spincast core message
            key(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE,
                msg("", "custom override"));

            key("my.custom.msg.1",
                msg("", "custom 111"));

            key("my.custom.msg.2",
                msg("", "custom 222"),
                msg("fr", "custom 222 fr"));

            key("my.custom.msg.3",
                msg("en", "custom 333"),
                msg("fr", "custom 333 fr"));

            key("my.custom.msg.4",
                msg("en", "Hi {{name}}!"),
                msg("fr", "Allo {{name}}!"));
        }
    }

    @Inject
    Dictionary dictionary;

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    @Test
    public void hasSpincastCoreMessages() throws Exception {
        Map<String, Map<String, String>> coreDictionaryEntries = this.spincastCoreDictionaryEntries.getDictionaryEntries();
        assertNotNull(coreDictionaryEntries);

        for (String key : coreDictionaryEntries.keySet()) {
            assertNotNull(getDictionary().get(key));
        }
    }

    @Test
    public void spincastMessageOverriden() throws Exception {
        assertEquals("custom override",
                     getDictionary().get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE));
    }

    @Test
    public void inEnglish() throws Exception {
        assertEquals("custom 111", getDictionary().get("my.custom.msg.1", Locale.ENGLISH));
        assertEquals("custom 222", getDictionary().get("my.custom.msg.2", Locale.ENGLISH));
    }

    @Test
    public void fallbackToBase() throws Exception {
        assertEquals("custom 222 fr", getDictionary().get("my.custom.msg.2", Locale.FRENCH));
        assertEquals("custom 111", getDictionary().get("my.custom.msg.1", Locale.FRENCH));
        assertEquals("custom 222", getDictionary().get("my.custom.msg.2", Locale.JAPAN));
    }

    @Test
    public void notFoundException() throws Exception {
        try {
            getDictionary().get("nope");
            fail();
        } catch (DictionaryKeyNotFoundException ex) {
        }
    }

    @Test
    public void nullException() throws Exception {
        try {
            getDictionary().get(null);
            fail();
        } catch (DictionaryKeyNotFoundException ex) {
        }
    }

    @Test
    public void noFallback() throws Exception {
        assertEquals("custom 333", getDictionary().get("my.custom.msg.3", Locale.ENGLISH));
        assertEquals("custom 333 fr", getDictionary().get("my.custom.msg.3", Locale.FRENCH));

        try {
            getDictionary().get("my.custom.msg.3", Locale.JAPAN);
            fail();
        } catch (DictionaryKeyNotFoundException ex) {
        }
    }

    @Test
    public void getAllKeys() throws Exception {

        Set<String> allKeys = getDictionary().getAllKeys();
        assertNotNull(allKeys);
        assertTrue(allKeys.size() > 3);
        assertTrue(allKeys.contains("my.custom.msg.1"));
        assertTrue(allKeys.contains("my.custom.msg.2"));
        assertTrue(allKeys.contains("my.custom.msg.3"));
        assertTrue(allKeys.contains("my.custom.msg.4"));
    }

    @Test
    public void getAll() throws Exception {

        Map<String, String> byLang = getDictionary().getAll("my.custom.msg.2");
        assertNotNull(byLang);
        assertEquals(2, byLang.size());
        assertEquals("custom 222", byLang.get("en"));
        assertEquals("custom 222 fr", byLang.get("fr"));
    }

    @Test
    public void getAllWithParams() throws Exception {

        Map<String, String> byLang = getDictionary().getAll("my.custom.msg.4", Pair.of("name", "Sromgol"));
        assertNotNull(byLang);
        assertEquals(2, byLang.size());
        assertEquals("Hi Sromgol!", byLang.get("en"));
        assertEquals("Allo Sromgol!", byLang.get("fr"));
    }

    @Test
    public void hasKey() throws Exception {

        assertTrue(getDictionary().hasKey("my.custom.msg.1"));
        assertTrue(getDictionary().hasKey("my.custom.msg.2"));
        assertFalse(getDictionary().hasKey("nope"));
        assertFalse(getDictionary().hasKey(""));
        assertFalse(getDictionary().hasKey(null));
    }


}





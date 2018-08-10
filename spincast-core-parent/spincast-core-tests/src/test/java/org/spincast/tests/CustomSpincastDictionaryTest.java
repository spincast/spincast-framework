package org.spincast.tests;

import static org.junit.Assert.assertEquals;

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
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class CustomSpincastDictionaryTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule2() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Dictionary.class).to(CustomSpincastDictionary.class).in(Scopes.SINGLETON);
            }
        };
    }

    public static class CustomSpincastDictionary extends SpincastDictionaryDefault {

        @Inject
        public CustomSpincastDictionary(LocaleResolver localeResolver,
                                        TemplatingEngine templatingEngine,
                                        SpincastConfig spincastConfig,
                                        @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {
            super.addMessages();

            //==========================================
            // Overrides a Spincast core message
            //==========================================
            key(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE,
                msg("en", "Not found custom message"));
        }
    }

    @Test
    public void testNotFoundCustomMessage() throws Exception {

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Not found custom message", response.getContentAsString());
    }

}

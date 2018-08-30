package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.fail;

import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.plugins.request.SpincastRequestPluginDictionaryEntries;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException()
public class PluginDictionaryMessageDeletePluginRequiredKeyTest extends NoAppStartHttpServerTestingBase {

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

            //==========================================
            // Deletes a key that is required...
            //==========================================
            getMessages().remove(SpincastRequestPluginDictionaryEntries.MESSAGE_KEY_FORM_GET_EMPTYNAME);
        }
    }

    @Test
    public void test() throws Exception {
        fail();
    }

}

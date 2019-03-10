package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.fail;

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
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException()
public class PluginDictionaryMessageDeleteCoreRequiredKeyTest extends NoAppStartHttpServerTestingBase {

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
            getMessages().remove(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE);
        }
    }

    @Test
    public void test() throws Exception {
        fail();
    }

}

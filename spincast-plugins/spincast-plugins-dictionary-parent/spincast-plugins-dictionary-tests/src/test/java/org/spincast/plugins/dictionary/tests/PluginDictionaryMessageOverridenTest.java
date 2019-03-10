package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.request.SpincastRequestPluginDictionaryEntries;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PluginDictionaryMessageOverridenTest extends NoAppStartHttpServerTestingBase {

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
            // Overrides the plugin's message!
            //==========================================
            key(SpincastRequestPluginDictionaryEntries.MESSAGE_KEY_FORM_GET_EMPTYNAME,
                msg("", "my custom override"));
        }
    }

    @Test
    public void formNameEmptyOverridenMessage() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    context.request().getFormOrCreate("");
                    fail();
                } catch (Exception ex) {
                    assertEquals("my custom override", ex.getMessage());
                }

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}

package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;
import org.spincast.plugins.dictionary.SpincastDictionaryPluginGuiceModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;

public class CustomSpincastDictionaryTest extends SpincastDefaultNoAppIntegrationTestBase {

    public static class CustomSpincastDictionary extends SpincastDictionaryDefault {

        @Inject
        public CustomSpincastDictionary(LocaleResolver localeResolver) {
            super(localeResolver);
        }

        @Override
        public String route_notFound_default_message() {
            return "Not found custom message";
        }
    }

    /**
     * Custom module
     */
    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void bindDictionaryPlugin() {
                install(new SpincastDictionaryPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

                    @Override
                    protected Class<? extends SpincastDictionary> bindSpincastDictionaryImplClass() {
                        return CustomSpincastDictionary.class;
                    }
                });
            }
        };
    }

    @Test
    public void testNotFoundCustomMessage() throws Exception {

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Not found custom message", response.getContentAsString());
    }

}

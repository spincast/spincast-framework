package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.dictionary.SpincastDictionary;
import org.spincast.plugins.dictionary.SpincastDictionaryPluginGuiceModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;

public class CustomSpincastDictionaryTest extends DefaultIntegrationTestingBase {

    public static class CustomSpincastDictionary extends SpincastDictionary {

        @Inject
        public CustomSpincastDictionary(ILocaleResolver localeResolver) {
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
        return new DefaultTestingModule() {

            @Override
            protected void bindDictionaryPlugin() {
                install(new SpincastDictionaryPluginGuiceModule(getRequestContextType(), getWebsocketContextType()) {

                    @Override
                    protected Class<? extends ISpincastDictionary> bindSpincastDictionaryImplClass() {
                        return CustomSpincastDictionary.class;
                    }
                });
            }
        };
    }

    @Test
    public void testNotFoundCustomMessage() throws Exception {

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Not found custom message", response.getContentAsString());
    }

}

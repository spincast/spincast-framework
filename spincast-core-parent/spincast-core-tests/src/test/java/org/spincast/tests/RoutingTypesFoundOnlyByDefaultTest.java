package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class RoutingTypesFoundOnlyByDefaultTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected Dictionary dictionary;

    @Test
    public void defaultRouteTypes() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());
            }
        });

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE),
                     response.getContentAsString());
    }

}

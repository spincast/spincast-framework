package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class RoutingTypesFoundOnlyByDefaultTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected SpincastDictionary spincastDictionary;

    @Test
    public void defaultRouteTypes() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());
            }
        });

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

}

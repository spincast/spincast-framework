package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class RoutingTypesFoundOnlyByDefaultTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ISpincastDictionary spincastDictionary;

    @Test
    public void defaultRouteTypes() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

}

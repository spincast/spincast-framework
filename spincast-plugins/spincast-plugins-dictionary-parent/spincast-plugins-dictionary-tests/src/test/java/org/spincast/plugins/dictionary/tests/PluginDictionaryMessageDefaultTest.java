package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class PluginDictionaryMessageDefaultTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void formNameEmptyDefaultMessage() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    context.request().getFormOrCreate("");
                    fail();
                } catch (Exception ex) {
                    assertEquals("The name can't be empty.", ex.getMessage());
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

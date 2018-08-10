package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class PluginDictionaryMessageDefaultTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void formNameEmptyDefaultMessage() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    context.request().getForm("");
                    fail();
                } catch (Exception ex) {
                    assertEquals("The name can't be empty.", ex.getMessage());
                }

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}

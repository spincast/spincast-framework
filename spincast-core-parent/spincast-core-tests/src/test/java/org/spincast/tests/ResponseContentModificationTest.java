package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;

public class ResponseContentModificationTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void changeContent() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().setStatusCode(HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE);
                context.response().sendPlainText("plain text");
            }
        });
        getRouter().ALL("/*{path}").pos(1).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().resetEverything();
                context.response().sendHtml("html");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("html", response.getContentAsString());
    }

    @Test
    public void changeContentHeaderSent() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().setStatusCode(HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE);

                // flush
                context.response().sendPlainText("1", true);

                // flush
                context.response().sendPlainText("2", true);

                // no flush
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });
        getRouter().ALL("/*{path}").pos(1).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String unsentCharacters = context.response().getUnsentCharacters();
                assertEquals(SpincastTestingUtils.TEST_STRING, unsentCharacters);

                context.response().resetEverything();
                context.response().sendHtml("4");
            }
        });

        HttpResponse response = GET("/one").send();

        //==========================================
        // Headers and sent content can't be changed
        //==========================================
        assertEquals(HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("124", response.getContentAsString());
    }

}

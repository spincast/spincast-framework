package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;
import com.google.inject.Module;

public class MethodReferenceAsHandlersOrLambdasTest extends NoAppStartHttpServerTestingBase {

    public static class MyController {

        public void test(DefaultRequestContext context) {
            context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
        }
    }

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(MyController.class);
            }
        };
    }

    @Inject
    protected MyController ctl;

    @Test
    public void methodHandler() throws Exception {

        getRouter().GET("/one").handle(this.ctl::test);

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void lambda() throws Exception {

        getRouter().GET("/one").handle(context -> context.response().sendPlainText(SpincastTestingUtils.TEST_STRING));

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}

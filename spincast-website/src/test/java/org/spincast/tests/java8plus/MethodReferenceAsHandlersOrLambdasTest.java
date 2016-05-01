package org.spincast.tests.java8plus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;
import com.google.inject.Module;

public class MethodReferenceAsHandlersOrLambdasTest extends DefaultIntegrationTestingBase {

    public static class MyController {

        public void test(IDefaultRequestContext context) {
            context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bind(MyController.class);
            }
        };
    }

    @Inject
    protected MyController ctl;

    @Test
    public void methodHandler() throws Exception {

        getRouter().GET("/one").save(this.ctl::test);

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void lambda() throws Exception {

        getRouter().GET("/one").save(context -> context.response().sendPlainText(SpincastTestUtils.TEST_STRING));

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}

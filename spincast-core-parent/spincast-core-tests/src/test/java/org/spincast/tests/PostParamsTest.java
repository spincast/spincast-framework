package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class PostParamsTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void test() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String value = context.request().getFormDataFirst("param1");
                assertNotNull(value);
                assertEquals(SpincastTestUtils.TEST_STRING, value);

                value = context.request().getFormDataFirst(SpincastTestUtils.TEST_STRING);
                assertNotNull(value);
                assertEquals("value2", value);

                List<String> values = context.request().getFormData("multiple");
                assertNotNull(value);
                assertEquals(2, values.size());
                assertEquals("multipleVal1", values.get(0));
                assertEquals("multipleVal2", values.get(1));

                value = context.request().getFormDataFirst("nope");
                assertNull(value);

                values = context.request().getFormData("nope");
                assertNotNull(values);
                assertEquals(0, values.size());
            }
        });

        List<String> paramValues = new ArrayList<String>();
        paramValues.add("multipleVal1");
        paramValues.add("multipleVal2");

        IHttpResponse response = POST("/one").addEntityFormDataValue("param1", SpincastTestUtils.TEST_STRING)
                                             .addEntityFormDataValue(SpincastTestUtils.TEST_STRING, "value2")
                                             .setEntityFormData("multiple", paramValues).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

}

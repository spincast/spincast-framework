package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class RequestPathParamsAndQueryStringParamsTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void paramsAndQueryStringParams() throws Exception {

        getRouter().GET("/${param1}/two/${param2}/four/*{param3}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String val = context.request().getPathParam("param1");
                assertEquals("one", val);

                val = context.request().getPathParam("param2");
                assertEquals("three", val);

                val = context.request().getPathParam("param3");
                assertEquals("five/six", val);

                val = context.request().getPathParam("nope");
                assertNull(val);

                Map<String, List<String>> querystringParams = context.request().getQueryStringParams();
                assertEquals(2, querystringParams.size());

                List<String> key1Values = context.request().getQueryStringParam("key1");
                assertNotNull(key1Values);
                assertEquals(1, key1Values.size());
                assertEquals("val1", key1Values.get(0));

                List<String> key2Values = context.request().getQueryStringParam("key2");
                assertNotNull(key2Values);
                assertEquals(2, key2Values.size());
                assertEquals("vala", key2Values.get(0));
                assertEquals("valb", key2Values.get(1));

                List<String> nopeValues = context.request().getQueryStringParam("nope");
                assertNotNull(nopeValues);
                assertEquals(0, nopeValues.size());

                String key1FirstValue = context.request().getQueryStringParamFirst("key1");
                assertEquals("val1", key1FirstValue);

                String key2FirstValue = context.request().getQueryStringParamFirst("key2");
                assertEquals("vala", key2FirstValue);

                String nopeFirstValue = context.request().getQueryStringParamFirst("nope");
                assertNull(nopeFirstValue);
            }
        });

        String url = createTestUrl("/one/two/three/four/five/six/") + "?key1=val1&key2=vala&key2=valb";

        IHttpResponse response = GET(url, true).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}

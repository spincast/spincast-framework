package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.CustomRouter;

public class CustomRouterTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected SpincastBootstrapper createBootstrapper() {

        SpincastBootstrapper bootstrapper = super.createBootstrapper();

        return bootstrapper.disableDefaultRoutingPlugin()
                           .plugin(new SpincastRoutingPlugin(CustomRouter.class));
    }

    @Test
    public void testStandard() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void testDirException() throws Exception {

        try {
            getRouter().dir("/one").classpath("/test").save();
            fail();
        } catch (Exception ex) {
            assertEquals("test123", ex.getMessage());
        }

    }

}

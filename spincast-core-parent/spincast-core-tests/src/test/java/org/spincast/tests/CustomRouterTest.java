package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.CustomRouter;

import com.google.inject.Injector;

public class CustomRouterTest extends IntegrationTestNoAppDefaultContextsBase {

    /**
     * Disabled and replaces the default routing plugin.
     */
    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .disableDefaultRoutingPlugin()
                       .plugin(new SpincastRoutingPlugin(CustomRouter.class))
                       .init(new String[]{});
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

package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.DefaultHandler;
import org.spincast.plugins.routing.DefaultRouter;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class DefaultComponentsTest extends NoAppStartHttpServerTestingBase {

    // DefaultRouter
    @Inject
    protected DefaultRouter defaultRouter;

    // Router
    @SuppressWarnings("rawtypes")
    @Inject
    protected Router genericRouter;

    // Router<?, ?>
    @Inject
    protected Router<?, ?> genericRouterPara;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void defaultComponents() throws Exception {

        // DefaultHandler
        this.defaultRouter.GET("/default").handle(new DefaultHandler() {

            // DefaultRequestContext
            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        // Handler
        this.genericRouter.GET("/generic").handle(new Handler() {

            @Override
            public void handle(RequestContext context) {
                context.response().sendPlainText("generic");
            }

        });

        // Router<?>
        List<?> mainRoutes = this.genericRouterPara.getMainRoutes();
        assertNotNull(mainRoutes);
        assertEquals(2, mainRoutes.size());

        HttpResponse response = GET("/default").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        response = GET("/generic").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("generic", response.getContentAsString());

    }

}

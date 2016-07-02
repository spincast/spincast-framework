package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.routing.IDefaultHandler;
import org.spincast.plugins.routing.IDefaultRouter;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class DefaultComponentsTest extends SpincastDefaultNoAppIntegrationTestBase {

    // IDefaultRouter
    @Inject
    protected IDefaultRouter defaultRouter;

    // IRouter
    @SuppressWarnings("rawtypes")
    @Inject
    protected IRouter genericRouter;

    // IRouter<?, ?>
    @Inject
    protected IRouter<?, ?> genericRouterPara;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void defaultComponents() throws Exception {

        // IDefaultHandler
        this.defaultRouter.GET("/default").save(new IDefaultHandler() {

            // IDefaultRequestContext
            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        // IHandler
        this.genericRouter.GET("/generic").save(new IHandler() {

            @Override
            public void handle(IRequestContext context) {
                context.response().sendPlainText("generic");
            }

        });

        // IRouter<?>
        List<?> mainRoutes = this.genericRouterPara.getMainRoutes();
        assertNotNull(mainRoutes);
        assertEquals(2, mainRoutes.size());

        IHttpResponse response = GET("/default").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        response = GET("/generic").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("generic", response.getContentAsString());

    }

}

package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.CustomRouter2;

import com.google.inject.Injector;

public class CustomRouterNotParameterizedTest extends IntegrationTestNoAppDefaultContextsBase {

    /**
     * Replace the default routing plugin
     */
    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .plugin(new SpincastRoutingPlugin(CustomRouter2.class))
                       .init(new String[]{});
    }

    @Override
    protected void clearRoutes() {
        // We don't want to clear router data here!
    }

    @Test
    public void test() throws Exception {

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

    }
}

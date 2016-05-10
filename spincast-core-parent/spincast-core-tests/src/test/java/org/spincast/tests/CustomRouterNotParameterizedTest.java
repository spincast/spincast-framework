package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.CustomRouter2;

import com.google.inject.Key;
import com.google.inject.Module;

public class CustomRouterNotParameterizedTest extends DefaultIntegrationTestingBase {

    @Override
    protected void clearRoutes() {
        // We don't want to clear router data here!
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void bindRoutingPlugin() {

                bindRoutingPluginConfigurations();

                install(new SpincastRoutingPluginGuiceModule(getRequestContextType()) {

                    @Override
                    protected Key<?> getRouterImplementationKey() {
                        return Key.get(CustomRouter2.class);
                    }
                });
            }
        };
    }

    @Test
    public void test() throws Exception {

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

    }
}

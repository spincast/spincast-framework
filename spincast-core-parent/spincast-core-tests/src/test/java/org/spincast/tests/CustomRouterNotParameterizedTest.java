package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.routing.Router;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.tests.varia.CustomRouter2;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class CustomRouterNotParameterizedTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Router.class).to(CustomRouter2.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {

        //==========================================
        // Replaces the default routing plugin
        //==========================================
        List<SpincastPlugin> extraPlugins = new ArrayList<SpincastPlugin>();
        extraPlugins.add(new SpincastRoutingPlugin());
        return extraPlugins;
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
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

    }
}

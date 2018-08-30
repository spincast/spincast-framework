package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.CustomRouter;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class CustomRouterTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Router.class).to(CustomRouter.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected SpincastBootstrapper createBootstrapper() {

        SpincastBootstrapper bootstrapper = super.createBootstrapper();

        return bootstrapper.disableDefaultRoutingPlugin()
                           .plugin(new SpincastRoutingPlugin());
    }

    @Test
    public void testStandard() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

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
            getRouter().dir("/one").classpath("/" + UUID.randomUUID().toString()).handle();
            fail();
        } catch (Exception ex) {
            assertEquals("test123", ex.getMessage());
        }

    }

}

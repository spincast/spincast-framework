package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.Server;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.ssl.SSLContextFactory;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.undertow.CacheBusterRemovalHandlerFactory;
import org.spincast.plugins.undertow.CorsHandlerFactory;
import org.spincast.plugins.undertow.FileClassPathResourceManagerFactory;
import org.spincast.plugins.undertow.GzipCheckerHandlerFactory;
import org.spincast.plugins.undertow.SkipResourceOnQueryStringHandlerFactory;
import org.spincast.plugins.undertow.SpincastHttpAuthIdentityManagerFactory;
import org.spincast.plugins.undertow.SpincastResourceHandlerFactory;
import org.spincast.plugins.undertow.SpincastUndertowServer;
import org.spincast.plugins.undertow.SpincastUndertowUtils;
import org.spincast.plugins.undertow.WebsocketEndpointFactory;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;
import org.xnio.OptionMap;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.LearningPushHandler;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

/**
 * HTTP/2 is currently not that easy to test on a Java 8
 * plateform. We will improve those tests once Spincast is based
 * on Java 11+, when our HTTP Client is updated and when
 * ALPN is available out-of-the-box.
 * <p>
 * You can easily test if HTTP/2 works properly will enabling it
 * in your Spincast application, make a request and look at the
 * dev tools of your browser (there are also brwoser add-ons to
 * whow if your site uses HTTP/2).
 */
public class Http2DefaultTest extends NoAppStartHttpServerTestingBase {

    @Inject
    private SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected static String[] pathWrapper = new String[]{null};
    protected static HttpString[] httpMethodWrapper = new HttpString[]{null};
    protected static HeaderMap[] headerMapWrapper = new HeaderMap[]{null};

    public static class TestingSpincastUndertowServer extends SpincastUndertowServer {

        @Inject
        public TestingSpincastUndertowServer(SpincastUndertowUtils spincastUndertowUtils,
                                             SpincastConfig config, SpincastUndertowConfig spincastUndertowConfig,
                                             FrontController frontController, SpincastUtils spincastUtils,
                                             CookieFactory cookieFactory, CorsHandlerFactory corsHandlerFactory,
                                             GzipCheckerHandlerFactory gzipCheckerHandlerFactory,
                                             SkipResourceOnQueryStringHandlerFactory skipResourceOnQueryStringHandlerFactory,
                                             SpincastResourceHandlerFactory spincastResourceHandlerFactory,
                                             CacheBusterRemovalHandlerFactory cacheBusterRemovalHandlerFactory,
                                             FileClassPathResourceManagerFactory fileClassPathResourceManagerFactory,
                                             SpincastHttpAuthIdentityManagerFactory spincastHttpAuthIdentityManagerFactory,
                                             WebsocketEndpointFactory spincastWebsocketEndpointFactory,
                                             SSLContextFactory sslContextFactory) {
            super(spincastUndertowUtils,
                  config,
                  spincastUndertowConfig,
                  frontController,
                  spincastUtils,
                  cookieFactory,
                  corsHandlerFactory,
                  gzipCheckerHandlerFactory,
                  skipResourceOnQueryStringHandlerFactory,
                  spincastResourceHandlerFactory,
                  cacheBusterRemovalHandlerFactory,
                  fileClassPathResourceManagerFactory,
                  spincastHttpAuthIdentityManagerFactory,
                  spincastWebsocketEndpointFactory,
                  sslContextFactory);
        }

        @Override
        protected boolean isPushSupported(ServerConnection connection) {
            return true;
        }

        @Override
        protected void pushResource(ServerConnection connection, String path, HttpString httpMethod, HeaderMap headerMap) {
            pathWrapper[0] = path;
            httpMethodWrapper[0] = httpMethod;
            headerMapWrapper[0] = headerMap;
        }

    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Server.class).to(TestingSpincastUndertowServer.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        pathWrapper[0] = null;
        httpMethodWrapper[0] = null;
        headerMapWrapper[0] = null;
    }

    @Test
    public void http2EnabledByDefault() throws Exception {

        Server server = getServer();
        assertTrue(server instanceof SpincastUndertowServer);
        SpincastUndertowServer spincastUndertowServer = (SpincastUndertowServer)server;

        Field undertowServerField = SpincastUndertowServer.class.getDeclaredField("undertowServer");
        undertowServerField.setAccessible(true);

        Undertow undertow = (Undertow)undertowServerField.get(spincastUndertowServer);
        Field serverOptionsField = Undertow.class.getDeclaredField("serverOptions");
        serverOptionsField.setAccessible(true);

        OptionMap optionMap = (OptionMap)serverOptionsField.get(undertow);
        Boolean http2Enabled = optionMap.get(UndertowOptions.ENABLE_HTTP2);
        assertTrue(http2Enabled);

        //==========================================
        // LearningPushHandler disabled by default
        //==========================================
        Field learningPushHandlerField = SpincastUndertowServer.class.getDeclaredField("learningPushHandler");
        learningPushHandlerField.setAccessible(true);
        LearningPushHandler learningPushHandler = (LearningPushHandler)learningPushHandlerField.get(spincastUndertowServer);
        assertNull(learningPushHandler);
    }

    @Test
    public void pushDefault() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.OPTIONS,
                             "/titi",
                             SpincastStatics.map(HttpHeaders.ACCEPT_LANGUAGE,
                                                 Lists.newArrayList(ContentTypeDefaults.JSON.getMainVariation())))
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        assertEquals(HttpMethod.OPTIONS.name(), httpMethodWrapper[0].toString());
        assertEquals("/titi", pathWrapper[0]);
        assertEquals(ContentTypeDefaults.JSON.getMainVariation(),
                     headerMapWrapper[0].getFirst(HttpHeaders.ACCEPT_LANGUAGE));
    }

    @Test
    public void pushWithCacheBuster() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "/toto${cacheBuster}.js", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        assertEquals(HttpMethod.GET.name(), httpMethodWrapper[0].toString());
        assertEquals("/toto" + getSpincastUtils().getCacheBusterCode() + ".js", pathWrapper[0]);
    }

}

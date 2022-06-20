package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.ResourceToPush;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.ssl.SSLContextFactory;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.undertow.CacheBusterRemovalHandlerFactory;
import org.spincast.plugins.undertow.CorsHandlerFactory;
import org.spincast.plugins.undertow.SpincastClassPathResourceManagerFactory;
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

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.util.HeaderMap;

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
public class Http2PushHeadersTest extends NoAppStartHttpServerTestingBase {

    @Inject
    private SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

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
                                             SpincastClassPathResourceManagerFactory fileClassPathResourceManagerFactory,
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
            return false;
        }

        @Override
        protected void sendPushHeaders(HttpServerExchange exchange, ResourceToPush resourceToPush) {
            super.sendPushHeaders(exchange, resourceToPush);
            headerMapWrapper[0] = exchange.getResponseHeaders();
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
        headerMapWrapper[0] = null;
    }

    @Test
    public void pushHeadersScript() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "/toto${cacheBuster}.js?param1=val1", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String linkHeader = headerMapWrapper[0].getFirst(HttpHeaders.LINK);
        assertNotNull(linkHeader);

        assertEquals("</toto" + getSpincastUtils().getCacheBusterCode() + ".js?param1=val1>; as=script; rel=preload", linkHeader);
    }

    @Test
    public void pushHeadersImage() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "toto.png", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String linkHeader = headerMapWrapper[0].getFirst(HttpHeaders.LINK);
        assertNotNull(linkHeader);

        assertEquals("</toto.png>; as=image; rel=preload", linkHeader);
    }

    @Test
    public void pushHeadersVideo() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "/toto.mkv", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String linkHeader = headerMapWrapper[0].getFirst(HttpHeaders.LINK);
        assertNotNull(linkHeader);

        assertEquals("</toto.mkv>; as=video; rel=preload", linkHeader);
    }

    @Test
    public void pushHeadersAudio() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "/toto.mp3", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String linkHeader = headerMapWrapper[0].getFirst(HttpHeaders.LINK);
        assertNotNull(linkHeader);

        assertEquals("</toto.mp3>; as=audio; rel=preload", linkHeader);
    }

    @Test
    public void pushHeadersStyle() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .push(HttpMethod.GET, "/toto.css?a=1&b=2", null)
                       .sendPlainText("ok!");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String linkHeader = headerMapWrapper[0].getFirst(HttpHeaders.LINK);
        assertNotNull(linkHeader);

        assertEquals("</toto.css?a=1&b=2>; as=style; rel=preload", linkHeader);
    }

}

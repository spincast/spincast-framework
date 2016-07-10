package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.core.websocket.IWebsocketEndpointManager;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

public class RedirectRulesTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Override
    public void beforeTest() {
        super.beforeTest();

        //==========================================
        // For the WebSocket tests
        //==========================================
        List<IWebsocketEndpointManager> websocketEndpointManagers = getServer().getWebsocketEndpointManagers();
        for(IWebsocketEndpointManager manager : websocketEndpointManagers) {
            manager.closeEndpoint();
        }
        assertTrue(SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getServer().getWebsocketEndpointManagers().size() == 0;
            }
        }));
    }

    @Test
    public void redirectDefault() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectPermanently() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").permanently().to("/two");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectTemporarily() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").temporarily().to("/two");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/nope");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void fullUrl() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        String fullUrl = createTestUrl("/two");
        getRouter().redirect("/one").to(fullUrl);

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void oldRouteNotFound() throws Exception {

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void dynParams() throws Exception {

        getRouter().GET("/two/three/four").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/${param2}").to("/two/${param1}/${param2}");

        IHttpResponse response = GET("/one/three/four").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void splatParam() throws Exception {

        getRouter().GET("/two/three/four").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/*{path}").to("/two/*{path}");

        IHttpResponse response = GET("/one/three/four").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void startOrDollarSignAreInterchnageableInTheNewPath() throws Exception {

        getRouter().GET("/two/four/five/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/*{path}").to("/two/${path}/*{param1}");

        IHttpResponse response = GET("/one/three/four/five").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void startOrDollarSignAreInterchnageableInTheNewPathNotFound() throws Exception {

        getRouter().GET("/two/four/five/nope").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/*{path}").to("/two/${path}/*{param1}");

        IHttpResponse response = GET("/one/three/four/five").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dynParamsCanBeUsedAnywhereInTheNewPath() throws Exception {

        getRouter().GET("/aaabbbccc/dddeee/fff").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/${param2}/*{path}").to("/aaa${param1}ccc/${param2}eee/*{path}");

        IHttpResponse response = GET("/one/bbb/ddd/fff").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void nullNewPathLeadsToRoot() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one").to(null);

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void emptyNewPathLeadsToRoot() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one").to("");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void postIsNotRedirectedButWeReceive301Default() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        IHttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
    }

    @Test
    public void postIsNotRedirectedButWeReceive301() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").permanently().to("/two");

        IHttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
    }

    @Test
    public void postIsNotRedirectedButWeReceive302() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").temporarily().to("/two");

        IHttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
    }

    @Test
    public void webSocketRedirect() throws Exception {

        DefaultWebsocketControllerTest controller1 = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerConnected(IDefaultWebsocketContext context) {
                fail();
            }
        };
        getRouter().websocket("/one").save(controller1);

        DefaultWebsocketControllerTest controller2 = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerConnected(IDefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/two").save(controller2);

        getRouter().redirect("/one").to("/two");

        WebsocketClientTest client = new WebsocketClientTest();

        IWebsocketClientWriter writer = websocket("/one").connect(client);
        assertNotNull(writer);

        assertTrue(controller2.waitNrbPeerConnected("endpoint1", 1));
        assertFalse(controller1.isEndpointOpen("endpoint1"));

        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("ok!", client.getStringMessageReceived().get(0));
    }

    @Test
    public void webSocketMaxNbrRedirects() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerConnected(IDefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/final").save(controller);

        getRouter().redirect("/one").to("/two");
        getRouter().redirect("/two").to("/three");
        getRouter().redirect("/three").to("/four");
        getRouter().redirect("/four").to("/five");
        getRouter().redirect("/five").to("/final");

        WebsocketClientTest client = new WebsocketClientTest();

        IWebsocketClientWriter writer = websocket("/one").connect(client);
        assertNotNull(writer);

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("ok!", client.getStringMessageReceived().get(0));
    }

    @Test
    public void webSocketTooManyRedirects() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerConnected(IDefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/final").save(controller);

        getRouter().redirect("/one").to("/two");
        getRouter().redirect("/two").to("/three");
        getRouter().redirect("/three").to("/four");
        getRouter().redirect("/four").to("/five");
        getRouter().redirect("/five").to("/six");
        getRouter().redirect("/six").to("/final");

        WebsocketClientTest client = new WebsocketClientTest();

        try {
            websocket("/one").connect(client);
            fail();
        } catch(Exception ex) {
            System.out.println();
        }
    }

}

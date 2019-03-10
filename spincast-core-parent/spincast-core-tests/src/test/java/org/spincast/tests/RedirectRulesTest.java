package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.WebsocketEndpointManager;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class RedirectRulesTest extends NoAppStartHttpServerTestingBase {

    @Override
    public void beforeTest() {
        super.beforeTest();

        //==========================================
        // For the WebSocket tests
        //==========================================
        List<WebsocketEndpointManager> websocketEndpointManagers = getServer().getWebsocketEndpointManagers();
        for (WebsocketEndpointManager manager : websocketEndpointManagers) {
            manager.closeEndpoint();
        }
        assertTrue(SpincastTestingUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getServer().getWebsocketEndpointManagers().size() == 0;
            }
        }));
    }

    @Test
    public void redirectDefault() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectPermanently() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").permanently().to("/two");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectTemporarily() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").temporarily().to("/two");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/nope");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void fullUrl() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        String fullUrl = createTestUrl("/two");
        getRouter().redirect("/one").to(fullUrl);

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void oldRouteNotFound() throws Exception {

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void dynParams() throws Exception {

        getRouter().GET("/two/three/four").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/${param2}").to("/two/${param1}/${param2}");

        HttpResponse response = GET("/one/three/four").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void splatParam() throws Exception {

        getRouter().GET("/two/three/four").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/*{path}").to("/two/*{path}");

        HttpResponse response = GET("/one/three/four").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void startOrDollarSignAreInterchnageableInTheNewPath() throws Exception {

        getRouter().GET("/two/four/five/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/*{path}").to("/two/${path}/*{param1}");

        HttpResponse response = GET("/one/three/four/five").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void startOrDollarSignAreInterchnageableInTheNewPathNotFound() throws Exception {

        getRouter().GET("/two/four/five/nope").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/*{path}").to("/two/${path}/*{param1}");

        HttpResponse response = GET("/one/three/four/five").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dynParamsCanBeUsedAnywhereInTheNewPath() throws Exception {

        getRouter().GET("/aaabbbccc/dddeee/fff").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one/${param1}/${param2}/*{path}").to("/aaa${param1}ccc/${param2}eee/*{path}");

        HttpResponse response = GET("/one/bbb/ddd/fff").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void nullNewPathLeadsToRoot() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one").to(null);

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void emptyNewPathLeadsToRoot() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok!");
            }
        });

        getRouter().redirect("/one").to("");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok!", response.getContentAsString());
    }

    @Test
    public void postIsNotRedirectedButWeReceive301Default() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").to("/two");

        HttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
    }

    @Test
    public void postIsNotRedirectedButWeReceive301() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").permanently().to("/two");

        HttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
    }

    @Test
    public void postIsNotRedirectedButWeReceive302() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        getRouter().POST("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().redirect("/one").temporarily().to("/two");

        HttpResponse response = POST("/one").send();

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
    }

    /*
     
     TODO
     There is currently a bug with the Websocket client provided by Undertow:
     https://issues.jboss.org/browse/UNDERTOW-1471
     
     Redirection tests will only work when this is fixed.
     
    @Test
    public void webSocketRedirect() throws Exception {
    
        DefaultWebsocketControllerTest controller1 = new DefaultWebsocketControllerTest(getServer()) {
    
            @Override
            public void onPeerConnected(DefaultWebsocketContext context) {
                fail();
            }
        };
        getRouter().websocket("/one").handle(controller1);
    
        DefaultWebsocketControllerTest controller2 = new DefaultWebsocketControllerTest(getServer()) {
    
            @Override
            public void onPeerConnected(DefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/two").handle(controller2);
    
        getRouter().redirect("/one").to("/two");
    
        WebsocketClientTest client = new WebsocketClientTest();
    
        WebsocketClientWriter writer = websocket("/one").disableSslCertificateErrors().connect(client);
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
            public void onPeerConnected(DefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/final").handle(controller);
    
        getRouter().redirect("/one").to("/two");
        getRouter().redirect("/two").to("/three");
        getRouter().redirect("/three").to("/four");
        getRouter().redirect("/four").to("/five");
        getRouter().redirect("/five").to("/final");
    
        WebsocketClientTest client = new WebsocketClientTest();
    
        WebsocketClientWriter writer = websocket("/one").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
    
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));
    
        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("ok!", client.getStringMessageReceived().get(0));
    }
    
    @Test
    public void webSocketTooManyRedirects() throws Exception {
    
        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {
    
            @Override
            public void onPeerConnected(DefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/final").handle(controller);
    
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
        } catch (Exception ex) {
            System.out.println();
        }
    }
    */

}

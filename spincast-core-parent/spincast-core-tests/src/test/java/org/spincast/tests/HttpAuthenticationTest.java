package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class HttpAuthenticationTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected void clearRoutes() {
        // We don't clear routes...
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        assertEquals(0, getServer().getHttpAuthenticationRealms().size());

        getServer().addHttpAuthentication("testRealm", "user1", "pass1");
        getServer().addHttpAuthentication("testRealm", "user2", "pass2");

        assertEquals(0, getServer().getHttpAuthenticationRealms().size());

        getRouter().httpAuth("/one", "testRealm");

        assertEquals(1, getServer().getHttpAuthenticationRealms().size());
        String path = getServer().getHttpAuthenticationRealms().get("testRealm");
        assertNotNull(path);
        assertEquals("/one", path);

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });
    }

    @Test
    public void notProtected() throws Exception {

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void noAuthSent() throws Exception {

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void authSentValid() throws Exception {

        HttpResponse response = GET("/one").setHttpAuthCredentials("user1", "pass1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("one", response.getContentAsString());
    }

    @Test
    public void authSentValid2() throws Exception {

        HttpResponse response = GET("/one").setHttpAuthCredentials("user2", "pass2").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("one", response.getContentAsString());
    }

    @Test
    public void authSentInvalid() throws Exception {

        HttpResponse response = GET("/one").setHttpAuthCredentials("user2", "passxxx").send();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void authSentInvalid2() throws Exception {

        HttpResponse response = GET("/one").setHttpAuthCredentials("user3", "pass2").send();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

}

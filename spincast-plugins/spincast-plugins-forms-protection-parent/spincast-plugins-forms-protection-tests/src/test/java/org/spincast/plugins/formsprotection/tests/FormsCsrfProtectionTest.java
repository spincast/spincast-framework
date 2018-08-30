package org.spincast.plugins.formsprotection.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.plugins.formsprotection.csrf.SpincastCsrfToken;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class FormsCsrfProtectionTest extends FormsProtectionTestBase {

    //==========================================
    // The session filter is required for the CSRF
    // protection!
    //==========================================
    @Override
    protected boolean isAddSessionFilter() {
        return true;
    }

    @Override
    protected boolean isAddCsrfProtectionFilter() {
        return true;
    }

    @Test
    public void get() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void noCsrfToken() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("k1", "v1")
                                         .addFormBodyFieldValue("k2", "v2")
                                         .send();

        //==========================================
        // Redirected to main page
        //==========================================
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }


    @Test
    public void validCsrfTokenNoOriginNoReferer() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   csrfToken)
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void validCsrfTokenSameOrigin() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addHeaderValue("origin", getSpincastConfig().getPublicUrlBase())
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   csrfToken)
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void validCsrfTokenSameReferer() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addHeaderValue("referer", getSpincastConfig().getPublicUrlBase())
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   csrfToken)
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void invalidCsrfToken() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addHeaderValue("origin", getSpincastConfig().getPublicUrlBase())
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   "nope")
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void validCsrfTokenInvalidOrigin() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addHeaderValue("origin", "http://nope.com")
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   csrfToken)
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void validCsrfTokenInvalidReferer() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .setCookies(getPreviousResponseCookies())
                            .addHeaderValue("referer", "http://nope.com")
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                   csrfToken)
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void csrfTokenAndSessionCreation() throws Exception {

        savedSessionCalledNbr = 0;

        //==========================================
        // No CSRF token in the session :
        // submitting a form is invalid!
        //==========================================
        getRouter().POST("/aaa").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);

                context.response().sendPlainText(session.getId());
            }
        });

        HttpResponse response = POST("/aaa").addFormBodyFieldValue("k1", "v1")
                                            .addFormBodyFieldValue("k2", "v2")
                                            .addHeaderValue("origin", getSpincastConfig().getPublicUrlBase())
                                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        saveResponseCookies(response);

        //==========================================
        // Session not created!
        //==========================================
        Cookie sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNull(sessionCookie);
        assertEquals(0, savedSessionCalledNbr);

        //==========================================
        // Creating a CSRF
        //==========================================
        getRouter().GET("/bbb").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);

                //==========================================
                // We call getCurrentCsrfToken()!
                //==========================================
                SpincastCsrfToken csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken();
                assertNotNull(csrfToken);

                context.response().sendPlainText(csrfToken.getId());
            }
        });

        response = GET("/bbb").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        //==========================================
        // Session saved...
        //==========================================
        sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);
        assertEquals(1, savedSessionCalledNbr);

        String csrfToken = response.getContentAsString();
        assertNotNull(csrfToken);

        //==========================================
        // Submitting a form with a CSRF won't save the
        // session.
        //==========================================
        getRouter().POST("/ccc").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);

                String csrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken().getId();
                context.response().sendPlainText(csrfToken);
            }
        });

        response = POST("/ccc").addFormBodyFieldValue("k1", "v1")
                               .addFormBodyFieldValue("k2", "v2")
                               .setCookies(getPreviousResponseCookies())
                               .addHeaderValue("origin", getSpincastConfig().getPublicUrlBase())
                               .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                      csrfToken)
                               .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());

        //==========================================
        // Session not saved
        //==========================================
        sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);
        assertEquals(1, savedSessionCalledNbr); // still one

        //==========================================
        // Delete sessions
        //==========================================
        savedSessions.clear();

        //==========================================
        // Submitting a form with a CSRF but not found
        // in the session will clear the session on the
        // user, but won't save any new one.
        //==========================================
        getRouter().POST("/ddd").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);
                context.response().sendPlainText(session.getId());
            }
        });

        response = POST("/ddd").addFormBodyFieldValue("k1", "v1")
                               .addFormBodyFieldValue("k2", "v2")
                               .setCookies(getPreviousResponseCookies())
                               .addHeaderValue("origin", getSpincastConfig().getPublicUrlBase())
                               .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName(),
                                                      csrfToken)
                               .send();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());

        //==========================================
        // Session not saved!
        //==========================================
        assertEquals(1, savedSessionCalledNbr); // still one

        //==========================================
        // But the session cookie still exists
        // because an exception occured so the
        // session "after" filter was not ran.
        //==========================================
        sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);

        //==========================================
        // Calling with an existing session id
        // but the session has been cleared =>
        // delete the cookie.
        //==========================================
        getRouter().GET("/eee").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);
                context.response().sendPlainText(session.getId());
            }
        });

        response = GET("/eee").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        //==========================================
        // Session cleared and not saved!
        //==========================================
        sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNull(sessionCookie);
        assertEquals(1, savedSessionCalledNbr); // still one

        //==========================================
        // Set a new session cookie
        //==========================================
        getRouter().GET("/fff").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);
                session.getAttributes().set("titi", "toto");
                context.response().sendPlainText(session.getId());
            }
        });

        response = GET("/fff").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);
        assertEquals(response.getContentAsString(), sessionCookie.getValue());
        assertEquals(2, savedSessionCalledNbr); // incremented

        //==========================================
        // Delete sessions
        //==========================================
        savedSessions.clear();

        //==========================================
        // Calling with an existing session id
        // but the session has been cleared:
        // delete the cookie, but a new session is
        // created and saved, so the session cookie 
        // is recreated!
        //==========================================
        getRouter().GET("/ggg").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                SpincastSession session = getSpincastSessionManager().getCurrentSession();
                assertNotNull(session);
                session.getAttributes().set("titi222", "toto222");
                context.response().sendPlainText(session.getId());
            }
        });

        response = GET("/ggg").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        //==========================================
        // Session cleared and not saved!
        //==========================================
        Cookie sessionCookie2 = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie2);
        assertNotNull(sessionCookie2.getValue());
        assertNotEquals(sessionCookie2.getValue(), sessionCookie.getValue());
        assertEquals(3, savedSessionCalledNbr); // incremented
    }

}

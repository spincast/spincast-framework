package org.spincast.plugins.session.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.shaded.org.apache.http.HttpStatus;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SessionTest extends CustomRepoTestBase {

    protected static String mySessionId;

    @Test
    public void t00_startNoSession() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);
                assertFalse(currentSession.isDirty());
                assertFalse(currentSession.isInvalidated());

                String sessionId = currentSession.getId();
                assertNotNull(sessionId);

                context.response().sendPlainText(sessionId);
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        mySessionId = response.getContentAsString();
        assertNotNull(mySessionId);

        //==========================================
        // No Session cookie, because nothing was added to the session!
        //==========================================
        Cookie sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNull(sessionCookie);

        //==========================================
        // The session was not saved
        //==========================================
        assertEquals(0, savedSession.size());
    }

    @Test
    public void t01_startNoSessionAttributesAdded() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);

                String sessionId = currentSession.getId();
                assertNotNull(currentSession);

                currentSession.getAttributes().set("kiki", "koko");

                context.response().sendPlainText(sessionId);
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        mySessionId = response.getContentAsString();
        assertNotNull(mySessionId);

        // Session cookie
        Cookie sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);

        String sessionCookieValue = sessionCookie.getValue();
        assertNotNull(sessionCookieValue);
        assertEquals(mySessionId, sessionCookieValue);

        //==========================================
        // The session was saved
        //==========================================
        assertEquals(1, savedSession.size());
    }

    @Test
    public void t02_sessionKept() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);

                String sessionId = currentSession.getId();
                assertNotNull(sessionId);
                assertEquals(mySessionId, sessionId);

                String sessionCookieValue = context.request().getCookieValue(getSpincastSessionConfig().getSessionIdCookieName());
                assertNotNull(sessionCookieValue);
                assertNotNull(sessionCookieValue);
                assertEquals(mySessionId, sessionCookieValue);

                String attr = currentSession.getAttributes().getString("kiki");
                assertNotNull(attr);
                assertEquals("koko", attr);

                context.response().sendPlainText(sessionId);
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        String sessionId = response.getContentAsString();
        assertNotNull(sessionId);
        assertEquals(mySessionId, sessionId);

        // Session cookie
        Cookie sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);

        String sessionCookieValue = sessionCookie.getValue();
        assertNotNull(sessionCookieValue);
        assertEquals(sessionId, sessionCookieValue);
    }

    @Test
    public void t03_add_attributes() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);

                int userId = 12345;
                currentSession.getAttributes().set("userId", userId);

                JsonObject info = context.json().create();
                info.set("k1", "v1");
                info.set("k2", "v2");
                currentSession.getAttributes().set("info", info);

                context.response().sendPlainText(currentSession.getId());
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
    }

    @Test
    public void t04_retrieve_attributes() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);

                Integer userId = currentSession.getAttributes().getInteger("userId");
                assertNotNull(userId);
                assertEquals(Integer.valueOf(12345), userId);

                JsonObject infoSet = context.json().create();
                infoSet.set("k1", "v1");
                infoSet.set("k2", "v2");

                JsonObject info = currentSession.getAttributes().getJsonObject("info");
                assertNotNull(info);
                assertTrue(info.isEquivalentTo(infoSet));

                context.response().sendPlainText(currentSession.getId());
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
    }

    @Test
    public void t05_dates() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    SpincastSession currentSession = getSessionManager().getCurrentSession();
                    currentSession.getCreationDate().isBefore(Instant.now());
                    assertEquals(currentSession.getCreationDate().truncatedTo(ChronoUnit.SECONDS),
                                 currentSession.getModificationDate().truncatedTo(ChronoUnit.SECONDS));

                    currentSession.getAttributes().set("toti", "123");

                    context.response().sendPlainText(currentSession.getId());
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/") /* .addCookies(getPreviousResponseCookies() )*/ .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
    }

    @Test
    public void t07_isDirty() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    SpincastSession currentSession = getSessionManager().getCurrentSession();
                    assertFalse(currentSession.isDirty());
                    currentSession.getAttributes().set("toti", "555555555555555");
                    assertTrue(currentSession.isDirty());

                    context.response().sendPlainText(currentSession.getAttributes().getString("toti"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        assertEquals("555555555555555", response.getContentAsString());
    }

    @Test
    public void t09_invalidate() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    SpincastSession currentSession = getSessionManager().getCurrentSession();
                    assertFalse(currentSession.isInvalidated());

                    currentSession.invalidate();
                    assertTrue(currentSession.isInvalidated());

                    context.response().sendPlainText("ok");
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        Cookie cookie = getPreviousResponseCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(cookie);

        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        cookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNull(cookie);
    }

    @Test
    public void t10_newSessionDirtyForced() throws Exception {

        //==========================================
        // Reset sessions
        //==========================================
        savedSession.clear();

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                SpincastSession currentSession = getSessionManager().getCurrentSession();
                assertNotNull(currentSession);

                String sessionId = currentSession.getId();
                assertNotNull(currentSession);

                // Force dirty
                currentSession.setDirty();

                context.response().sendPlainText(sessionId);
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);

        // Session cookie
        Cookie sessionCookie = response.getCookie(getSpincastSessionConfig().getSessionIdCookieName());
        assertNotNull(sessionCookie);

        //==========================================
        // The session was saved
        //==========================================
        assertEquals(1, savedSession.size());
    }

}

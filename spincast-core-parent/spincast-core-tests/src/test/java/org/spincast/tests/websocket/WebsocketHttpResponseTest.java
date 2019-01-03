package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.defaults.testing.NoAppWebsocketTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.DefaultHandler;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

/**
 * Test HTTP responses from the server for Websocket upgrade
 * requests but without the upgrade to actually be made.
 */
public class WebsocketHttpResponseTest extends NoAppWebsocketTestingBase {

    int[] nbr = new int[]{0};

    @Test
    public void simpleWebsocketUpgradeRequestSuccess() throws Exception {

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {
                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        WebsocketHttpResponseTest.this.nbr[0]++;
                        return "endpoint" + WebsocketHttpResponseTest.this.nbr[0];
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }

                };
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().send();
        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void upgradeRefusedBecauseOfCookie() throws Exception {

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("username");
                assertNotNull(cookie);

                if (!("Stromgol".equals(cookie))) {
                    context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                    context.response().sendPlainText("Websocket upgrade not allowed.");
                    return null;
                }

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }

                };
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "nope", false).send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeRefusedByBeforeFilterInline() throws Exception {

        DefaultHandler beforeFilter = new DefaultHandler() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("username");
                assertNotNull(cookie);

                if (!("Stromgol".equals(cookie))) {
                    throw new PublicExceptionDefault("Websocket upgrade not allowed.", HttpStatus.SC_FORBIDDEN);
                }
            }
        };

        getRouter().websocket("/ws").before(beforeFilter).handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }
                };
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "nope", false).send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeRefusedByBeforeFilterGlobal() throws Exception {

        DefaultHandler beforeFilter = new DefaultHandler() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("username");
                assertNotNull(cookie);

                if (!("Stromgol".equals(cookie))) {
                    throw new PublicExceptionDefault("Websocket upgrade not allowed.", HttpStatus.SC_FORBIDDEN);
                }
            }
        };

        getRouter().ALL("/*{path}").pos(-10).handle(beforeFilter);

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }
                };
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "nope", false).send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeAcceptedBecauseOfValidCookie() throws Exception {

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("username");
                assertNotNull(cookie);

                if (!("Stromgol".equals(cookie))) {
                    context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                    context.response().sendPlainText("Websocket upgrade not allowed.");
                    return null;
                }

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }
                };
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "Stromgol", false).send();
        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void afterFiltersAreNotAppliedConnectionAllowed() throws Exception {

        DefaultHandler afterFilter = new DefaultHandler() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        };
        getRouter().ALL("/*{path}").pos(10).handle(afterFilter);

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()));

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "nope").send();
        assertNotNull(response);

        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void afterFiltersAreNotAppliedConnectionRefused() throws Exception {

        final boolean[] filterCalled = new boolean[]{false};
        final boolean[] onPeerPreConnectCalled = new boolean[]{false};

        DefaultHandler afterFilter = new DefaultHandler() {

            @Override
            public void handle(DefaultRequestContext context) {
                filterCalled[0] = true;
            }
        };
        getRouter().ALL("/*{path}").pos(10).handle(afterFilter);

        getRouter().websocket("/ws").handle(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {
                onPeerPreConnectCalled[0] = true;
                return null;
            }
        });

        String path = "/ws";
        HttpResponse response = websocket(path).disableSslCertificateErrors().setCookie("username", "nope").send();
        assertNotNull(response);

        validateIsNotWebsocketUpgradeHttpResponse(response);

        assertTrue(onPeerPreConnectCalled[0]);
        assertFalse(filterCalled[0]);

    }

}

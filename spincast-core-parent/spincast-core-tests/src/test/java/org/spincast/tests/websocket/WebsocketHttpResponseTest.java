package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IWebsocketConnectionConfig;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.routing.IDefaultHandler;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

/**
 * Test HTTP responses from the server for Websocket upgrade
 * requests but without the upgrade to actually be made.
 */
public class WebsocketHttpResponseTest extends SpincastDefaultWebsocketNoAppIntegrationTestBase {

    int[] nbr = new int[]{0};

    @Test
    public void simpleWebsocketUpgradeRequestSuccess() throws Exception {

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {
                return new IWebsocketConnectionConfig() {

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
        IHttpResponse response = websocket(path).send();
        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void upgradeRefusedBecauseOfCookie() throws Exception {

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("username");
                assertNotNull(cookie);

                if(!("Stromgol".equals(cookie.getValue()))) {
                    context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                    context.response().sendPlainText("Websocket upgrade not allowed.");
                    return null;
                }

                return new IWebsocketConnectionConfig() {

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
        IHttpResponse response = websocket(path).addCookie("username", "nope").send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeRefusedByBeforeFilterInline() throws Exception {

        IDefaultHandler beforeFilter = new IDefaultHandler() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("username");
                assertNotNull(cookie);

                if(!("Stromgol".equals(cookie.getValue()))) {
                    throw new PublicException("Websocket upgrade not allowed.", HttpStatus.SC_FORBIDDEN);
                }
            }
        };

        getRouter().websocket("/ws").before(beforeFilter).save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

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
        IHttpResponse response = websocket(path).addCookie("username", "nope").send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeRefusedByBeforeFilterGlobal() throws Exception {

        IDefaultHandler beforeFilter = new IDefaultHandler() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("username");
                assertNotNull(cookie);

                if(!("Stromgol".equals(cookie.getValue()))) {
                    throw new PublicException("Websocket upgrade not allowed.", HttpStatus.SC_FORBIDDEN);
                }
            }
        };

        getRouter().before("/*{path}").save(beforeFilter);

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

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
        IHttpResponse response = websocket(path).addCookie("username", "nope").send();
        assertNotNull(response);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Websocket upgrade not allowed.", response.getContentAsString());

        validateIsNotWebsocketUpgradeHttpResponse(response);
    }

    @Test
    public void upgradeAcceptedBecauseOfValidCookie() throws Exception {

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("username");
                assertNotNull(cookie);

                if(!("Stromgol".equals(cookie.getValue()))) {
                    context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                    context.response().sendPlainText("Websocket upgrade not allowed.");
                    return null;
                }

                return new IWebsocketConnectionConfig() {

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
        IHttpResponse response = websocket(path).addCookie("username", "Stromgol").send();
        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void afterFiltersAreNotAppliedConnectionAllowed() throws Exception {

        IDefaultHandler afterFilter = new IDefaultHandler() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        };
        getRouter().after("/*{path}").save(afterFilter);

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()));

        String path = "/ws";
        IHttpResponse response = websocket(path).addCookie("username", "nope").send();
        assertNotNull(response);

        validateIsWebsocketUpgradeHttpResponse(path, response);
    }

    @Test
    public void afterFiltersAreNotAppliedConnectionRefused() throws Exception {

        final boolean[] filterCalled = new boolean[]{false};
        final boolean[] onPeerPreConnectCalled = new boolean[]{false};

        IDefaultHandler afterFilter = new IDefaultHandler() {

            @Override
            public void handle(IDefaultRequestContext context) {
                filterCalled[0] = true;
            }
        };
        getRouter().after("/*{path}").save(afterFilter);

        getRouter().websocket("/ws").save(new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {
                onPeerPreConnectCalled[0] = true;
                return null;
            }
        });

        String path = "/ws";
        IHttpResponse response = websocket(path).addCookie("username", "nope").send();
        assertNotNull(response);

        validateIsNotWebsocketUpgradeHttpResponse(response);

        assertTrue(onPeerPreConnectCalled[0]);
        assertFalse(filterCalled[0]);

    }

}

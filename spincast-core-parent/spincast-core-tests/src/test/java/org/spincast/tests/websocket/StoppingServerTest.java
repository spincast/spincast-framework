package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.defaults.testing.NoAppWebsocketTestingBase;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

/**
 * We currently have to ignore this class because of this issue :
 * http://lists.jboss.org/pipermail/undertow-dev/2016-August/001657.html
 * This is currently not fixed on branch 1.3.X (Java 7 compatible).
 * 
 * The issue occures when stopping the server.
 * 
 * This test class can be run manually and tests should pass, if the
 * issue doesn't occure.
 */
@Ignore
public class StoppingServerTest extends NoAppWebsocketTestingBase {

    @Override
    protected void startServer() {
        // nothing
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        getServer().start();
    }

    //==========================================
    // We will stop the server by ourself so we can
    // manage if a "closing" message is sent to the
    // peers.
    //==========================================
    @Override
    protected void stopServer() {
        // nothing
    }

    @Test
    public void multipleEndpointsAndPeers() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").save(controller);

        Set<WebsocketClientTest> clients = new HashSet<WebsocketClientTest>();

        int endpointNbr = 5;
        for (int endpointPos = 1; endpointPos <= endpointNbr; endpointPos++) {

            String endpointName = "endpoint" + endpointPos;
            endpointIdToUse[0] = endpointName;
            int peerNbr = 20;
            for (int i = 1; i <= peerNbr; i++) {
                peerIdToUse[0] = "peer" + i;

                WebsocketClientTest client = new WebsocketClientTest();
                clients.add(client);
                WebsocketClientWriter writer = websocket("/ws").ping(-1).connect(client);
                assertNotNull(writer);
            }
            assertTrue(controller.isEndpointOpen(endpointName));
            assertTrue(controller.waitNrbPeerConnected(endpointName, peerNbr));
        }

        //==========================================
        // true => send a "closing" message to the peers!
        //==========================================
        getServer().stop(true);

        for (WebsocketClientTest client : clients) {
            assertTrue(client.waitForConnectionClosed());
            assertEquals(1, client.getConnectionClosedEvents().size());
        }

        assertTrue(SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return !getServer().isRunning();
            }
        }));
    }

    @Test
    public void noClosingMessages() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").save(controller);

        Set<WebsocketClientTest> clients = new HashSet<WebsocketClientTest>();

        int endpointNbr = 5;
        for (int endpointPos = 1; endpointPos <= endpointNbr; endpointPos++) {

            String endpointName = "endpoint" + endpointPos;
            endpointIdToUse[0] = endpointName;
            int peerNbr = 20;
            for (int i = 1; i <= peerNbr; i++) {
                peerIdToUse[0] = "peer" + i;

                WebsocketClientTest client = new WebsocketClientTest();
                clients.add(client);
                WebsocketClientWriter writer = websocket("/ws").ping(-1).connect(client);
                assertNotNull(writer);
            }
            assertTrue(controller.isEndpointOpen(endpointName));
            assertTrue(controller.waitNrbPeerConnected(endpointName, peerNbr));
        }

        //==========================================
        // false => no "closing" message sent to the peers!
        //==========================================
        getServer().stop(false);

        assertTrue(SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return !getServer().isRunning();
            }
        }));

        for (WebsocketClientTest client : clients) {
            assertEquals(0, client.getConnectionClosedEvents().size());
        }
    }

}

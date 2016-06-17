package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.net.ssl.SSLHandshakeException;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

import com.google.inject.Module;

public class SSLTest extends DefaultWebsocketTestBase {

    /**
     * Custom Test config to start the server on HTTPS.
     */
    protected static class HttpsTestConfig extends SpincastTestConfig {

        private int httpsServerPort = -1;

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if(this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestUtils.findFreePort();
            }
            return this.httpsServerPort;
        }

        @Override
        public String getHttpsKeyStorePath() {
            return "/self-signed-certificate.jks";
        }

        @Override
        public String getHttpsKeyStoreType() {
            return "JKS";
        }

        @Override
        public String getHttpsKeyStoreStorePass() {
            return "myStorePass";
        }

        @Override
        public String getHttpsKeyStoreKeypass() {
            return "myKeyPass";
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return HttpsTestConfig.class;
            }
        };
    }

    @Test
    public void httpsAndWebsocketSsl() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(IDefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        IWebsocketClientWriter writer = websocket("/ws", false, true).disableSslCertificateErrors().connect(client);
        assertNotNull(writer);

        assertTrue(controller.isEndpointOpen("endpoint1"));

        writer.sendMessage("httpsAndWebsocketSsl");
        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));
        assertEquals("httpsAndWebsocketSsl", controller.getStringMessageReceived("endpoint1").get(0));

        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("Pong httpsAndWebsocketSsl", client.getStringMessageReceived().get(0));
        assertTrue(client.isConnectionOpen());
    }

    @Test
    public void selfSignedCertificateNotAccepted() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(IDefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        //==========================================
        // We do not disable SSL errors here...
        //==========================================
        try {
            @SuppressWarnings("unused")
            IWebsocketClientWriter writer = websocket("/ws", false, true).connect(client);
            fail();
        } catch(Exception ex) {
            Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof SSLHandshakeException);
        }
    }

}

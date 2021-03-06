package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.net.ssl.SSLHandshakeException;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppWebsocketTestingBase;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

import com.google.inject.Inject;

public class SSLTest extends NoAppWebsocketTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplClass() {
        return HttpsTestConfig.class;
    }

    /**
     * Custom Test config to start the server on HTTPS.
     */
    protected static class HttpsTestConfig extends SpincastConfigTestingDefault {

        private int httpsServerPort = -1;

        /**
         * Constructor
         */
        @Inject
        protected HttpsTestConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if (this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestingUtils.findFreePort();
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
        public String getHttpsKeyStoreKeyPass() {
            return "myKeyPass";
        }
    }

    @Test
    public void httpsAndWebsocketSsl() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws", false, true).disableSslCertificateErrors().connect(client);
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
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        //==========================================
        // We do not disable SSL errors here...
        //==========================================
        try {
            @SuppressWarnings("unused")
            WebsocketClientWriter writer = websocket("/ws", false, true).connect(client);
            fail();
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof SSLHandshakeException);
        }
    }

}

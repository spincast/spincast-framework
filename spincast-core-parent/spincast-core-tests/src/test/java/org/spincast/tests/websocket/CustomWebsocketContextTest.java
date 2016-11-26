package org.spincast.tests.websocket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;
import org.spincast.core.websocket.WebsocketPeerManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.WebsocketControllerTestBase;
import org.spincast.tests.websocket.CustomWebsocketContextTest.AppWebsocketContext;

import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomWebsocketContextTest extends
                                        SpincastWebsocketNoAppIntegrationTestBase<DefaultRequestContext, AppWebsocketContext> {

    public static interface AppWebsocketContext extends WebsocketContext<AppWebsocketContext> {

        public void customMethod(String message);
    }

    public static class AppWebsocketContextDefault extends WebsocketContextBase<AppWebsocketContext>
                                                   implements AppWebsocketContext {

        @AssistedInject
        public AppWebsocketContextDefault(@Assisted("endpointId") String endpointId,
                                          @Assisted("peerId") String peerId,
                                          @Assisted WebsocketPeerManager peerManager,
                                          WebsocketContextBaseDeps<AppWebsocketContext> deps) {
            super(endpointId,
                  peerId,
                  peerManager,
                  deps);
        }

        @Override
        public void customMethod(String message) {
            sendMessageToCurrentPeer("customMethod: " + message);
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
                return AppWebsocketContextDefault.class;
            }
        };
    }

    @Test
    public void testCustomMethod() throws Exception {

        WebsocketControllerTestBase<DefaultRequestContext, AppWebsocketContext> controller =
                new WebsocketControllerTestBase<DefaultRequestContext, AppWebsocketContext>(getServer()) {

                    @Override
                    public void onPeerMessage(AppWebsocketContext context, String message) {

                        super.onPeerMessage(context, message);

                        //==========================================
                        // Uses our custom method here.
                        //==========================================
                        context.customMethod(message);
                    }
                };

        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();
        WebsocketClientWriter writer = websocket("/ws").connect(client);
        assertNotNull(writer);
        assertTrue(client.isConnectionOpen());

        assertTrue(controller.isEndpointOpen("endpoint1"));

        writer.sendMessage(SpincastTestUtils.TEST_STRING);
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", SpincastTestUtils.TEST_STRING));

        //==========================================
        // Ous custom method echoes the message to
        // the peer.
        //==========================================
        assertTrue(client.waitForStringMessageReceived("customMethod: " + SpincastTestUtils.TEST_STRING));

    }

}

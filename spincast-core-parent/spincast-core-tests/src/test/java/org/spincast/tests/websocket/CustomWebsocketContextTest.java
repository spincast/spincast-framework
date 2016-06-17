package org.spincast.tests.websocket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketPeerManager;
import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.WebsocketControllerTestBase;
import org.spincast.tests.websocket.CustomWebsocketContextTest.IAppWebsocketContext;

import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomWebsocketContextTest extends WebsocketTestBase<IDefaultRequestContext, IAppWebsocketContext> {

    public static interface IAppWebsocketContext extends IWebsocketContext<IAppWebsocketContext> {

        public void customMethod(String message);
    }

    public static class AppWebsocketContext extends WebsocketContextBase<IAppWebsocketContext>
                                            implements IAppWebsocketContext {

        @AssistedInject
        public AppWebsocketContext(@Assisted("endpointId") String endpointId,
                                   @Assisted("peerId") String peerId,
                                   @Assisted IWebsocketPeerManager peerManager,
                                   WebsocketContextBaseDeps<IAppWebsocketContext> deps) {
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
        return new DefaultTestingModule() {

            @Override
            protected Class<? extends IWebsocketContext<?>> getWebsocketContextImplementationClass() {
                return AppWebsocketContext.class;
            }
        };
    }

    @Test
    public void testCustomMethod() throws Exception {

        WebsocketControllerTestBase<IDefaultRequestContext, IAppWebsocketContext> controller =
                new WebsocketControllerTestBase<IDefaultRequestContext, IAppWebsocketContext>(getServer()) {

                    @Override
                    public void onPeerMessage(IAppWebsocketContext context, String message) {

                        super.onPeerMessage(context, message);

                        //==========================================
                        // Uses our custom method here.
                        //==========================================
                        context.customMethod(message);
                    }
                };

        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();
        IWebsocketClientWriter writer = websocket("/ws").connect(client);
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

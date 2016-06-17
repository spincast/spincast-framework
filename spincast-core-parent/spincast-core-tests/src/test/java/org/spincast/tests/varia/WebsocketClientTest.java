package org.spincast.tests.varia;

import java.util.ArrayList;
import java.util.List;

import org.spincast.plugins.httpclient.websocket.IWebsocketClientHandler;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;

public class WebsocketClientTest implements IWebsocketClientHandler {

    private final List<String> stringMessageReceived = new ArrayList<String>();
    private final List<byte[]> bytesMessageReceived = new ArrayList<byte[]>();
    private final List<Pair<Integer, String>> connectionClosedEvents = new ArrayList<Pair<Integer, String>>();

    @Override
    public void onEndpointMessage(byte[] message) {
        this.bytesMessageReceived.add(message);
    }

    @Override
    public void onEndpointMessage(String message) {
        this.stringMessageReceived.add(message);
    }

    @Override
    public void onConnectionClosed(int code, String reason) {
        Pair<Integer, String> closedEvent = Pair.of(code, reason);
        this.connectionClosedEvents.add(closedEvent);
    }

    public List<String> getStringMessageReceived() {
        return this.stringMessageReceived;
    }

    public List<byte[]> getBytesMessageReceived() {
        return this.bytesMessageReceived;
    }

    public List<Pair<Integer, String>> getConnectionClosedEvents() {
        return this.connectionClosedEvents;
    }

    public boolean isConnectionOpen() {
        return getConnectionClosedEvents().size() == 0;
    }

    public boolean hasReceivedStringMessage(String messageExpected) {
        for(int i = this.stringMessageReceived.size() - 1; i >= 0; i--) {
            if(messageExpected.equals(this.stringMessageReceived.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean waitForStringMessageReceived(int nbrExpected) {
        return waitForStringMessageReceived(nbrExpected, 5000);
    }

    public boolean waitForStringMessageReceived(final int nbrExpected, int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return WebsocketClientTest.this.stringMessageReceived.size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForStringMessageReceived(String messageExpected) {
        return waitForStringMessageReceived(messageExpected, 5000);
    }

    public boolean waitForStringMessageReceived(final String messageExpected, int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {

                if(WebsocketClientTest.this.stringMessageReceived.size() == 0) {
                    return false;
                }
                for(int i = WebsocketClientTest.this.stringMessageReceived.size() - 1; i >= 0; i--) {
                    if(messageExpected.equals(WebsocketClientTest.this.stringMessageReceived.get(i))) {
                        return true;
                    }
                }
                return false;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForBytesMessageReceived(int nbrExpected) {
        return waitForBytesMessageReceived(nbrExpected, 5000);
    }

    public boolean waitForBytesMessageReceived(final int nbrExpected, int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return WebsocketClientTest.this.bytesMessageReceived.size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForConnectionClosed() {
        return waitForConnectionClosed(5000);
    }

    public boolean waitForConnectionClosed(int maxMillisecToWait) {

        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return !isConnectionOpen();
            }

        }, maxMillisecToWait);
    }

}

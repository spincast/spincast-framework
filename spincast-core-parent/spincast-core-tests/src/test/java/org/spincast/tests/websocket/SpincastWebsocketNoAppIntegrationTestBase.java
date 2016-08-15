package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.core.websocket.IWebsocketEndpointManager;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.plugins.httpclient.websocket.builders.IWebsocketRequestBuilder;
import org.spincast.plugins.undertow.config.ISpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.testing.core.SpincastNoAppIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;

import com.google.common.net.HttpHeaders;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * Base class for WebSocket tests without an existing
 * application.
 */
public abstract class SpincastWebsocketNoAppIntegrationTestBase<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                                               extends
                                                               SpincastNoAppIntegrationTestBase<IDefaultRequestContext, W> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastWebsocketNoAppIntegrationTestBase.class);

    private String expectedWebsocketV13AcceptHeaderValue;
    private String secSocketKey;

    @Inject
    protected ISpincastHttpClientUtils spincastHttpClientUtils;

    protected ISpincastHttpClientUtils getSpincastHttpClientUtils() {
        return this.spincastHttpClientUtils;
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        closeAllWebsocketEndpoints();
    }

    @Override
    protected Module getOverridingModule() {

        return new AbstractModule() {

            @Override
            protected void configure() {

                bind(ISpincastUndertowConfig.class).toInstance(new SpincastUndertowConfigDefault() {

                    //==========================================
                    // Server pings every seconds
                    //==========================================
                    @Override
                    public int getWebsocketAutomaticPingIntervalSeconds() {
                        return getServerPingIntervalSeconds();
                    }
                });
            }
        };
    }

    protected int getServerPingIntervalSeconds() {
        return 1;
    }

    protected void closeAllWebsocketEndpoints() {

        List<IWebsocketEndpointManager> websocketEndpointManagers = getServer().getWebsocketEndpointManagers();
        for(IWebsocketEndpointManager manager : websocketEndpointManagers) {
            manager.closeEndpoint(false);
        }
        assertTrue(SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getServer().getWebsocketEndpointManagers().size() == 0;
            }
        }));
    }

    protected String getWebsocketTestExpectedWebsocketV13AcceptHeaderValue() {
        if(this.expectedWebsocketV13AcceptHeaderValue == null) {
            this.expectedWebsocketV13AcceptHeaderValue =
                    getSpincastHttpClientUtils().generateExpectedWebsocketV13AcceptHeaderValue(getSecSocketKey());
        }
        return this.expectedWebsocketV13AcceptHeaderValue;
    }

    /**
     * Generates a random String for the "Sec-WebSocket-Key"
     * Websocket header.
     */
    protected String getSecSocketKey() {
        if(this.secSocketKey == null) {
            this.secSocketKey = UUID.randomUUID().toString();
        }
        return this.secSocketKey;
    }

    /**
     * Validates the response is a Websocket upgrade permission.
     */
    protected void validateIsWebsocketUpgradeHttpResponse(String path, IHttpResponse response) {

        assertNotNull(response);

        String upgradeHeader = response.getHeaderFirst(HttpHeaders.UPGRADE);
        assertNotNull(upgradeHeader);
        assertEquals("WebSocket", upgradeHeader);

        String locationHeader = response.getHeaderFirst(HttpHeadersExtra.SEC_WEBSOCKET_LOCATION);
        assertNotNull(locationHeader);
        assertEquals("ws://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpServerPort() + path,
                     locationHeader);

        String connectionHeader = response.getHeaderFirst(HttpHeaders.CONNECTION);
        assertNotNull(connectionHeader);
        assertEquals("Upgrade", connectionHeader);

        String websocketAcceptHeader = response.getHeaderFirst(HttpHeadersExtra.SEC_WEBSOCKET_ACCEPT);
        assertNotNull(websocketAcceptHeader);

        assertEquals(getWebsocketTestExpectedWebsocketV13AcceptHeaderValue(), websocketAcceptHeader);
    }

    /**
     * Validates the response is NOT a Websocket upgrade permission.
     */
    protected void validateIsNotWebsocketUpgradeHttpResponse(IHttpResponse response) {
        String upgradeHeader = response.getHeaderFirst(HttpHeaders.UPGRADE);
        assertNull(upgradeHeader);

        String websocketAcceptHeader = response.getHeaderFirst(HttpHeadersExtra.SEC_WEBSOCKET_ACCEPT);
        assertNull(websocketAcceptHeader);
    }

    /**
     * @param sslInfo If not null, then the initial connection
     * is considered to be HTTPS and Websocket is served using SSL too.
     */
    @Override
    protected IWebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        IWebsocketRequestBuilder builder = super.websocket(pathOrUrl, isFullUrl, isHttps);

        //==========================================
        // Add an known value for the "Sec-WebSocket-Key"
        // header so we can validate the response.
        //==========================================
        builder.addHeaderValue(HttpHeadersExtra.SEC_WEBSOCKET_KEY, getSecSocketKey());

        return builder;
    }

}

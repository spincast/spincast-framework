package org.spincast.defaults.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketEndpointManager;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Base class for WebSocket testing.
 */
public abstract class AppBasedWebsocketTestingBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                                  extends
                                                  AppBasedTestingBase<DefaultRequestContext, W> {

    protected final Logger logger = LoggerFactory.getLogger(AppBasedWebsocketTestingBase.class);

    @Inject
    protected SpincastHttpClientUtils spincastHttpClientUtils;

    private String expectedWebsocketV13AcceptHeaderValue;
    private String secSocketKey;

    @Override
    protected Module getExtraOverridingModule2() {

        Module extraModuleUserSpecified = getExtraOverridingModule3();
        if (extraModuleUserSpecified == null) {
            extraModuleUserSpecified = new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    // nothing
                }
            };
        }

        return Modules.override(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUndertowConfig.class).toInstance(getSpincastUndertowConfigImplementation());
            }
        }).with(extraModuleUserSpecified);
    }

    protected Module getExtraOverridingModule3() {
        return null;
    }

    protected SpincastUndertowConfig getSpincastUndertowConfigImplementation() {
        return new SpincastUndertowConfigDefault() {

            //==========================================
            // Server pings every seconds
            //==========================================
            @Override
            public int getWebsocketAutomaticPingIntervalSeconds() {
                return getServerPingIntervalSeconds();
            }
        };
    }

    protected SpincastHttpClientUtils getSpincastHttpClientUtils() {
        return this.spincastHttpClientUtils;
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        closeAllWebsocketEndpoints();
    }

    protected int getServerPingIntervalSeconds() {
        return 1;
    }

    protected void closeAllWebsocketEndpoints() {

        List<WebsocketEndpointManager> websocketEndpointManagers = getServer().getWebsocketEndpointManagers();
        for (WebsocketEndpointManager manager : websocketEndpointManagers) {
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
        if (this.expectedWebsocketV13AcceptHeaderValue == null) {
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
        if (this.secSocketKey == null) {
            this.secSocketKey = UUID.randomUUID().toString();
        }
        return this.secSocketKey;
    }

    /**
     * Validates the response is a Websocket upgrade permission.
     */
    protected void validateIsWebsocketUpgradeHttpResponse(String path, HttpResponse response) {

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
    protected void validateIsNotWebsocketUpgradeHttpResponse(HttpResponse response) {
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
    protected WebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        WebsocketRequestBuilder builder = super.websocket(pathOrUrl, isFullUrl, isHttps);

        //==========================================
        // Add an known value for the "Sec-WebSocket-Key"
        // header so we can validate the response.
        //==========================================
        builder.addHeaderValue(HttpHeadersExtra.SEC_WEBSOCKET_KEY, getSecSocketKey());

        return builder;
    }

}

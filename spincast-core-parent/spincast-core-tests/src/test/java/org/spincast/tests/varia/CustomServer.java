package org.spincast.tests.varia;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.controllers.IFrontController;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IWebsocketEndpointHandler;
import org.spincast.core.websocket.IWebsocketEndpointManager;

import com.google.inject.Inject;

/**
 * The server itself.
 */
public class CustomServer implements IServer {

    private final ISpincastConfig spincastConfig;
    private final IFrontController frontController;

    public String serverFlag = "";

    @Inject
    public CustomServer(ISpincastConfig spincastConfig,
                        IFrontController frontController) {
        assertNotNull(spincastConfig);
        assertNotNull(frontController);
        this.spincastConfig = spincastConfig;
        this.frontController = frontController;
        this.serverFlag += "constructor";
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected IFrontController getFrontController() {
        return this.frontController;
    }

    @Override
    public boolean isRunning() {
        return this.serverFlag.contains("start") && !this.serverFlag.contains("stop");
    }

    @Override
    public void start() {
        this.serverFlag += "start";
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public void stop(boolean sendClosingMessageToPeers) {
        this.serverFlag += "stop";
    }

    //==========================================
    // We simulate the method that the server will call when it
    // receives an actual HTTP request...
    //==========================================
    public void handle(CustomExchange exchange) {
        getFrontController().handle(exchange);
    }

    @Override
    public void addStaticResourceToServe(IStaticResource<?> staticResource) {
    }

    @Override
    public void removeStaticResourcesServed(StaticResourceType staticResourceType, String urlPath) {
    }

    @Override
    public IStaticResource<?> getStaticResourceServed(String urlPath) {
        return null;
    }

    @Override
    public Set<IStaticResource<?>> getStaticResourcesServed() {
        return null;
    }

    @Override
    public HttpMethod getHttpMethod(Object exchange) {
        return ((CustomExchange)exchange).httpMethod;
    }

    @Override
    public String getFullUrlOriginal(Object exchange) {
        return getFullUrlOriginal(exchange, false);
    }

    @Override
    public String getFullUrlOriginal(Object exchange, boolean keepCacheBusters) {
        return ((CustomExchange)exchange).fullUrl;
    }

    @Override
    public String getFullUrlProxied(Object exchange) {
        return getFullUrlProxied(exchange, false);
    }

    @Override
    public String getFullUrlProxied(Object exchange, boolean keepCacheBusters) {
        return getFullUrlOriginal(exchange, keepCacheBusters);
    }

    @Override
    public ContentTypeDefaults getContentTypeBestMatch(Object exchange) {
        return ContentTypeDefaults.fromString(((CustomExchange)exchange).contentType);
    }

    @Override
    public void addCookies(Object exchange, Map<String, ICookie> cookies) {
    }

    @Override
    public Map<String, ICookie> getCookies(Object exchange) {
        return null;
    }

    @Override
    public Map<String, List<String>> getQueryStringParams(Object exchange) {
        return null;
    }

    @Override
    public void setResponseStatusCode(Object exchange, int statusCode) {
    }

    @Override
    public void flushBytes(Object exchange, byte[] bytes, boolean end) {
    }

    @Override
    public void end(Object exchange) {
    }

    @Override
    public boolean isResponseClosed(Object exchange) {
        return false;
    }

    @Override
    public boolean isResponseHeadersSent(Object exchange) {
        return false;
    }

    @Override
    public String getRequestScheme(Object exchange) {
        return null;
    }

    @Override
    public Map<String, List<String>> getFormDatas(Object exchange) {
        return null;
    }

    @Override
    public Map<String, List<File>> getUploadedFiles(Object exchange) {
        return null;
    }

    @Override
    public InputStream getRawInputStream(Object exchange) {
        return null;
    }

    @Override
    public boolean forceRequestSizeValidation(Object exchange) {
        return true;
    }

    @Override
    public Map<String, List<String>> getRequestHeaders(Object exchange) {
        return null;
    }

    @Override
    public Map<String, List<String>> getResponseHeaders(Object exchange) {
        return null;
    }

    @Override
    public void setResponseHeader(Object exchange, String name, List<String> values) {
    }

    @Override
    public void removeResponseHeader(Object exchange, String name) {
    }

    @Override
    public void setResponseHeaders(Object exchange, Map<String, List<String>> headers) {
    }

    @Override
    public void removeAllStaticResourcesServed() {
    }

    @Override
    public void addHttpAuthentication(String pathPrefix, String username, String password) {
    }

    @Override
    public void createHttpAuthenticationRealm(String pathPrefix, String realmName) {
    }

    @Override
    public void removeHttpAuthentication(String username, String realmName) {
    }

    @Override
    public void removeHttpAuthentication(String username) {
    }

    @Override
    public Map<String, String> getHttpAuthenticationRealms() {
        return null;
    }

    @Override
    public List<IWebsocketEndpointManager> getWebsocketEndpointManagers() {
        return null;
    }

    @Override
    public IWebsocketEndpointManager websocketCreateEndpoint(String endpointId, IWebsocketEndpointHandler endpointHandler) {
        return null;
    }

    @Override
    public void websocketCloseEndpoint(String endpointId) {
    }

    @Override
    public void websocketCloseEndpoint(String endpointId, int closingCode, String closingReason) {
    }

    @Override
    public void websocketConnection(Object exchange, String endpointId, String peerId) {
    }

    @Override
    public IWebsocketEndpointManager getWebsocketEndpointManager(String endpointId) {
        return null;
    }

}

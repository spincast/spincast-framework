package org.spincast.core.server;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spincast.core.cookies.ICookie;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IWebsocketEndpointHandler;
import org.spincast.core.websocket.IWebsocketEndpointManager;

/**
 * The interface a HTTP server implementation must implement.
 * <p>
 * The "exchange" object is a request scoped object provided by the
 * HTTP server to identify the request.
 * </p>
 * <p>
 * A IServer implementation has to receive {@link org.spincast.core.controllers.IFrontController IFrontController} as
 * a dependency and call {@link org.spincast.core.controllers.IFrontController#handle(Object) handle(...)} on it when
 * a new request is made.
 * </p>
 * <p>
 * Note that the server will automatically remove any <code>cache buster codes</code>
 * from the request's path.
 * </p>
 */
public interface IServer {

    /**
     * Starts the server.
     */
    public void start();

    /**
     * Stops the server
     * <p>
     * Will try to send a "closing" message to any
     * WebSocket peer before closing their connections.
     * </p>
     */
    public void stop();

    /**
     * Stops the server
     * 
     * @param sendClosingMessageToPeers if <code>true</code>,
     * Spincast will try to send a "closing" message to any 
     * WebSocket peer before closing their connections.
     */
    public void stop(boolean sendClosingMessageToPeers);

    /**
     * Is this server running (started)?
     */
    public boolean isRunning();

    /**
     * Adds a static resource to serve directly by the server.
     */
    public void addStaticResourceToServe(IStaticResource<?> staticResource);

    /**
     * Removes a static resource served directly by the server.
     */
    public void removeStaticResourcesServed(StaticResourceType staticResourceType, String urlPath);

    /**
     * Removes all static resources served directly by the server.
     */
    public void removeAllStaticResourcesServed();

    /**
     * Gets a static resource served directly by the server, using its path.
     */
    public IStaticResource<?> getStaticResourceServed(String urlPath);

    /**
     * Gets all static resource served directly by the server.
     */
    public Set<IStaticResource<?>> getStaticResourcesServed();

    /**
     * Gets the HTTP method associated with the request.
     */
    public HttpMethod getHttpMethod(Object exchange);

    /**
     * The full encoded URL of the original, non proxied, request, including the queryString. 
     * Cache buster codes are removed, if there were any.
     * <p>
     * This is going to be the *original* URL, as seen by the user, even if 
     * a reverse proxy is used (such as Nginx or Apache).
     * </p>
     * <p>
     * Even if the request is forwarded elsewhere in the framework, this
     * URL won't change, it will still be the original one.
     * </p>
     */
    public String getFullUrlOriginal(Object exchange);

    /**
     * The full encoded URL of the original, non proxied, request, including the queryString. 
     * <p>
     * This is going to be the *original* URL, as seen by the user, even if 
     * a reverse proxy is used (such as Nginx or Apache).
     * </p>
     * <p>
     * Even if the request is forwarded elsewhere in the framework, this
     * URL won't change, it will still be the original one.
     * </p>
     * 
     * @param keepCacheBusters if <code>true</code>, the returned URL will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getFullUrlOriginal(Object exchange, boolean keepCacheBusters);

    /**
     * The full encoded URL of the potentially proxied request, including the queryString. 
     * Cache buster codes are removed, if there were any.
     * <p>
     * Is a reverse proxy is used (such as Nginx or Apache), this is going to be the
     * proxied URL, as forwarded by the reverse proxy. If no reverse proxy is used,
     * this is going to be the original URL, as seen by the user.
     * </p>
     * <p>
     * Even if the request is forwarded elsewhere in the framework, this
     * URL won't change, it will still be the original one.
     * </p>
     */
    public String getFullUrlProxied(Object exchange);

    /**
     * The full encoded URL of the potentially proxied request, including the queryString. 
     * <p>
     * Is a reverse proxy is used (such as Nginx or Apache), this is going to be the
     * proxied URL, as forwarded by the reverse proxy. If no reverse proxy is used,
     * this is going to be the original URL, as seen by the user.
     * </p>
     * <p>
     * Even if the request is forwarded elsewhere in the framework, this
     * URL won't change, it will still be the original one.
     * </p>
     * 
     * @param keepCacheBusters if <code>true</code>, the returned URL will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getFullUrlProxied(Object exchange, boolean keepCacheBusters);

    /**
     * Gets the best <code>Content-Type</code> to use for the current request.
     */
    public ContentTypeDefaults getContentTypeBestMatch(Object exchange);

    /**
     * Sets the response headers. Override any existing ones.
     */
    public void setResponseHeaders(Object exchange, Map<String, List<String>> headers);

    /**
     * Sets a response header. Override any existing one with the same name.
     */
    public void setResponseHeader(Object exchange, String name, List<String> values);

    /**
     * Gets the response headers.
     */
    public Map<String, List<String>> getResponseHeaders(Object exchange);

    /**
     * Removes a response header.
     */
    public void removeResponseHeader(Object exchange, String name);

    /**
     * Adds cookies.
     */
    public void addCookies(Object exchange, Map<String, ICookie> cookies);

    /**
     * Gets the current cookies.
     */
    public Map<String, ICookie> getCookies(Object exchange);

    /**
     * Gets the queryString parameters.
     */
    public Map<String, List<String>> getQueryStringParams(Object exchange);

    /**
     * Sets the response status code.
     */
    public void setResponseStatusCode(Object exchange, int statusCode);

    /**
     * Flushes some bytes to the response.
     * 
     * @param end if <code>true</code>, the exchange will be closed
     * and nothing more can be send.
     */
    public void flushBytes(Object exchange, byte[] bytes, boolean end);

    /**
     * Ends the exchange. Nothing more can be send.
     */
    public void end(Object exchange);

    /**
     * Is the response closed?
     */
    public boolean isResponseClosed(Object exchange);

    /**
     * Are the response headers sent?
     */
    public boolean isResponseHeadersSent(Object exchange);

    /**
     * Gets the request scheme, "http" for example.
     */
    public String getRequestScheme(Object exchange);

    /**
     * The raw InputStream of the current request.
     */
    public InputStream getRawInputStream(Object exchange);

    /**
     * The form datas, if any.
     */
    public Map<String, List<String>> getFormDatas(Object exchange);

    /**
     * The uploaded files, if any.
     */
    public Map<String, List<File>> getUploadedFiles(Object exchange);

    /**
     * Is the request size valid?
     */
    public boolean forceRequestSizeValidation(Object exchange);

    /**
     * The headers from the request. 
     * The names are all lowercased.
     */
    public Map<String, List<String>> getRequestHeaders(Object exchange);

    /**
     * Creates HTTP authentication protection (realm) for the
     * specified path prefix.
     * 
     * @param realmName The name of the realm. Must be unique on this server,
     * otherwise an exception is thrown. This allows the application to add
     * user to the realm using its name.
     */
    public void createHttpAuthenticationRealm(String pathPrefix, String realmName);

    /**
     * Returns the existing HTTP authentication realms, the
     * key being the realm's name and the value being the
     * prefix path associated to this realm.
     * <p>
     * The map is immutable.
     * </p>
     */
    public Map<String, String> getHttpAuthenticationRealms();

    /**
     * Adds a user to an HTTP protected realm.
     */
    public void addHttpAuthentication(String realmName, String username, String password);

    /**
     * Removes a user to an HTTP protected realm.
     */
    public void removeHttpAuthentication(String username, String realmName);

    /**
     * Removes a user from all HTTP protected realms.
     */
    public void removeHttpAuthentication(String username);

    /**
     * Creates a new Websocket endpoint.
     * 
     * @return the manager for this endpoint.
     */
    public IWebsocketEndpointManager websocketCreateEndpoint(String endpointId, IWebsocketEndpointHandler endpointHandler);

    /**
     * Closes a Websocket endpoint. No more connections will
     * be accepter
     */
    public void websocketCloseEndpoint(String endpointId);

    /**
     * Closes the entire Websocket endpoint. 
     * All peer connections of this endpoint will be
     * closed.
     * 
     * @param closingCode The closing code.
     * @param closingReason The closing reason.
     */
    public void websocketCloseEndpoint(String endpointId, int closingCode, String closingReason);

    /**
     * Transforms the request to a peer Websocket connection
     * on the endpoint 'endpointId'. 
     */
    public void websocketConnection(Object exchange,
                                    String endpointId,
                                    String peerId);

    /**
     * Returns the managers of the existing Websockets endpoints. 
     */
    public List<IWebsocketEndpointManager> getWebsocketEndpointManagers();

    /**
     * Returns the manager for a Websockets endpoint. 
     * 
     * @return the manager or <code>null</code> if not found.
     */
    public IWebsocketEndpointManager getWebsocketEndpointManager(String endpointId);

}

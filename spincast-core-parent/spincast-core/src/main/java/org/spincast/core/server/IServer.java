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

/**
 * The interface a HTTP server implementation must implement.
 * 
 * The "exchange" object is a request scoped object provided by the
 * HTTP server to identify the request.
 * 
 * A IServer implementation has to receive {@link org.spincast.core.controllers.IFrontController IFrontController} as
 * a dependency and call {@link org.spincast.core.controllers.IFrontController#handle(Object) handle(...)} on it when
 * a new request is made.
 */
public interface IServer {

    /**
     * Starts the server.
     */
    public void start();

    /**
     * Stops the server
     */
    public void stop();

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
     * The full encoded URL of the original request, including the queryString. 
     * Even if the request is forwarded elsewhere in the framework, this
     * URL won't change, it will still be the original one.
     */
    public String getFullUrl(Object exchange);

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
     * and nothing more will be able to be send.
     */
    public void flushBytes(Object exchange, byte[] bytes, boolean end);

    /**
     * Ends the exchange. Nothing more will be able to be send.
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
    public void addHttpAuthentication(String realmName,
                                      String username,
                                      String password);

    /**
     * Removes a user to an HTTP protected realm.
     */
    public void removeHttpAuthentication(String username, String realmName);

    /**
     * Removes a user from all HTTP protected realms.
     */
    public void removeHttpAuthentication(String username);

}

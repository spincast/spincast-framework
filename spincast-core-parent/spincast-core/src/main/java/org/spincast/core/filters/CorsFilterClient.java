package org.spincast.core.filters;

import java.util.Set;

import org.spincast.core.routing.HttpMethod;

/**
 * Interface for a client of the Cors filter.
 */
public interface CorsFilterClient {

    /**
     * Gets the first value of an header.
     */
    public String getHeaderFirst(String name);

    /**
     * Are the headers sent?
     */
    public boolean isHeadersSent();

    /**
     * Gets the full URL of the request.
     */
    public String getFullUrl();

    /**
     * Resets the response.
     */
    public void resetEverything();

    /**
     * Sets the response's status code.
     */
    public void setStatusCode(int code);

    /**
     * Gets the request's HTTP method.
     */
    public HttpMethod getHttpMethod();

    /**
     * Adds an header value.
     */
    public void addHeaderValue(String name, String value);

    /**
     * DOes the request contain cookies?
     */
    public boolean requestContainsCookies();

    /**
     * The allowed origins, for the cors request.
     */
    public Set<String> getAllowedOrigins();

    /**
     * The extra headers allowed to be read, for the cors request.
     */
    public Set<String> getExtraHeadersAllowedToBeRead();

    /**
     * The extra headers allowed to be sent, for the cors request.
     */
    public Set<String> getExtraHeadersAllowedToBeSent();

    /**
     * Are cookies allowed in the cors request?
     */
    public boolean isAllowCookies();

    /**
     * The allowed HTTP methods, for the cors request.
     */
    public Set<HttpMethod> getAllowedMethods();

    /**
     * The max age to cache a cors preflight response.
     */
    public int getMaxAgeInSeconds();

}

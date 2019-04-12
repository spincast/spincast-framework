package org.spincast.core.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A resource to push using HTTP/2.
 */
public class ResourceToPush {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, List<String>> requestHeaders;

    public ResourceToPush(HttpMethod httpMethod, String path, Map<String, List<String>> requestHeaders) {
        this.httpMethod = httpMethod;
        this.path = path;
        if (requestHeaders == null) {
            requestHeaders = new HashMap<String, List<String>>();
        }
        this.requestHeaders = requestHeaders;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return this.requestHeaders;
    }

    @Override
    public String toString() {
        return getHttpMethod() + " - " + getPath();
    }

}

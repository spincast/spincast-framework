package org.spincast.core.routing;

import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

/**
 * Enum for the <code>HTTP methods</code>.
 */
public enum HttpMethod {

    CONNECT,
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT,
    TRACE;

    /**
     * Returns a HttpMethod from its String representation,
     * or <code>null</code> if not found.
     */
    public static HttpMethod fromStringValue(String stringValue) {

        if(!StringUtils.isBlank(stringValue)) {

            stringValue = stringValue.toUpperCase();

            for(HttpMethod httpMethod : HttpMethod.values()) {
                if(stringValue.equals(httpMethod.name())) {
                    return httpMethod;
                }
            }
        }

        return null;
    }
}

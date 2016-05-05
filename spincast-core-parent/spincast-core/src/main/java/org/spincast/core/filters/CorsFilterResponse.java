package org.spincast.core.filters;

/**
 * A response from the cors filter.
 */
public enum CorsFilterResponse {
    INVALID_CORS_REQUEST,
    SIMPLE,
    PREFLIGHT,
    NOT_CORS,
    HEADERS_ALREADY_SENT
}

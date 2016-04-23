package org.spincast.tests.varia;

import org.spincast.core.routing.HttpMethod;

/**
 * The class used to create request scoped exchange 
 * objects.
 */
public class CustomExchange {

    public HttpMethod httpMethod;
    public String fullUrl;
    public String contentType;
}

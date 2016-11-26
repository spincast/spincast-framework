package org.spincast.core.controllers;

/**
 * The front controller is called by the HTTP server when a request for
 * something else than a <code>static resource</code> arrives. It finds the
 * appropriate route to use and will call the associated
 * handlers. It also manages exceptions and not found resources.
 */
public interface FrontController {

    /**
     * @param exchange an object representing the current request,
     * provided by the HTTP server
     */
    void handle(Object exchange);

}

package org.spincast.core.exceptions;

/**
 * Exception that will forward the request to another route.
 * 
 */
public class ForwardRouteException extends ResponseResetableException {

    private static final long serialVersionUID = 1L;

    private final String newRoute;

    /**
     * @param newRoute The new route to forward to. This can be a
     * full URL or a path (+ potential queryString)
     * 
     */
    public ForwardRouteException(String newRoute) {
        super();
        this.newRoute = newRoute;
    }

    /**
     * @param newRoute The new route to forward to. This can be a
     * full URL or a path (+ potential queryString)
     * 
     * @param resetResponse If not already flushed, should the response be
     * reset before running the new route?
     */
    public ForwardRouteException(String newRoute, boolean resetResponse) {
        super(resetResponse);
        this.newRoute = newRoute;
    }

    /**
     * @param newRoute The new route to forward to. This can be a
     * full URL or a path (+ potential queryString)
     */
    public ForwardRouteException(String newRoute, String message) {
        super(message);
        this.newRoute = newRoute;
    }

    /**
     * @param newRoute The new route to forward to. This can be a
     * full URL or a path (+ potential queryString)
     * 
     * @param resetResponse If not already flushed, should the response be
     * reset before running the new route?
     */
    public ForwardRouteException(String newRoute, String message, boolean resetResponse) {
        super(message, resetResponse);
        this.newRoute = newRoute;
    }

    /**
     * The new route to forward to. This can be a
     * full URL or a path (+ potential queryString)
     */
    public String getNewRoute() {
        return this.newRoute;
    }

}

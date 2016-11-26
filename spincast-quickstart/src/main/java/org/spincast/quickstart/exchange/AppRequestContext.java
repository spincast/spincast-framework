package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.RequestContext;

/**
 * Custom type which allows our application to
 * extend the default Request Context.
 * 
 * Spincast will pass an instance of this class to all matching
 * <code>Route Handlers</code>, when a request arrives.
 */
public interface AppRequestContext extends RequestContext<AppRequestContext> {

    /**
     * A custom method example.
     * This will simply output a plain text "Hello [NAME]".
     */
    public void customGreetingMethod();

}

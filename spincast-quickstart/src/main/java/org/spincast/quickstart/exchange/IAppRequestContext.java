package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.IRequestContext;

/**
 * Custom type which allows our application to
 * extend the default request context and add custom methods
 * and add-ons provided by plugins.
 * 
 * Spincast will pass an instance of this class to all matching
 * <code>route handlers</code>, when a request arrives.
 */
public interface IAppRequestContext extends IRequestContext<IAppRequestContext> {

    /**
     * A custom method example.
     * This will simply output a plain text "Hello [NAME]".
     */
    public void customGreetingMethod();

}

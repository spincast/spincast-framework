package org.spincast.core.config;

/**
 * Labels required by Spincast.
 */
public interface ISpincastDictionary {

    /**
     * The message to display if the default <code>Not Found</code> 
     * route is used.
     * 
     * @return the message to display
     */
    public String route_notFound_default_message();

    /**
     * The message to display if the default <code>Exception</code> 
     * route is used.
     * 
     * @return the message to display
     */
    public String exception_default_message();

}

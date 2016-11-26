package org.spincast.core.response;

/**
 * An <code>Alert</code> is am Error, a Warning
 * or a Confirmation message often displayed at the top
 * of a webpage to inform the user about the result
 * of an action.
 */
public interface Alert {

    /**
     * The type of the Alert message.
     */
    public AlertLevel getAlertType();

    /**
     * The text of the Alert message.
     */
    public String getText();

}

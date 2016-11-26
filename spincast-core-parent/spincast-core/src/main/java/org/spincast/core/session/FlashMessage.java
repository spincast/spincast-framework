package org.spincast.core.session;

import org.spincast.core.json.JsonObject;

/**
 * A Flash message. Will only be displayed one time
 * to the user when a redirection is performed.
 */
public interface FlashMessage {

    /**
     * The type of the Flash message.
     */
    public FlashMessageLevel getFlashType();

    /**
     * The text of the Flash message.
     */
    public String getText();

    /**
     * Potential variables associated with this Flash
     * message. May be <code>null</code>!
     */
    public JsonObject getVariables();

}

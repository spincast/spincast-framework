package org.spincast.core.session;

/**
 * Currently used to save the Flash Messages between the time
 * a page sets one, and the time the next page displays it.
 */
public interface FlashMessagesHolder {

    /**
     * Saves a Flash Message.
     * 
     * @return a generated and unique ID for the Flash Message.
     */
    public String saveFlashMessage(FlashMessage flashMessage);

    /**
     * Gets a Flash Message from its id.
     * 
     * @param removeIt if <code>true</code>, the Flash Message is 
     * removed from the holder.
     */
    public FlashMessage getFlashMessage(String id, boolean removeIt);

}

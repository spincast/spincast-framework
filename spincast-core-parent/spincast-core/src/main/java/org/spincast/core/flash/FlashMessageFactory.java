package org.spincast.core.flash;

import org.spincast.core.json.JsonObject;

/**
 * Factory to create Flash Messages.
 */
public interface FlashMessageFactory {

    public FlashMessage create(FlashMessageLevel messageType, String text);

    public FlashMessage create(FlashMessageLevel messageType, String text, JsonObject variables);

}

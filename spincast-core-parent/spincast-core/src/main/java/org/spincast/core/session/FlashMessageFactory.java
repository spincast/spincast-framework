package org.spincast.core.session;

import org.spincast.core.json.JsonObject;

/**
 * Factory to create Flash Messages.
 */
public interface FlashMessageFactory {

    public FlashMessage create(FlashMessageLevel messageType, String text);

    public FlashMessage create(FlashMessageLevel messageType, String text, JsonObject variables);

}

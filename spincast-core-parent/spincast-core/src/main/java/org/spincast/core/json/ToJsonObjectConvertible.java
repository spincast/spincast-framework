package org.spincast.core.json;

/**
 * An object implementing this interface is able to
 * convert itself to a <code>JsonObject</code>.
 * <p>
 * This is useful when full control over the conversion
 * process is required.
 */
public interface ToJsonObjectConvertible {

    /**
     * Converts the object to a <code>JsonObject</code>.
     */
    public JsonObject convertToJsonObject();

}

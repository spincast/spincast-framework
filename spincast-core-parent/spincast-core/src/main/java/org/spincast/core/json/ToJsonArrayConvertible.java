package org.spincast.core.json;

/**
 * An object implementing this interface is able to
 * convert itself to a <code>JsonArray</code>.
 * <p>
 * This is useful when full control over the conversion
 * process is required.
 */
public interface ToJsonArrayConvertible {

    /**
     * Converts the object to a <code>JsonArray</code>.
     */
    public JsonArray convertToJsonArray();
}

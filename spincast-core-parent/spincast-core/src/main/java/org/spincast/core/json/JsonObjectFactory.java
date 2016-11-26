package org.spincast.core.json;

import java.util.List;
import java.util.Map;

/**
 * Factory to create <code>JsonObject</code> 
 * and <code>JsonArray</code> objects.
 */
public interface JsonObjectFactory {

    /**
     * Creates an empty and mutable JsonObject.
     */
    public JsonObject create();

    /**
     * Creates an IJonObject based on the specified Map.
     * <p>
     * The keys will be parsed as <code>JsonPaths</code>.
     * <p>
     * Any none native objects will
     * be cloned to a <code>JsonObject</code>.
     * 
     * @param mutable if <code>true</code> the resulting
     * object and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    public JsonObject create(Map<String, Object> params, boolean mutable);

    /**
     * Creates an empty and mutable JsonArray.
     */
    public JsonArray createArray();

    /**
     * Creates a JsonArray based on the List.
     * <p>
     * Any none native objects will
     * be cloned to a <code>JsonObject</code>.
     * 
     * @param mutable if <code>true</code> the resulting
     * array and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    public JsonArray createArray(List<Object> elements, boolean mutable);

}

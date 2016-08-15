package org.spincast.core.json;

import java.util.List;
import java.util.Map;

/**
 * Factory to create <code>IJsonObject</code> 
 * and <code>IJsonArray</code> objects.
 */
public interface IJsonObjectFactory {

    /**
     * Creates an empty IJsonObject.
     */
    public IJsonObject create();

    /**
     * Creates an IJonObject based on the specified Map.
     * <p>
     * Any none primitive (or BigDecimal) object will
     * be cloned to a <code>IJsonObject</code> or
     * <code>IJsonArray</code> so an external modification 
     * won't affect it and vice-versa.
     * </p>
     */
    public IJsonObject create(Map<String, ?> params);

    /**
     * Create a IJsonObject that is flagged as <em>immutable</em>.
     * Note that the map that is passed as a parameter must already
     * be immutable, and all its elements too!
     */
    public IJsonObjectImmutable createImmutable(Map<String, Object> immutableMapAndChildren);

    /**
     * Creates an empty IJsonArray.
     */
    public IJsonArray createArray();

    /**
     * Creates a IJsonArray based on the List.
     * <p>
     * Any none primitive (or BigDecimal) object will
     * be cloned to a <code>IJsonObject</code> or
     * <code>IJsonArray</code> so an external modification 
     * won't affect it and vice-versa.
     * </p>
     */
    public IJsonArray createArray(List<Object> elements);

    /**
     * Create a IJsonArray that is flagged as <em>immutable</em>.
     * Note that the list that is passed as a parameter must already
     * be immutable, and all its children too!
     */
    public IJsonArrayImmutable createArrayImmutable(List<Object> immutableListAndChildren);

}

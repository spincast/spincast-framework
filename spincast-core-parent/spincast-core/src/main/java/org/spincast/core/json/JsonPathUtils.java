package org.spincast.core.json;

/**
 * Utilities to deal with <code>JsonPaths</code>.
 */
public interface JsonPathUtils {

    /**
     * Gets an element from the <code>JsonObject</code>,
     * at the specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath);

    /**
     * Gets an element from the <code>JsonObject</code>, 
     * at the specified <code>JsonPath</code>.
     * 
     * @return the element or the <code>default element</code> if not found.
     */
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath, Object defaultElement);

    /**
     * Gets an element from the <code>JsonArray</code>, at the 
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonArray array, String jsonPath);

    /**
     * Gets an element from the <code>JsonArray</code>, at the 
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the <code>default element</code> if not found.
     */
    public Object getElementAtJsonPath(JsonArray array, String jsonPath, Object defaultElement);

    /**
     * Puts an element in the object at the specified <code>JsonPath</code>
     * position. No clone is made, the element is put as is.
     * <p>
     * The complete hierarchy to the final element is created if required.
     * </p>
     */
    public void putElementAtJsonPath(JsonObjectOrArray root, String jsonPath, Object elementToAdd);

    /**
     * Removes an element at the specified <code>JsonPath</code> from the
     * object.
     */
    public void removeElementAtJsonPath(JsonObject obj, String jsonPath);

    /**
     * Removes an element at the specified <code>JsonPath</code> from the
     * array.
     */
    public void removeElementAtJsonPath(JsonArray array, String jsonPath);

    /**
     * Does the object contain an element at
     * the <code>JsonPath</code> position (even if 
     * <code>null</code>)?
     */
    public boolean isElementExists(JsonObject obj, String jsonPath);

    /**
     * Does the array contain an element at
     * the <code>JsonPath</code> position (even if 
     * <code>null</code>)?
     */
    public boolean isElementExists(JsonArray array, String jsonPath);

}

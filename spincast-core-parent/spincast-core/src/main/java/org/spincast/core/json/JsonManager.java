package org.spincast.core.json;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to play with <code>Json</code> strings and objects.
 */
public interface JsonManager {

    /**
     * Creates an empty <code>JsonObject</code>
     */
    public JsonObject create();

    /**
     * Creates a <code>JsonObject</code> from a <code>Json</code>
     * String.
     */
    public JsonObject fromString(String jsonString);

    /**
     * Creates an empty <code>JsonObject</code> based on the specified Map.
     * An attempt will be made to create a deep copy of every elements so
     * a modification won't affect any external references and vice-versa.
     * <p>
     * The keys will be used <em>as is</em>, not parsed as JsonPaths.
     * </p>
     */
    public JsonObject fromMap(Map<String, ?> params);

    /**
     * Creates an empty <code>JsonObject</code> based on the specified Map.
     * An attempt will be made to create a deep copy of every elements so
     * a modification won't affect any external references and vice-versa.
     * 
     * @param parseKeysAsJsonPaths if <code>true</code>, the keys will
     * be parsed as <code>JsonPaths</code>, otherwise they will ne used
     * as is.
     */
    public JsonObject fromMap(Map<String, ?> params, boolean parseKeysAsJsonPaths);

    /**
     * Creates a <code>JsonObject</code> from an inputStream.
     */
    public JsonObject fromInputStream(InputStream inputStream);

    /**
     * Creates a <code>JsonObject</code> from a Json file.
     */
    public JsonObject fromFile(File jsonFile);

    /**
     * Creates a <code>JsonObject</code> from the path of
     * a file, on the file system.
     */
    public JsonObject fromFile(String jsonFilePath);

    /**
     * Creates a <code>JsonObject</code> from a classpath
     * Json file.
     * 
     * @param the path to the classpath file. This path always
     * starts from the root of the classpath.
     */
    public JsonObject fromClasspathFile(String path);

    /**
     * Creates a <code>Map&lt;String, Object&gt;</code> from a <code>Json</code>
     * String.
     */
    public Map<String, Object> fromStringToMap(String jsonString);

    /**
     * Creates a <code>Map&lt;String, Object&gt;</code> from a <code>Json</code> inputStream.
     */
    public Map<String, Object> fromInputStreamToMap(InputStream inputStream);

    /**
     * Creates an instance of the specified <code>T</code> type
     * from a <code>Json</code> String. 
     */
    public <T> T fromString(String jsonString, Class<T> clazz);

    /**
     * Creates an instance of the specified <code>T</code> type
     * from a <code>Json</code> inputStream.
     */
    public <T> T fromInputStream(InputStream inputStream, Class<T> clazz);

    /**
     * Creates an empty <code>JsonArray</code>.
     */
    public JsonArray createArray();

    /**
     * Creates a <code>JsonArray</code> from a <code>Json</code>
     * String.
     */
    public JsonArray fromStringArray(String jsonString);

    /**
     * Creates a <code>JsonArray</code> from a <code>List</code>
     * of elements.
     */
    public JsonArray fromListArray(List<?> elements);

    /**
     * Creates a <code>JsonArray</code> from an inputStream.
     */
    public JsonArray fromInputStreamArray(InputStream inputStream);

    /**
     * Gets the <code>Json</code> String representation of 
     * the specified object.
     */
    public String toJsonString(Object obj);

    /**
     * Gets the <code>Json</code> String representation of the 
     * specified object.
     * @param pretty if <code>true</code>, the generated 
     * String will be formatted.
     */
    public String toJsonString(Object obj, boolean pretty);

    /**
     * Converts a <code>Json</code> date to a Java <code>UTC</code> date. 
     */
    public Date parseDateFromJson(String str);

    /**
     * Converts a Date to a <code>Json</code> date format.  
     */
    public String convertToJsonDate(Date date);

    /**
     * Convert a random object to a valid native JsonObject type, if
     * it's not already.
     */
    public Object convertToNativeType(Object originalObject);

    /**
     * Tries to clone an object to a <code>JsonObject</code> or
     * a <code>JsonArray</code>, if the object is not of
     * a primitive type (or <code>BigDecimal</code>).
     * <p>
     * The cloning is made by serializing the Object to a
     * Json string, and deserializing back. So any (de)serialization
     * rules apply.
     * </p>
     * <p>
     * The non primitive object will be cloned to a <code>JsonArray</code>
     * if it is a :
     * <ul>
     * <li>
     * JsonArray
     * </li>
     * <li>
     * Collection
     * </li>
     * <li>
     * Array
     * </li>
     * </ul>
     * </p>
     */
    public Object clone(Object originalObject);

    /**
     * Tries to clone an object to a <code>JsonObject</code> or
     * a <code>JsonArray</code>, if the object is not of
     * a primitive type (or <code>BigDecimal</code>).
     * <p>
     * The cloning is made by serializing the Object to a
     * Json string, and deserializing back. So any (de)serialization
     * rules apply.
     * </p>
     * <p>
     * The non primitive object will be cloned to a <code>JsonArray</code>
     * if it is a :
     * <ul>
     * <li>
     * JsonArray
     * </li>
     * <li>
     * Collection
     * </li>
     * <li>
     * Array
     * </li>
     * </ul>
     * </p>
     * 
     * @param mutable if <code>false</code>, the resulting
     * Object and all its potential children 
     * will be immutable.
     */
    public Object clone(Object originalObject, boolean mutable);

    /**
     * Deep copy of the <code>JsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * Note that if the current object is immutable and
     * the <code>mutable</code> parameter is set to <code>false</code>,
     * then the current object will be returned as is, since no cloning
     * is required.
     * 
     * @param mutable if <code>true</code> the resulting
     * array and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    public JsonObject cloneJsonObject(JsonObject jsonObject, boolean mutable);

    /**
     * Deep copy of the <code>JsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * Note that if the current object is immutable and
     * the <code>mutable</code> parameter is set to <code>false</code>,
     * then the current array will be returned as is, since no cloning
     * is required.
     * 
     * @param mutable if <code>true</code> the resulting
     * array and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    public JsonArray cloneJsonArray(JsonArray jsonArray, boolean mutable);

    /**
     * Gets an element from the <code>JsonObject</code> 
     * at the specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath);

    /**
     * Gets an element from the <code>JsonObject</code> 
     * at the specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath, Object defaultElement);

    /**
     * Gets an element from the <code>JsonArray</code> at the 
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonArray array, String jsonPath);

    /**
     * Gets an element from the <code>JsonArray</code> at the 
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object getElementAtJsonPath(JsonArray array, String jsonPath, Object defaultElement);

    /**
     * Puts an element in the object at the specified <code>JsonPath</code>.
     * <p>
     * All the hierarchy to the end of the <code>JsonPath</code> 
     * is created if required.
     */
    public void putElementAtJsonPath(JsonObjectOrArray obj, String jsonPath, Object element);

    /**
     * Puts a clone of the element in the object at the 
     * specified <code>JsonPath</code>.
     * <p>
     * All the hierarchy to the end of the <code>JsonPath</code> 
     * is created if required.
     */
    public void putElementAtJsonPath(JsonObjectOrArray obj, String jsonPath, Object element, boolean clone);

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
     * the specified <code>JsonPath</code> (even if 
     * <code>null</code>)?
     */
    public boolean isElementExists(JsonObject obj, String jsonPath);

    /**
     * Does the array contain an element at
     * the specified <code>JsonPath</code> (even if 
     * <code>null</code>)?
     */
    public boolean isElementExists(JsonArray array, String jsonPath);

}

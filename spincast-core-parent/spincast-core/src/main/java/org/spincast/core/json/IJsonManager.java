package org.spincast.core.json;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to play with <code>Json</code> strings and objects.
 */
public interface IJsonManager {

    /**
     * Creates an empty <code>JsonObject</code>
     */
    public IJsonObject create();

    /**
     * Creates a <code>JsonObject</code> from a <code>Json</code>
     * String.
     */
    public IJsonObject create(String jsonString);

    /**
     * Creates an empty <code>JsonObject</code> based on the specified Map.
     * An attempt will be made to create a deep copy of every elements so
     * a modification won't affect any external references and vice-versa.
     * <p>
     * The keys will be used <em>as is</em>. Use the
     * {@link #create(Map, boolean) create(Map, boolean)} version to parse
     * the keys to IJsonObjects and IJsonArrays.
     * </p>
     */
    public IJsonObject create(Map<String, ?> params);

    /**
     * Creates an empty <code>JsonObject</code> based on the specified Map.
     * An attempt will be made to create a deep copy of every elements so
     * a modification won't affect any external references and vice-versa.
     * 
     * @param parseKeyAsFieldPath If <code>true</code>, the keys will be parsed
     * as <code>FieldPaths</code> so a IJSonObject object is created.
     * For example : <code>user.books[1].name</code>
     * will be converted to a IJsonObject with a "books" arrays which
     * has a IJsonObject book object at position 1 and this book 
     * has a "name" property.
     */
    public IJsonObject create(Map<String, ?> params, boolean parseKeyAsFieldPath);

    /**
     * Creates a <code>JsonObject</code> from an inputStream.
     */
    public IJsonObject create(InputStream inputStream);

    /**
     * Make a <code>IJsonObject</code> immutable, no element
     * could be added or removed on it.
     */
    public IJsonObjectImmutable createImmutable(IJsonObject jsonObject);

    /**
     * Creates an empty <code>JsonArray</code>.
     */
    public IJsonArray createArray();

    /**
     * Creates a <code>JsonArray</code> from a <code>Json</code>
     * String.
     */
    public IJsonArray createArray(String jsonString);

    /**
     * Creates a <code>JsonArray</code> from a <code>List</code>
     * of elements.
     */
    public IJsonArray createArray(List<?> elements);

    /**
     * Creates a <code>JsonArray</code> from an inputStream.
     */
    public IJsonArray createArray(InputStream inputStream);

    /**
     * Make a <code>IJsonArray</code> immutable, no element
     * could be added or removed on it.
     */
    public IJsonArrayImmutable createArrayImmutable(IJsonArray jsonArray);

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
     * Creates a <code>Map&lt;String, Object&gt;</code> from a <code>Json</code>
     * String.
     */
    public Map<String, Object> fromJsonStringToMap(String jsonString);

    /**
     * Creates a <code>Map&lt;String, Object&gt;</code> from a <code>Json</code> inputStream.
     */
    public Map<String, Object> fromJsonInputStreamToMap(InputStream inputStream);

    /**
     * Creates an instance of the specified <code>T</code> type
     * from a <code>Json</code> String. 
     */
    public <T> T fromJsonString(String jsonString, Class<T> clazz);

    /**
     * Creates an instance of the specified <code>T</code> type
     * from a <code>Json</code> inputStream.
     */
    public <T> T fromJsonInputStream(InputStream inputStream, Class<T> clazz);

    /**
     * Converts a <code>Json</code> date to a Java <code>UTC</code> date. 
     */
    public Date parseDateFromJson(String str);

    /**
     * Convert a random object to a valid native Json type, if
     * it's not already.
     */
    public Object convertToNativeType(Object originalObject);

    /**
     * Tries to clone an object to a <code>IJsonObject</code> or
     * a <code>IJsonArray</code>, if the object is not of
     * a primitive type (or <code>BigDecimal</code>).
     * <p>
     * The cloning is made by serializing the Object to a
     * Json string, and deserializing back. So any (de)serialization
     * rules apply.
     * </p>
     * <p>
     * The non primitive object will be cloned to a <code>IJsonArray</code>
     * if it is a :
     * <ul>
     * <li>
     * IJsonArray
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
     * Tries to clone an object to a <code>IJsonObject</code> or
     * a <code>IJsonArray</code>, if the object is not of
     * a primitive type (or <code>BigDecimal</code>).
     * <p>
     * The cloning is made by serializing the Object to a
     * Json string, and deserializing back. So any (de)serialization
     * rules apply.
     * </p>
     * <p>
     * The non primitive object will be cloned to a <code>IJsonArray</code>
     * if it is a :
     * <ul>
     * <li>
     * IJsonArray
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
     * Deep copy of the <code>IJsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * The resulting <code>IJsonObject</code> is mutable.
     * </p>
     */
    public IJsonObject cloneJsonObject(IJsonObject jsonObject);

    /**
     * Deep copy of the <code>IJsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * 
     * @param mutable if <code>false</code>, the resulting
     * <code>IJsonArray</code> and all its children 
     * will be immutable.
     */
    public IJsonObject cloneJsonObject(IJsonObject jsonObject, boolean mutable);

    /**
     * Deep copy of the <code>IJsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * The resulting <code>IJsonArray</code> is mutable.
     * </p>
     */
    public IJsonArray cloneJsonArray(IJsonArray jsonArray);

    /**
     * Deep copy of the <code>IJsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * 
     * @param mutable if <code>false</code>, the resulting
     * <code>IJsonArray</code> and all its children 
     * will be immutable.
     */
    public IJsonArray cloneJsonArray(IJsonArray jsonArray, boolean mutable);

}

package org.spincast.core.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.spincast.core.exceptions.CantConvertException;

/**
 * Represents a <code>Json</code> object, "{}".
 */
public interface JsonObject extends JsonObjectOrArray, Iterable<Map.Entry<String, Object>> {

    /**
     * Puts an element at the specified key, without parsing this
     * key as a <code>JsonPath</code>.
     * <p>
     * If the element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If the element to add is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em>, it will be cloned. 
     * Doing so, we can make sure the <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If the element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to an <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of objects that will be converted to a 
     * <code>JsonArray</code> instead of a <code>JsonObject</code>, if 
     * no conversion interface is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     */
    public JsonObject putNoKeyParsing(String key, Object element);

    /**
     * Puts an element at the specified key, without parsing this
     * key as a <code>JsonPath</code>.
     * <p>
     * If the element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If the element to add is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em>, it will be cloned. 
     * Doing so, we can make sure the <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If the element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to an <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of objects that will be converted to a 
     * <code>JsonArray</code> instead of a <code>JsonObject</code>, if 
     * no conversion interface is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     * 
     * @param clone if <code>true</code>, and the element to add is a
     * <code>JsonObject</code> or <code>JsonArray</code>, a clone will be made 
     * before being added. If that case, any modification to the 
     * original element won't affect the added one, 
     * and vice-versa. If the element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>JsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonObject putNoKeyParsing(String key, Object element, boolean clone);

    /**
     * Merges all the specified Map elements in the JsonObject. The keys
     * are parsed as <code>JsonPaths</code>.
     * Overwrites existing elements at the specified JsonPaths.
     * <p>
     * Note that the JsonObject and JsonArray objects from the source 
     * will be added as is, so any modification to them WILL affect 
     * the added elements, and vise-versa. There is an exception though :
     * if the element to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>JsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If an element to is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em>, it will be cloned. 
     * Doing so, we can make sure the <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If an element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to an <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of objects that will be converted to a 
     * <code>JsonArray</code> instead of a <code>JsonObject</code>, if 
     * no conversion interface is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     */
    public JsonObject merge(Map<String, ?> map);

    /**
     * Merges all the specified Map elements in the JsonObject. The keys
     * are parsed as <code>JsonPaths</code>.
     * Overwrites existing elements at the specified JsonPaths.
     * <p>
     * If an element to is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em>, it will be cloned. 
     * Doing so, we can make sure the <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If an element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to an <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of objects that will be converted to a 
     * <code>JsonArray</code> instead of a <code>JsonObject</code>, if 
     * no conversion interface is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     * 
     * @param clone if <code>true</code>, a clone of any 
     * <code>JsonObject</code> or <code>JsonArray</code> will be made 
     * before being added. If that case, any modification to the 
     * original elements won't affect the added elements, 
     * and vice-versa. If the element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>JsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonObject merge(Map<String, ?> map, boolean clone);

    /**
     * Merges the specified <code>JsonObject</code> properties in the current
     * object. Overwrites elements of the same JsonPaths.
     * <p>
     * Note that the elements from the source are added as is, 
     * so any modification to them WILL affect the added element,
     * and vise-versa. There is an exception though :
     * if an element to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>JsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     */
    public JsonObject merge(JsonObject jsonObj);

    /**
     * Merges the specified <code>JsonObject</code> properties in the current
     * object. Overwrites elements of the same JsonPaths.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * <code>JsonObject</code> will be made before being added.
     * If that case, any modification to the original object
     * won't affect the added element, and vice-versa. 
     * If an  element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>JsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonObject merge(JsonObject jsonObj, boolean clone);

    /**
     * Transforms the specifie3d object to <code>JsonObject</code> 
     * and merges its properties in the current
     * object. Overwrites elements of the same JsonPaths.
     */
    public JsonObject merge(ToJsonObjectConvertible jsonObj);

    /**
     * Removes a element from the object.
     * The key is used <em>as is</em>, without
     * being parsed as a <code>JsonPath</code>.
     */
    public JsonObject removeNoKeyParsing(String key);

    /**
     * Does the JsonObject contain an element at 
     * the specified key?
     * The key is considered <em>as is</em>, without
     * being parsed as a <code>JsonPath</code>.
     */
    public boolean isElementExistsNoKeyParsing(String key);

    /**
     * Gets an untyped Object.
     * 
     * @return the object or <code>null</code> if not found. This object
     * will inevitably be of a type managed by <code>JsonObject</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObjectNoKeyParsing(String jsonPath);

    /**
     * Gets an untyped Object.
     * @return the object or the specified 
     * <code>defaultElement</code> if not found. This object
     * will inevitably be of a type managed by <code>JsonObject</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObjectNoKeyParsing(String jsonPath, Object defaultElement);

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the object or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the object or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectNoKeyParsing(String jsonPath, JsonObject defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the element or an empty
     * <code>JsonObject</code> if not found or if <code>null</code>.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectOrEmptyNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * 
     * @return the object or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * 
     * @return the object or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayNoKeyParsing(String jsonPath, JsonArray defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * @return the element or an empty
     * <code>JsonArray</code> if not found or if <code>null</code>.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayOrEmptyNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public String getStringNoKeyParsing(String jsonPath);

    /**
     * Gets an element as <code>String</code>.
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     */
    public String getStringNoKeyParsing(String jsonPath, String defaultElement);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getIntegerNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getIntegerNoKeyParsing(String jsonPath, Integer defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLongNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLongNoKeyParsing(String jsonPath, Long defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloatNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloatNoKeyParsing(String jsonPath, Float defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDoubleNoKeyParsing(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDoubleNoKeyParsing(String jsonPath, Double defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code>.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * <p>
     * The signature of this method is different than from the other
     * "get" methods with a "parseJsonPath" parameter since we have to
     * differenciate it from the overload that takes a Boolean
     * default value.
     * <p>
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBooleanNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code>.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBooleanNoKeyParsing(String key, Boolean defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code>.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimalNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code>.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimalNoKeyParsing(String key, BigDecimal defaultElement) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element.
     * 
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64StringNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element.
     * 
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64StringNoKeyParsing(String key, byte[] defaultElement) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDateNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDateNoKeyParsing(String key, Date defaultElement) throws CantConvertException;

    /**
     * Gets an Instant from a <code>ISO 8601</code> date element.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getInstantNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets an Instant from a <code>ISO 8601</code> date element.
     * <p>
     * The <code>key</code> will be used <em>as is</em>, it won't be parsed
     * as a <code>JsonPath</code>.
     * </p>
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getInstantNoKeyParsing(String key, Instant defaultElement) throws CantConvertException;


    /**
     * Gets the first value (as JsonObject) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObjectNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as JsonObject) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObjectNoKeyParsing(String key, JsonObject defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as JsonArray) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArrayNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as JsonArray) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArrayNoKeyParsing(String key, JsonArray defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as String) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     */
    public String getArrayFirstStringNoKeyParsing(String key);

    /**
     * Gets the first value (as String) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     */
    public String getArrayFirstStringNoKeyParsing(String key, String defaultElement);

    /**
     * Gets the first value (as Integer) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstIntegerNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Integer) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the arr
     * ay or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstIntegerNoKeyParsing(String key, Integer defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Long) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLongNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Long) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLongNoKeyParsing(String key, Long defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Double) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDoubleNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Double) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDoubleNoKeyParsing(String key, Double defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Float) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloatNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Float) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloatNoKeyParsing(String key, Float defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Boolean) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBooleanNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Boolean) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBooleanNoKeyParsing(String key, Boolean defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as BigDecimal) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimalNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as BigDecimal) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimalNoKeyParsing(String key, BigDecimal defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as byte[]) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64StringNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as byte[]) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64StringNoKeyParsing(String key, byte[] defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Date) of a <code>JsonArray</code> element
     * . The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDateNoKeyParsing(String key) throws CantConvertException;

    /**
     * Gets the first value (as Date) of a <code>JsonArray</code> element. 
     * The key will be used <em>as is</em> without being parsed
     * as a <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDateNoKeyParsing(String key, Date defaultElement) throws CantConvertException;

    /**
     * Converts the <code>JsonObject</code> to an instance of
     * the specified <code>T</code> type.
     * <p>
     * This uses {@link org.spincast.core.json.JsonManager#fromString(String, Class) JsonManager#fromJsonString}
     * and may throw an exception if it is unable to do the conversion.
     * </p>
     */
    public <T> T convert(Class<T> clazz);

    /**
     * Converts the <code>JsonObject</code> to a plain Map. 
     * All <code>JsonObject</code> children will be converted to
     * Maps and all <code>JsonArray</code> children will be converted to
     * Lists.
     */
    public Map<String, Object> convertToPlainMap();

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>String</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToStringNoKeyParsing(String key);

    /**
     * Validates that the element at the specifiedkey (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Integer</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToIntegerNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Long</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToLongNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Float</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToFloatNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Double</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDoubleNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Boolean</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBooleanNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>BigDecimal</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBigDecimalNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>base 64 String</code> representing 
     * a byte array, or can be converted and retrieved as one.
     */
    public boolean isCanBeConvertedToByteArrayNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>Date</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDateNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>JsonObject</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonObjectNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code>, of type <code>JsonArray</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonArrayNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>String</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeStringNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>Integer</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeIntegerNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>Long</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeLongNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>Float</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeFloatNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>Double</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDoubleNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>)> exists and
     * is currently <code>null</code> or of type <code>Boolean</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBooleanNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>BigDecimal</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBigDecimalNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>byte[]</code>, without requiring
     * any conversion.
     * 
     * @param acceptBase64StringToo if <code>true</code>, then a valid base 64 String
     * will also be accepted.
     */
    public boolean isOfTypeByteArrayNoKeyParsing(String key, boolean acceptBase64StringToo);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>Date</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDateNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>JsonObject</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonObjectNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is currently <code>null</code> or of type <code>JsonArray</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonArrayNoKeyParsing(String key);

    /**
     * Validates that the element at the specified key (not parsed
     * as a <code>JsonPath</code>) exists and
     * is <code>null</code>.
     */
    public boolean isNullNoKeyParsing(String key);

    /**
     * Compares the current <code>JsonObject</code> to the specified one
     * and returns <code>true</code> if they are equivalent. To be equivalent,
     * all their elements must be so too.
     * <p>
     * An element is equivalent to the other if they can be converted to the¸
     * same type, and then if they are equals.
     * <p>
     * For example, the <code>String</code> "123" is equivalent to 
     * <code>new BigDecimal("123")</code> or to <code>123L</code>.
     */
    public boolean isEquivalentTo(JsonObject other);

    /**
     * Deep copy of the <code>JsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * Note that if the current object is immutable and
     * the <code>mutable</code> parameter is set to <code>false</code>,
     * then the current object will be returned as is, since no
     * cloning is required.
     * 
     * @param mutable if <code>true</code> the resulting
     * object and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    @Override
    public JsonObject clone(boolean mutable);

}

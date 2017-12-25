package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;

import org.spincast.core.exceptions.CantConvertException;

/**
 * Base interface for <code>JsonObject</code> and
 * <code>JsonArray</code>.
 */
public interface JsonObjectOrArray {

    /**
     * Transforms the element at the specified <code>JsonPath</code> using
     * the transformer.
     */
    public void transform(String jsonPath, ElementTransformer transformer);

    /**
     * Is this object/array mutable?
     */
    public boolean isMutable();

    /**
     * Clears all elements.
     */
    public JsonObjectOrArray clear();

    /**
     * The size of the object.
     */
    public int size();

    /**
     * The <code>Json</code> string representation
     * of the object.
     */
    public String toJsonString();

    /**
     * The <code>Json</code> string representation
     * of the object.
     * 
     * @param pretty if <code>true</code>, the generated String will 
     * be formatted.
     */
    public String toJsonString(boolean pretty);

    /**
     * Clone the object.
     * 
     * @param mutable if <code>false</code>, the resulting
     * object will be immutable.
     */
    public JsonObjectOrArray clone(boolean mutable);

    /**
     * Transforms all the elements of the object, using the specified
     * transformer. This transformation is not recursive.
     */
    public void transformAll(ElementTransformer transformer);

    /**
     * Transforms all the elements of the object, using the specified
     * transformer.
     * 
     * @param recursive if  <code>true</code>,
     * then all children elements will also be transformed,
     * recursively.
     */
    public void transformAll(ElementTransformer transformer, boolean recursive);

    /**
     * Trims the element at the specified <code>JsonPath</code> if 
     * it's of type <code>String</code>.
     */
    public void trim(String jsonPath);

    /**
     * Trims all the elements of the object that are of type
     * <code>String</code>. This transformation is not recursive.
     * 
     */
    public void trimAll();

    /**
     * Trims all the elements of the object that are of type
     * String.
     * 
     * @param recursive if  <code>true</code>,
     * then all children elements will also be trimmed,
     * recursively.
     */
    public void trimAll(boolean recursive);

    /**
     * Does the object contain an element at
     * the <code>JsonPath</code> position (even if 
     * <code>null</code>)?
     */
    public boolean isElementExists(String jsonPath);

    /**
     * Removes an element at <code>JsonPath</code>.
     * 
     * @return the current object (fluent style).
     */
    public JsonObjectOrArray remove(String jsonPath);

    /**
     * Puts an element at the specified <code>JsonPath</code>.
     * Overwrites any existing element at that <code>JsonPath</code>.
     * <p>
     * The required hierarchy will be created, if required, to
     * support the <code>JsonPath</code>.
     * <p>
     * If the object to add is not of a JsonObject's native type,
     * the object is converted to a JsonObject or a
     * JsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
     * <p>
     * If the element to add is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em> then 
     * it will be cloned. 
     * Doing so, we can make sure that the current object is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If the element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to a <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of object that will be converted to a 
     * JsonArray instead of a JsonObject, if no conversion interface
     * is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     * </p>
     */
    public JsonObjectOrArray put(String jsonPath, Object element);

    /**
     * Puts an element at the specified <code>JsonPath</code>.
     * Overwrites any existing element at that <code>JsonPath</code>.
     * <p>
     * The required hierarchy will be created, if required, to
     * support the <code>JsonPath</code>.
     * <p>
     * If the object to add is not of a JsonObject's native type,
     * the object is converted to a JsonObject or a
     * JsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
     * <p>
     * If the element to add is a <code>JsonObject</code> or 
     * a <code>JsonArray</code> and is <em>immutable</em> then 
     * it will be cloned. 
     * Doing so, we can make sure that the current object is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * <p>
     * If the element implements <code>ToJsonObjectConvertible</code>, it
     * will be converted to a <code>JsonObject</code> using the associated
     * conversion method. If it implements <code>ToJsonArrayConvertible</code>, it
     * will be converted to a <code>JsonArray</code> using the associated
     * conversion method.
     * <p>
     * Those are the types of object that will be converted to a 
     * JsonArray instead of a JsonObject, if no conversion interface
     * is implemented :
     * <ul>
     * <li>
     * A Collection
     * </li>
     * <li>
     * An array
     * </li>
     * </ul>
     * </p>
     * 
     * @param clone if <code>true</code>, and the element to add is a
     * <code>JsonObject</code> or <code>JsonArray</code>, a clone will be made 
     * before being added. If that case, any modification to the 
     * original element won't affect the added one, 
     * and vice-versa. If the element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure the current object is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonObjectOrArray put(String jsonPath, Object element, boolean clone);

    /**
     * Gets an element as <code>JsonObject</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObject(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObject(String jsonPath, JsonObject defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or an empty
     * <code>JsonObject</code> if not found or if <code>null</code>. The empty
     * <code>JsonObject</code> will <em>not</em> be added to the specified
     * path.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectOrEmpty(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @param addIfDoesntExist if <code>true</code> and the element doesn't exist,
     * the created empty <code>JsonObject</code> will be added to the specified
     * <code>JsonPath</code>.
     * 
     * @return the element or an empty
     * <code>JsonObject</code> if not found or if <code>null</code>.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectOrEmpty(String jsonPath, boolean addIfDoesntExist) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArray(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArray(String jsonPath, JsonArray defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or an empty
     * <code>JsonArray</code> if not found or if <code>null</code>. The empty
     * <code>JsonArray</code> will <em>not</em> be added to the specified
     * path.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayOrEmpty(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @param addIfDoesntExist if <code>true</code> and the element doesn't exist,
     * the created empty <code>JsonArray</code> will be added to the specified
     * <code>JsonPath</code>.
     * 
     * @return the element or an empty
     * <code>JsonArray</code> if not found or if <code>null</code>.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayOrEmpty(String jsonPath, boolean addIfDoesntExist) throws CantConvertException;

    /**
     * Gets an element as <code>String</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public String getString(String jsonPath);

    /**
     * Gets an element as <code>String</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     */
    public String getString(String jsonPath, String defaultElement);

    /**
     * Gets an element as <code>Integer</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Integer</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(String jsonPath, Integer defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(String jsonPath, Long defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(String jsonPath, Float defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(String jsonPath, Double defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(String jsonPath, Boolean defaultElement) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(String jsonPath) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code> using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(String jsonPath, BigDecimal defaultElement) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(String jsonPath) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(String jsonPath, byte[] defaultElement) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(String jsonPath) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element using the
     * specified <code>JsonPath</code>.
     * 
     * @return the element or the specified 
     * <code>defaultElement</code> if not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(String jsonPath, Date defaultElement) throws CantConvertException;

    /**
     * Gets an untyped Object using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or <code>null</code> if not found. This object
     * will necessarily be of a type managed by <code>JsonObjectOrArray</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObject(String jsonPath);

    /**
     * Gets an untyped Object using the
     * specified <code>JsonPath</code>.
     * 
     * @return the object or the specified 
     * <code>defaultElement</code> if not found. This object
     * will necessarily be of a type managed by <code>JsonObjectOrArray</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObject(String jsonPath, Object defaultElement);

    /**
     * Gets the first value (as JsonObject) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObject(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as JsonObject) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObject(String jsonPath, JsonObject defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as JsonArray) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArray(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as JsonArray) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArray(String jsonPath, JsonArray defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as String) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     */
    public String getArrayFirstString(String jsonPath);

    /**
     * Gets the first value (as String) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     */
    public String getArrayFirstString(String jsonPath, String defaultElement);

    /**
     * Gets the first value (as Integer) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Integer) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(String jsonPath, Integer defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Long) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Long) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(String jsonPath, Long defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Double) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Double) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(String jsonPath, Double defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Float) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Float) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(String jsonPath, Float defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Boolean) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Boolean) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(String jsonPath, Boolean defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as BigDecimal) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as BigDecimal) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(String jsonPath, BigDecimal defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as byte[]) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as byte[]) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(String jsonPath, byte[] defaultElement) throws CantConvertException;

    /**
     * Gets the first value (as Date) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(String jsonPath) throws CantConvertException;

    /**
     * Gets the first value (as Date) of a <code>JsonArray</code> property
     * of the object, using the <code>JsonPath</code> to find the array.
     * 
     * @return the value of the property or the specified 
     * <code>defaultElement</code> if the array or
     * the first element are not found.
     * 
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(String jsonPath, Date defaultElement) throws CantConvertException;

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>String</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToString(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Integer</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToInteger(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Long</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToLong(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Float</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToFloat(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Double</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDouble(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Boolean</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBoolean(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>BigDecimal</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBigDecimal(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>base 64 String</code> representing 
     * a byte array, or can be converted and retrieved as one.
     */
    public boolean isCanBeConvertedToByteArray(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>Date</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDate(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>JsonObject</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonObject(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code>, of type <code>JsonArray</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonArray(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>String</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeString(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Integer</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeInteger(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Long</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeLong(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Float</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeFloat(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Double</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDouble(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Boolean</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBoolean(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>BigDecimal</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBigDecimal(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>byte[]</code>, without requiring
     * any conversion.
     * 
     * @param acceptBase64StringToo if <code>true</code>, then a valid base 64 String
     * will also be accepted.
     */
    public boolean isOfTypeByteArray(String jsonPath, boolean acceptBase64StringToo);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>Date</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDate(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>JsonObject</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonObject(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>JsonArray</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonArray(String jsonPath);

    /**
     * Validates that the value at the specified <code>JsonPath</code> exists and
     * is <code>null</code>.
     */
    public boolean isNull(String jsonPath);

}

package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents a <code>Json</code> array "[]", and makes it easier
 * to get its elements in a typed way.
 */
public interface IJsonArray extends Iterable<Object> {

    /**
     * Adds a String.
     */
    public IJsonArray add(String value);

    /**
     * Adds a Integer.
     */
    public IJsonArray add(Integer value);

    /**
     * Adds a Long.
     */
    public IJsonArray add(Long value);

    /**
     * Adds a Float.
     */
    public IJsonArray add(Float value);

    /**
     * Adds a Double.
     */
    public IJsonArray add(Double value);

    /**
     * Adds a Boolean.
     */
    public IJsonArray add(Boolean value);

    /**
     * Adds a BigDecimal.
     */
    public IJsonArray add(BigDecimal value);

    /**
     * Adds a byte[].
     */
    public IJsonArray add(byte[] value);

    /**
     * Adds a Date.
     */
    public IJsonArray add(Date value);

    /**
     * Adds a IJsonObject.
     * Note that the object is added as is, so any modification
     * to the original object
     * WILL affect the added element, and vice-versa. 
     * There is an exception though :
     * if the <code>IJsonObject</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray add(IJsonObject value);

    /**
     * Adds a IJsonObject.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * object will be made before being added.
     * If that case, any modification to the original object
     * won't affect the added element, and vice-versa.
     * If the <code>IJsonObject</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray add(IJsonObject value, boolean clone);

    /**
     * Adds a IJsonArray.
     * Note that the object is added as is, so any modification
     * to the original object
     * WILL affect the added element, and vice-versa.
     * There is an exception though :
     * if the <code>IJsonArray</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray add(IJsonArray value);

    /**
     * Adds a IJsonArray.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * object will be made before being added.
     * If that case, any modification to the original <code>IJsonArray</code>
     * won't affect the added element, and vice-versa.
     * If the <code>IJsonArray</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray add(IJsonArray value, boolean clone);

    /**
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification of the original object won't
     * affect the added element, and vice-versa.
     * <p>
     * If the element to add is a <code>IJsonObject</code> or 
     * a <code>IJsonArray</code> and is <em>immutable</em> then 
     * it will be cloned. 
     * Doing so, we can make sure a <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     * <p>
     * Those are the types of objects that will be converted to a 
     * IJsonArray instead of a IJsonObject :
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
    public IJsonArray addConvert(Object value);

    /**
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
     * <p>
     * Those are the types of object that will be converted to a 
     * IJsonArray instead of a IJsonObject :
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
     * <code>IJsonObject</code> or <code>IJsonArray</code>, a clone will be made 
     * before being added. If that case, any modification to the 
     * original element won't affect the added one, 
     * and vice-versa. If the element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray addConvert(Object value, boolean clone);

    /**
     * Removes an element at the specified position.
     * Any subsequent elements are shift to the left.
     * If the position is invalid, nothing is do.
     */
    public IJsonArray remove(int index);

    /**
     * Clears all elements.
     */
    public IJsonArray clear();

    /**
     * The array size.
     */
    public int size();

    /**
     * The <code>Json</code> string representation
     * of the array.
     */
    public String toJsonString();

    /**
     * The <code>Json</code> string representation
     * of the array.
     * 
     * @param pretty if <code>true</code>, the generated String will be formatted.
     */
    public String toJsonString(boolean pretty);

    /**
     * Gets an element as <code>IJsonObject</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObject(int index);

    /**
     * Gets an element as <code>IJsonObject</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObject(int index, IJsonObject defaultValue);

    /**
     * Gets an element as <code>IJsonObject</code>.
     * 
     * @return the element or an empty <code>IJsonObject</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObjectOrEmpty(int index);

    /**
     * Gets an element as <code>IJsonArray</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArray(int index);

    /**
     * Gets an element as <code>IJsonArray</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArray(int index, IJsonArray defaultValue);

    /**
     * Gets an element as <code>IJsonArray</code>.
     * 
     * @return the value of the property or an empty
     * <code>IJsonArray</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArrayOrEmpty(int index);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(int index);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(int index, String defaultValue);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index, Integer defaultValue);

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index);

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index, Long defaultValue);

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index);

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index, Double defaultValue);

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index);

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index, Float defaultValue);

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index);

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index, Boolean defaultValue);

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index);

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index, BigDecimal defaultValue);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index, byte[] defaultValue);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index, Date defaultValue);

    /**
     * Deep copy of the <code>IJsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * The resulting <code>IJsonArray</code> is mutable.
     * </p>
     */
    public IJsonArray clone();

    /**
     * Deep copy of the <code>IJsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * 
     * @param mutable if <code>false</code>, the resulting
     * <code>IJsonArray</code> and all its children 
     * will be immutable.
     */
    public IJsonArray clone(boolean mutable);

    /**
     * Converts the <code>IJsonArray</code> to a plain <code>List&lt;Object&gt;</code>. 
     * All <code>IJsonObject</code> elements will be converted to
     * Maps and all <code>IJsonArray</code> elements will be converted to
     * Lists.
     * <p>
     * This list is always a new instance and is always mutable.
     * </p>
     */
    public List<Object> convertToPlainList();

    /**
     * Converts the IJsonArray to a <code>List&lt;String&gt;</code>.
     * The <code>toString()</code> method will be called on any non null
     * elements.
     * <p>
     * This list is always a new instance and is always mutable.
     * </p>
     */
    public List<String> convertToStringList();

}

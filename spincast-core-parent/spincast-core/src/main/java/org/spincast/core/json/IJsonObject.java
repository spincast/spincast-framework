package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Represents a <code>Json</code> object "{}", and makes it easier
 * to get its properties in a typed way.
 */
public interface IJsonObject extends Iterable<Map.Entry<String, Object>> {

    /**
     * Adds a String.
     */
    public IJsonObject put(String key, String value);

    /**
     * Adds a Integer.
     */
    public IJsonObject put(String key, Integer value);

    /**
     * Adds a Long.
     */
    public IJsonObject put(String key, Long value);

    /**
     * Adds a Float.
     */
    public IJsonObject put(String key, Float value);

    /**
     * Adds a Double.
     */
    public IJsonObject put(String key, Double value);

    /**
     * Adds a Boolean.
     */
    public IJsonObject put(String key, Boolean value);

    /**
     * Adds a BigDecimal.
     */
    public IJsonObject put(String key, BigDecimal value);

    /**
     * Adds a byte[].
     */
    public IJsonObject put(String key, byte[] value);

    /**
     * Adds a Date.
     */
    public IJsonObject put(String key, Date value);

    /**
     * Adds a IJsonObject.
     * Note that the reference is added as is, so any modification
     * to the original <code>IJsonObject</code>
     * WILL affect the added element. There is an exception though :
     * if the <code>IJsonObject</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject put(String key, IJsonObject value);

    /**
     * Adds a IJsonObject.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * <code>IJsonObject</code> will be made before being added.
     * If that case, any modification to the original <code>IJsonObject</code>
     * won't affect the added element, and vice-versa. 
     * If the <code>IJsonObject</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject put(String key, IJsonObject value, boolean clone);

    /**
     * Adds a IJsonArray.
     * Note that the reference is added as is, so any modification
     * to the original <code>IJsonArray</code>
     * WILL affect the added element, and vice-versa. 
     * There is an exception though :
     * if the <code>IJsonArray</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject put(String key, IJsonArray value);

    /**
     * Adds a IJsonArray.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * <code>IJsonArray</code> will be made before being added.
     * If that case, any modification to the original <code>IJsonArray</code>
     * won't affect the added element, and vice-versa.
     * If the <code>IJsonArray</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject put(String key, IJsonArray value, boolean clone);

    /**
     * If the object to add is not of a IJsonObject's native type,
     * the object is converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
     * <p>
     * If the element to add is a <code>IJsonObject</code> or 
     * a <code>IJsonArray</code> and is <em>immutable</em> then 
     * it will be cloned. 
     * Doing so, we can make sure a <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
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
     */
    public IJsonObject putConvert(String key, Object value);

    /**
     * If the object to add is not of a IJsonObject's native type,
     * the object is converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification of the original object won't
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
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject putConvert(String key, Object value, boolean clone);

    /**
     * Merges all the Map's entries in the IJsonObject.
     * Overwrites entries of the same keys.
     * <p>
     * Note that the IJsonObject and IJsonArray objects from the source 
     * will be added as is, so any modification to them WILL affect 
     * the added elements, and vise-versa. There is an exception though :
     * if the element to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     * <p>
     * Any none primitive (or BigDecimal) object will
     * be converted to a <code>IJsonObject</code> or a
     * <code>IJsonArray</code>.
     * </p>
     * <p>
     * Those are the types of object that will be converted to a 
     * <code>IJsonArray</code> instead of a <code>IJsonObject</code :
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
    public IJsonObject merge(Map<String, Object> map);

    /**
     * Merges all the Map's entries in the IJsonObject.
     * Overwrites entries of the same keys.
     * <p>
     * Any none primitive (or BigDecimal) object will
     * be converted to a <code>IJsonObject</code> or a
     * <code>IJsonArray</code>.
     * </p>
     * <p>
     * Those are the types of object that will be converted to a 
     * <code>IJsonArray</code> instead of a <code>IJsonObject</code> :
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
     * @param clone if <code>true</code>, a clone of any 
     * <code>IJsonObject</code> or <code>IJsonArray</code> will be made 
     * before being added. If that case, any modification to the 
     * original elements won't affect the added elements, 
     * and vice-versa. If the element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject merge(Map<String, Object> map, boolean clone);

    /**
     * Merges the specified <code>IJsonObject</code> properties in the current
     * object. Overwrites entries of the same keys.
     * <p>
     * Note that the elements from the source are added as is, 
     * so any modification to them WILL affect the added element,
     * and vise-versa. There is an exception though :
     * if an element to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     */
    public IJsonObject merge(IJsonObject jsonObj);

    /**
     * Merges the specified <code>IJsonObject</code> properties in the current
     * object. Overwrites entries of the same keys.
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * <code>IJsonObject</code> will be made before being added.
     * If that case, any modification to the original object
     * won't affect the added element, and vice-versa. 
     * If an  element is <em>immutable</em> then it will 
     * always be cloned. Doing so, we can make sure a 
     * <code>IJsonObject</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonObject merge(IJsonObject jsonObj, boolean clone);

    /**
     * Removes a property from the object.
     */
    public IJsonObject remove(String key);

    /**
     * Removes all properties from the object.
     */
    public IJsonObject removeAll();

    /**
     * Does the object contain the specified key?
     */
    public boolean isKeyExists(String key);

    /**
     * Gets a property as <code>IJsonObject</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObject(String key);

    /**
     * Gets a property as <code>IJsonObject</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObject(String key, IJsonObject defaultValue);

    /**
     * Gets a property as <code>IJsonObject</code>.
     * 
     * @return the value of the property or an empty
     * <code>IJsonObject</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getJsonObjectOrEmpty(String key);

    /**
     * Gets a property as <code>IJsonArray</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArray(String key);

    /**
     * Gets a property as <code>IJsonArray</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArray(String key, IJsonArray defaultValue);

    /**
     * Gets a property as <code>IJsonArray</code>.
     * 
     * @return the value of the property or an empty
     * <code>IJsonArray</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArrayOrEmpty(String key);

    /**
     * Gets a property as <code>String</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(String key);

    /**
     * Gets a property as <code>String</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(String key, String defaultValue);

    /**
     * Gets a property as <code>Integer</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(String key);

    /**
     * Gets a property as <code>Integer</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(String key, Integer defaultValue);

    /**
     * Gets a property as <code>Long</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(String key);

    /**
     * Gets a property as <code>Long</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(String key, Long defaultValue);

    /**
     * Gets a property as <code>Float</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(String key);

    /**
     * Gets a property as <code>Float</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(String key, Float defaultValue);

    /**
     * Gets a property as <code>Double</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(String key);

    /**
     * Gets a property as <code>Double</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(String key, Double defaultValue);

    /**
     * Gets a property as <code>Boolean</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(String key);

    /**
     * Gets a property as <code>Boolean</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(String key, Boolean defaultValue);

    /**
     * Gets a property as <code>BigDecimal</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(String key);

    /**
     * Gets a property as <code>BigDecimal</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(String key);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(String key, byte[] defaultValue);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(String key);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(String key, Date defaultValue);

    /**
     * Gets the first value (as IJsonObject) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getArrayFirstJsonObject(String key);

    /**
     * Gets the first value (as IJsonObject) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getArrayFirstJsonObject(String key, IJsonObject defaultValue);

    /**
     * Gets the first value (as IJsonArray) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getArrayFirstJsonArray(String key);

    /**
     * Gets the first value (as IJsonArray) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getArrayFirstJsonArray(String key, IJsonArray defaultValue);

    /**
     * Gets the first value (as String) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(String key);

    /**
     * Gets the first value (as String) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(String key, String defaultValue);

    /**
     * Gets the first value (as Integer) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(String key);

    /**
     * Gets the first value (as Integer) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(String key, Integer defaultValue);

    /**
     * Gets the first value (as Long) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(String key);

    /**
     * Gets the first value (as Long) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(String key, Long defaultValue);

    /**
     * Gets the first value (as Double) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(String key);

    /**
     * Gets the first value (as Double) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(String key, Double defaultValue);

    /**
     * Gets the first value (as Float) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(String key);

    /**
     * Gets the first value (as Float) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(String key, Float defaultValue);

    /**
     * Gets the first value (as Boolean) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(String key);

    /**
     * Gets the first value (as Boolean) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(String key, Boolean defaultValue);

    /**
     * Gets the first value (as BigDecimal) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(String key);

    /**
     * Gets the first value (as BigDecimal) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(String key, BigDecimal defaultValue);

    /**
     * Gets the first value (as byte[]) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(String key);

    /**
     * Gets the first value (as byte[]) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(String key, byte[] defaultValue);

    /**
     * Gets the first value (as Date) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(String key);

    /**
     * Gets the first value (as Date) of a <code>IJsonArray</code> property
     * of the object.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(String key, Date defaultValue);

    /**
     * Gets the <code>Json</code> String representation of the object.
     */
    public String toJsonString();

    /**
     * Gets the <code>Json</code> String representation of the object.
     * 
     * @param pretty if <code>true</code>, the generated String will be formatted.
     */
    public String toJsonString(boolean pretty);

    /**
     * Deep copy of the <code>IJsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * The resulting <code>IJsonObject</code> is mutable.
     * </p>
     */
    public IJsonObject clone();

    /**
     * Deep copy of the <code>IJsonObject</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * 
     * @param mutable if <code>false</code>, the resulting
     * <code>IJsonObject</code> and all its children 
     * will be immutable.
     */
    public IJsonObject clone(boolean mutable);

    /**
     * Converts the <code>IJsonObject</code> to a plain Map. 
     * All <code>IJsonObject</code> children will be converted to
     * Maps and all <code>IJsonArray</code> children will be converted to
     * Lists.
     */
    public Map<String, Object> convertToPlainMap();

    /**
     * The number of properties on the object.
     */
    public int size();

}

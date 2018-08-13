package org.spincast.core.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.spincast.core.exceptions.CantConvertException;

/**
 * Represents a <code>Json</code> array, "[]".
 */
public interface JsonArray extends JsonObjectOrArray, Iterable<Object> {

    /**
     * Adds an object at the end of the array.
     * <p>
     * If the object to add is not of a native type,
     * then the object is converted before being added. 
     * Once the object is converted and added, a modification of the original 
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
    public JsonArray add(Object value);

    /**
     * Inserts an object at the specified index in the array.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added to fill up positionss up to the specified index!
     * <p>
     * Use {@link #set(int, Object) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * <p>
     * If the object to add is not of a native type,
     * then the object is converted before being added. 
     * Once the object is converted and added, a modification of the original 
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
    public JsonArray add(int index, Object value);

    /**
     * Adds an object at the end of the array.
     * <p>
     * If the object to add is not of a native type,
     * then the object is converted before being added. 
     * Once the object is converted and added, a modification of the original 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray add(Object value, boolean clone);

    /**
     * Adds an object at the specified index.
     * <p>
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added to fill positions up to the specified index!
     * <p>
     * Use {@link #set(int, Object, boolean) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * <p>
     * If the object to add is not of a native type,
     * then the object is converted before being added. 
     * Once the object is converted and added, a modification of the original 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray add(int index, Object value, boolean clone);

    /**
     * Adds all elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
    public JsonArray addAll(Collection<?> values);

    /**
     * Adds all elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray addAll(Collection<?> values, boolean clone);

    /**
     * Adds all elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
    public JsonArray addAll(Object[] values);

    /**
     * Adds elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray addAll(Object[] values, boolean clone);

    /**
     * Adds all elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
    public JsonArray addAll(JsonArray values);

    /**
     * Adds elements at the end of the array.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray addAll(JsonArray values, boolean clone);

    /**
     * Sets an object at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added to fill positions up to the specified index.
     * <p>
     * Use {@link #add(int, Object) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
    public JsonArray set(int index, Object value);

    /**
     * Sets an object at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added to fill positions up to the specified index.
     * <p>
     * Use {@link #add(int, Object) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * <p>
     * If an element to add is not of a native type,
     * then the element is converted before being added. 
     * Once the element is converted and added, a modification of the original 
     * object won't affect this element, and vice-versa.
     * <p>
     * If an element to add is a <code>JsonObject</code> or 
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
     * <code>JsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public JsonArray set(int index, Object value, boolean clone);

    /**
     * Removes an element at the specified index.
     * Any elements to the right are shift to the left.
     * If the index is invalid, nothing is done.
     */
    public JsonArray remove(int index);

    /**
     * Is there an element at the specified index? This returns
     * <code>true</code> even if the element is <code>null</code>.
     * <p>
     * This is a synonym of <code>index &gt;= 0 && index &lt; size()</code>
     */
    public boolean isElementExists(int index);

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObject(int index) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObject(int index, JsonObject defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>JsonObject</code>.
     * 
     * @return the element or an empty <code>JsonObject</code> if not found
     * or if <code>null</code>.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getJsonObjectOrEmpty(int index) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArray(int index) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArray(int index, JsonArray defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>JsonArray</code>.
     * 
     * @return the element or an empty
     * <code>JsonArray</code> if not found or if <code>null</code>.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getJsonArrayOrEmpty(int index) throws CantConvertException;

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public String getString(int index);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     */
    public String getString(int index, String defaultValue);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index) throws CantConvertException;

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index, Integer defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index) throws CantConvertException;

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index, Long defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index) throws CantConvertException;

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index, Float defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index) throws CantConvertException;

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index, Double defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index) throws CantConvertException;

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index, Boolean defaultValue) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index) throws CantConvertException;

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index, BigDecimal defaultValue) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index) throws CantConvertException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index, byte[] defaultValue) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index) throws CantConvertException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index, Date defaultValue) throws CantConvertException;

    /**
     * Gets an Instant from a <code>ISO 8601</code> date element.
     * 
     * @return the element or <code>null</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getInstant(int index) throws CantConvertException;

    /**
     * Gets an Instant from a <code>ISO 8601</code> date element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getInstant(int index, Instant defaultValue) throws CantConvertException;

    /**
     * Gets an element, untyped.
     * 
     * @return the object or <code>null</code> if not found. This object
     * will necessarly be of a type managed by <code>JsonArray</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObject(int index);

    /**
     * Gets an element, untyped.
     * 
     * @return the object or the specified 
     * <code>defaultValue</code> if not found. This object
     * will necessarly be of a type managed by <code>JsonArray</code>, since
     * an object of any other type is automatically converted when added.
     */
    public Object getObject(int index, Object defaultValue);

    /**
     * Gets the first element (as JsonObject) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObject(int index) throws CantConvertException;

    /**
     * Gets the first element (as JsonObject) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonObject getArrayFirstJsonObject(int index, JsonObject defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as JsonArray) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArray(int index) throws CantConvertException;

    /**
     * Gets the first element (as JsonArray) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public JsonArray getArrayFirstJsonArray(int index, JsonArray defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as String) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(int index) throws CantConvertException;

    /**
     * Gets the first element (as String) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(int index, String defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Integer) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(int index) throws CantConvertException;

    /**
     * Gets the first element (as Integer) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(int index, Integer defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Long) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(int index) throws CantConvertException;

    /**
     * Gets the first element (as Long) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(int index, Long defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Double) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(int index) throws CantConvertException;

    /**
     * Gets the first element (as Double) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(int index, Double defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Float) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(int index) throws CantConvertException;

    /**
     * Gets the first element (as Float) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(int index, Float defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Boolean) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(int index) throws CantConvertException;

    /**
     * Gets the first element (as Boolean) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(int index, Boolean defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as BigDecimal) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(int index) throws CantConvertException;

    /**
     * Gets the first element (as BigDecimal) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(int index, BigDecimal defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as byte[]) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(int index) throws CantConvertException;

    /**
     * Gets the first element (as byte[]) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(int index, byte[] defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Date) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(int index) throws CantConvertException;

    /**
     * Gets the first element (as Date) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(int index, Date defaultValue) throws CantConvertException;

    /**
     * Gets the first element (as Instant) of a <code>JsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getArrayFirstInstant(int index) throws CantConvertException;

    /**
     * Gets the first element (as Instant) of a <code>JsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * @throws CantConvertException if an existing element can't be converted to the
     * required type.
     */
    public Instant getArrayFirstInstant(int index, Instant defaultValue) throws CantConvertException;


    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>String</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToString(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Integer</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToInteger(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Long</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToLong(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Float</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToFloat(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Double</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDouble(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Boolean</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBoolean(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>BigDecimal</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToBigDecimal(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>base 64 String</code> representing 
     * a byte array, or can be converted and retrieved as one.
     */
    public boolean isCanBeConvertedToByteArray(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>Date</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToDate(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>JsonObject</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonObject(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code>, of type <code>JsonArray</code>, or can be
     * converted and retrieved as one.
     */
    public boolean isCanBeConvertedToJsonArray(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>String</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeString(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Integer</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeInteger(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Long</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeLong(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Float</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeFloat(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Double</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDouble(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Boolean</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBoolean(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>BigDecimal</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeBigDecimal(int index);

    /**
     * Validates that the element at the specified <code>JsonPath</code> exists and
     * is currently <code>null</code> or of type <code>byte[]</code>, without requiring
     * any conversion.
     * 
     * @param acceptBase64StringToo if <code>true</code>, then a valid base 64 String
     * will also be accepted.
     */
    public boolean isOfTypeByteArray(int index, boolean acceptBase64StringToo);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>Date</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeDate(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>JsonObject</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonObject(int index);

    /**
     * Validates that the element at the specified <code>index</code> exists and
     * is currently <code>null</code> or of type <code>JsonArray</code>, without requiring
     * any conversion.
     */
    public boolean isOfTypeJsonArray(int index);

    /**
     * Validates that there is an element at the specified <code>index</code> and
     * it is <code>null</code>.
     */
    public boolean isNull(int index);

    /**
     * Compares the current <code>JsonArray</code> to the specified one
     * and returns <code>true</code> if they are equivalent. To be equivalent,
     * all their elements must be so too.
     * <p>
     * An element is equivalent to the other if they can be converted to the¸
     * same type, and then if they are equals.
     * <p>
     * For example, the <code>String</code> "123" is equivalent to 
     * <code>new BigDecimal("123")</code> or to <code>123L</code>.
     */
    public boolean isEquivalentTo(JsonArray other);

    /**
     * Deep copy of the <code>JsonArray</code>, so any
     * modification to the original won't affect the
     * clone, and vice-versa.
     * <p>
     * Note that if the current array is immutable and
     * the <code>mutable</code> parameter is set to <code>false</code>,
     * then the current array will be returned as is since no
     * cloning is then required.
     * 
     * @param mutable if <code>true</code> the resulting
     * array and all its children will be mutable, otherwise
     * they will all be immutable.
     */
    @Override
    public JsonArray clone(boolean mutable);

    /**
     * Transforms the element at the given index, using the
     * specified <code>ElementTransformer</code>.
     */
    public void transform(int index, ElementTransformer transformer);

    /**
     * Trims the element, if it's of type <code>String</code>.
     */
    public void trim(int index);

    /**
     * Converts the JsonArray to a <code>List&lt;String&gt;</code>.
     * To do so, the <code>toString()</code> method will be called 
     * on any non null element.
     * <p>
     * This list is always a new instance and is mutable.
     */
    public List<String> convertToStringList();

    /**
     * Converts the <code>JsonArray</code> to a plain <code>List&lt;Object&gt;</code>. 
     * All <code>JsonObject</code> elements will be converted to
     * Maps and all <code>JsonArray</code> elements will be converted to
     * Lists.
     * <p>
     * This list is always a new instance and is mutable.
     */
    public List<Object> convertToPlainList();

}

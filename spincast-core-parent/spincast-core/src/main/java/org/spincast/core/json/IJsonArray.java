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
     * Adds a String at the end of the array.
     */
    public IJsonArray add(String value);

    /**
     * Inserts a String at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, String) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, String value);

    /**
     * Sets a String at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, String) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, String value);

    /**
     * Adds a Integer at the end of the array.
     */
    public IJsonArray add(Integer value);

    /**
     * Inserts a Integer at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Integer) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Integer value);

    /**
     * Sets a Integer at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Integer) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Integer value);

    /**
     * Adds a Long at the end of the array.
     */
    public IJsonArray add(Long value);

    /**
     * Inserts a Long at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Long) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Long value);

    /**
     * Sets a Long at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Long) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Long value);

    /**
     * Adds a Float at the end of the array.
     */
    public IJsonArray add(Float value);

    /**
     * Inserts a Float at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Float) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Float value);

    /**
     * Sets a Float at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Float) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Float value);

    /**
     * Adds a Double at the end of the array.
     */
    public IJsonArray add(Double value);

    /**
     * Inserts a Double at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Double) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Double value);

    /**
     * Sets a Double at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Double) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Double value);

    /**
     * Adds a Boolean at the end of the array.
     */
    public IJsonArray add(Boolean value);

    /**
     * Inserts a Boolean at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Boolean) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Boolean value);

    /**
     * Sets a Boolean at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Boolean) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Boolean value);

    /**
     * Adds a BigDecimal at the end of the array.
     */
    public IJsonArray add(BigDecimal value);

    /**
     * Inserts a BigDecimal at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, BigDecimal) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, BigDecimal value);

    /**
     * Sets a BigDecimal at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, BigDecimal) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, BigDecimal value);

    /**
     * Adds a byte[] at the end of the array.
     */
    public IJsonArray add(byte[] value);

    /**
     * Inserts a byte[] at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, byte[]) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, byte[] value);

    /**
     * Sets a byte[] at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, byte[]) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, byte[] value);

    /**
     * Adds a Date at the end of the array.
     */
    public IJsonArray add(Date value);

    /**
     * Inserts a Date at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, Date) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, Date value);

    /**
     * Sets a Date at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Date) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     */
    public IJsonArray set(int index, Date value);

    /**
     * Adds a IJsonObject at the end of the array.
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
     * Inserts a IJsonObject at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, IJsonObject) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, IJsonObject value);

    /**
     * Sets a IJsonObject at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, IJsonObject) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     * <p>
     * Note that the object is added as is, so any modification
     * to the original object
     * WILL affect the added element, and vice-versa. 
     * There is an exception though :
     * if the <code>IJsonObject</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     */
    public IJsonArray set(int index, IJsonObject value);

    /**
     * Adds a IJsonObject at the end of the array.
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
     * Inserts a IJsonObject at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, IJsonObject, boolean) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
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
    public IJsonArray add(int index, IJsonObject value, boolean clone);

    /**
     * Sets a IJsonObject at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, IJsonObject, boolean) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
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
    public IJsonArray set(int index, IJsonObject value, boolean clone);

    /**
     * Adds a IJsonArray at the end of the array.
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
     * Inserts a IJsonArray at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, IJsonArray) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     */
    public IJsonArray add(int index, IJsonArray value);

    /**
     * Sets a IJsonArray at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, IJsonArray) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     * <p>
     * Note that the object is added as is, so any modification
     * to the original object
     * WILL affect the added element, and vice-versa.
     * There is an exception though :
     * if the <code>IJsonArray</code> to add is <em>immutable</em>
     * then it will always be cloned. Doing so, we can make sure a
     * <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     * </p>
     */
    public IJsonArray set(int index, IJsonArray value);

    /**
     * Adds a IJsonArray at the end of the array.
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
     * Inserts a IJsonArray at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #set(int, IJsonArray, boolean) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     * 
     * @param clone if <code>true</code>, a clone of the original 
     * object will be made before being added.
     * If that case, any modification to the original object
     * won't affect the added element, and vice-versa.
     * If the <code>IJsonArray</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray add(int index, IJsonArray value, boolean clone);

    /**
     * Sets a IJsonArray at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, IJsonArray, boolean) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     * @param clone if <code>true</code>, a clone of the original 
     * object will be made before being added.
     * If that case, any modification to the original <code>IJsonArray</code>
     * won't affect the added element, and vice-versa.
     * If the <code>IJsonArray</code> 
     * to add is <em>immutable</em> then it will always be cloned. 
     * Doing so, we can make sure a <code>IJsonArray</code> is always 
     * <em>fully</em> mutable or <em>fully</em> immutable.
     */
    public IJsonArray set(int index, IJsonArray value, boolean clone);

    /**
     * Adds an object at the end of the array.
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
     * Inserts an object at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #setConvert(int, Object) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     * <p>
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification of the original object won't
     * affect the added element, and vice-versa.
     * </p>
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
    public IJsonArray addConvert(int index, Object value);

    /**
     * Sets an object at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Object) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     * <p>
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification of the original object won't
     * affect the added element, and vice-versa.
     * </p>
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
    public IJsonArray setConvert(int index, Object value);

    /**
     * Adds an object at the end of the array.
     * 
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
     * Adds an object at the specified index.
     * Any existing elements starting at this index are
     * pushed to the right.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #setConvert(int, Object, boolean) set} instead if you want
     * to <em>replace</em> the element at the specified index.
     * </p>
     * <p>
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
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
    public IJsonArray addConvert(int index, Object value, boolean clone);

    /**
     * Sets an object at the specified index.
     * If there is an element at this index, it is 
     * <em>overwritten</em>.
     * <p>
     * If the specified index is greater than the current
     * end of the array, <code>null</code> elements are
     * added up to the specified index.
     * </p>
     * <p>
     * Use {@link #add(int, Object, boolean) add} instead if you want
     * to <em>insert</em> the element at the specified index without
     * overwritting any existing element.
     * </p>
     * <p>
     * If the object to add is not of a IJsonObject's native type,
     * then the object will be converted to a IJsonObject or a
     * IJsonArray before being added. Once the object is converted
     * and added, a modification to the original object won't
     * affect the added element, and vice-versa.
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
    public IJsonArray setConvert(int index, Object value, boolean clone);

    /**
     * Removes an element at the specified position.
     * Any subsequent elements are shift to the left.
     * If the position is invalid, nothing is done.
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
     * @return the element or <code>null</code> if not found.
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
     * @return the element or <code>null</code> if not found.
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
     * @return the element or an empty
     * <code>IJsonArray</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getJsonArrayOrEmpty(int index);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(int index);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getString(int index, String defaultValue);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getInteger(int index, Integer defaultValue);

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index);

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getLong(int index, Long defaultValue);

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index);

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getDouble(int index, Double defaultValue);

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index);

    /**
     * Gets an element as <code>Float</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getFloat(int index, Float defaultValue);

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index);

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getBoolean(int index, Boolean defaultValue);

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index);

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getBigDecimal(int index, BigDecimal defaultValue);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getBytesFromBase64String(int index, byte[] defaultValue);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the element or <code>null</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getDate(int index, Date defaultValue);

    /**
     * Gets the first value (as IJsonObject) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getArrayFirstJsonObject(int index);

    /**
     * Gets the first value (as IJsonObject) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonObject getArrayFirstJsonObject(int index, IJsonObject defaultValue);

    /**
     * Gets the first value (as IJsonArray) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getArrayFirstJsonArray(int index);

    /**
     * Gets the first value (as IJsonArray) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public IJsonArray getArrayFirstJsonArray(int index, IJsonArray defaultValue);

    /**
     * Gets the first value (as String) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(int index);

    /**
     * Gets the first value (as String) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public String getArrayFirstString(int index, String defaultValue);

    /**
     * Gets the first value (as Integer) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(int index);

    /**
     * Gets the first value (as Integer) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Integer getArrayFirstInteger(int index, Integer defaultValue);

    /**
     * Gets the first value (as Long) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(int index);

    /**
     * Gets the first value (as Long) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Long getArrayFirstLong(int index, Long defaultValue);

    /**
     * Gets the first value (as Double) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(int index);

    /**
     * Gets the first value (as Double) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Double getArrayFirstDouble(int index, Double defaultValue);

    /**
     * Gets the first value (as Float) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(int index);

    /**
     * Gets the first value (as Float) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Float getArrayFirstFloat(int index, Float defaultValue);

    /**
     * Gets the first value (as Boolean) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(int index);

    /**
     * Gets the first value (as Boolean) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Boolean getArrayFirstBoolean(int index, Boolean defaultValue);

    /**
     * Gets the first value (as BigDecimal) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(int index);

    /**
     * Gets the first value (as BigDecimal) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public BigDecimal getArrayFirstBigDecimal(int index, BigDecimal defaultValue);

    /**
     * Gets the first value (as byte[]) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(int index);

    /**
     * Gets the first value (as byte[]) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public byte[] getArrayFirstBytesFromBase64String(int index, byte[] defaultValue);

    /**
     * Gets the first value (as Date) of a <code>IJsonArray</code> element.
     * 
     * @return the element or <code>null</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(int index);

    /**
     * Gets the first value (as Date) of a <code>IJsonArray</code> element.
     * 
     * @return the element or the specified 
     * <code>defaultValue</code> if the array or
     * the first element are not found.
     * Throws an exception if an existing element can't be converted to the
     * required type.
     */
    public Date getArrayFirstDate(int index, Date defaultValue);

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

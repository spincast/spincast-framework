package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a <code>Json</code> array "[]", and makes it easier
 * to get its elements in a typed way.
 */
public interface IJsonArray extends Iterable<Object> {

    /**
     * Adds an element at the end.
     */
    public void add(Object element);

    /**
     * Adds an element at the beginning.
     */
    public void addFirst(Object element);

    /**
     * Clears all elements.
     */
    public void clear();

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
     * The underlying list.
     */
    public List<Object> getUnderlyingList();

    /**
     * Gets an element at the specified position.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Object get(int pos);

    /**
     * Gets an element as <code>IJsonObject</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public IJsonObject getJsonObject(int pos);

    /**
     * Gets an element as <code>IJsonArray</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public IJsonArray getJsonArray(int pos);

    /**
     * Gets an element as <code>String</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public String getString(int pos);

    /**
     * Gets an element as <code>Integer</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Integer getInteger(int pos);

    /**
     * Gets an element as <code>Long</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Long getLong(int pos);

    /**
     * Gets an element as <code>Double</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Double getDouble(int pos);

    /**
     * Gets an element as <code>Boolean</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public Boolean getBoolean(int pos);

    /**
     * Gets an element as <code>BigDecimal</code>.
     * 
     * @return the element or <code>null</code> if not found.
     */
    public BigDecimal getBigDecimal(int pos);

}

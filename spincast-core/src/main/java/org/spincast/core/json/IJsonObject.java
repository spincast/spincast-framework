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
     * Exception thrown when a <code>key</code> is not found on a <code>IJsonObject</code> object.
     */
    public static class KeyNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private final String key;

        public KeyNotFoundException(String key) {
            super("Key not found : " + key);
            this.key = key;
        }

        /**
         * The key that wasn not found.
         */
        public String getKey() {
            return key;
        }
    };

    /**
     * Adds a property to the object.
     */
    public void put(String key, Object value);

    /**
     * Removes a property from the object.
     */
    public void remove(String key);

    /**
     * Removes all properties from the object.
     */
    public void removeAll();

    /**
     * Gets a property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Object get(String key) throws KeyNotFoundException;

    /**
     * Gets a property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Object get(String key, Object defaultValue);

    /**
     * Gets a property as <code>IJsonObject</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public IJsonObject getJsonObject(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>IJsonObject</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public IJsonObject getJsonObject(String key, IJsonObject defaultValue);

    /**
     * Gets a property as <code>IJsonArray</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public IJsonArray getJsonArray(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>IJsonArray</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public IJsonArray getJsonArray(String key, IJsonArray defaultValue);

    /**
     * Gets a property as <code>String</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public String getString(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>String</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public String getString(String key, String defaultValue);

    /**
     * Gets a property as <code>Integer</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Integer getInteger(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>Integer</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Integer getInteger(String key, Integer defaultValue);

    /**
     * Gets a property as <code>Long</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Long getLong(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>Long</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Long getLong(String key, Long defaultValue);

    /**
     * Gets a property as <code>Float</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Float getFloat(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>Float</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Float getFloat(String key, Float defaultValue);

    /**
     * Gets a property as <code>Double</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Double getDouble(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>Double</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Double getDouble(String key, Double defaultValue);

    /**
     * Gets a property as <code>Boolean</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Boolean getBoolean(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>Boolean</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Boolean getBoolean(String key, Boolean defaultValue);

    /**
     * Gets a property as <code>BigDecimal</code>.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public BigDecimal getBigDecimal(String key) throws KeyNotFoundException;

    /**
     * Gets a property as <code>BigDecimal</code>.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public byte[] getBytesFromBase64String(String key) throws KeyNotFoundException;

    /**
     * Gets a byte array, from a <em>base 64 encoded</em> property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public byte[] getBytesFromBase64String(String key, byte[] defaultValue);

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or <code>null</code> if not found.
     */
    public Date getDate(String key) throws KeyNotFoundException;

    /**
     * Gets a UTC timezoned date from a <code>ISO 8601</code> date property.
     * 
     * @return the value of the property or the specified 
     * <code>defaultValue</code> if not found.
     */
    public Date getDate(String key, Date defaultValue);

    /**
     * Gets the <code>Json</code> String representation of the object.
     */
    public String toJsonString();

    /**
     * Gets the <code>Json</code> String representation of the object.
     * 
     * @param pretty ff <code>true</code>, the generated String will be formatted.
     */
    public String toJsonString(boolean pretty);

    /**
     * The underlying map.
     */
    public Map<String, Object> getUnderlyingMap();

}

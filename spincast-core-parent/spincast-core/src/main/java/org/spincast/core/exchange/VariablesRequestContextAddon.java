package org.spincast.core.exchange;

import java.util.Map;

import org.spincast.core.json.JsonObject;

import com.google.inject.Key;

/**
 * Methods to read and write request scoped variables.
 */
public interface VariablesRequestContextAddon<R extends RequestContext<?>> {

    /**
     * Adds a request scoped variable. 
    * <p>
     * It is recommended that you prefixe the name of the
     * <code>key</code> with the name of your class
     * (for example : <code>this.getClass().getName()</code>),
     * so it doesn't clash with other keys!
     * <p>
     * If the variable already exists, it is overwritten.
     */
    public void set(String key, Object obj);

    /**
     * Adds request scoped variables.
     * <p>
     * It is recommended that you prefixe the name of the
     * <code>keys</code> with the name of your class
     * (for example : <code>this.getClass().getName()</code>),
     * so they don't clash with other keys!
     * <p>
     * If some variables already exist, they are overwritten.
     */
    public void set(Map<String, Object> variables);

    /**
     * Gets all the request scoped variables.
     * The map is immutable.
     */
    public Map<String, Object> getAll();

    /**
     * Gets the specified request scoped variable.
     */
    public Object get(String key);

    /**
     * Gets the specified request scoped variable as the
     * specified class. 
     * 
     * @throws an exception is the object is not
     * of the specified class.
     */
    public <T> T get(String key, Class<T> clazz);

    /**
     * Gets the specified request scoped variable as the
     * specified <code>Key</code>. 
     * 
     * @throws an exception is the object is not
     * of the specified Key.
     */
    public <T> T get(String key, Key<T> typeKey);

    /**
     * Gets the specified request scoped variable as <code>JsonObject</code>.
     */
    public JsonObject getAsJsonObject(String key);

    /**
     * Gets the specified request scoped variable as a String.
     * {@link java.lang.Object#toString() toString} will be called on the Object.
     */
    public String getAsString(String key);

    /**
     * Removes a request scoped variable.
     * 
     * Note: Spincast uses some request scoped variables for
     * internal purposes. Do not try to remove <em>all</em>
     * variables!
     */
    public void remove(String key);

}

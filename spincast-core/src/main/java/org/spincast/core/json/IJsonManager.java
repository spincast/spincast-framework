package org.spincast.core.json;

import java.io.InputStream;
import java.util.Date;
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
     * Creates a <code>JsonObject</code> from an inputStream.
     */
    public IJsonObject create(InputStream inputStream);

    /**
     * Creates an empty <code>JsonArray</code>.
     */
    public IJsonArray createArray();

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

}

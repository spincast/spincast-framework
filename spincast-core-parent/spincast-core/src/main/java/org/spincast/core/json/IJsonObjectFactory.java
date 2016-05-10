package org.spincast.core.json;

/**
 * Factory to create <code>IJsonObject</code>  and <code>IJsonArray</code> objects.
 */
public interface IJsonObjectFactory {

    public IJsonObject create();

    public IJsonArray createArray();
}

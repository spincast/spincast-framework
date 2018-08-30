package org.spincast.plugins.variables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.VariablesRequestContextAddon;
import org.spincast.core.json.JsonObject;

import com.google.inject.Key;

public class SpincastVariablesRequestContextAddon<R extends RequestContext<?>>
                                                 implements VariablesRequestContextAddon<R> {

    private final Map<String, Object> requestScopedVariables = new HashMap<String, Object>();

    @Override
    public void set(String key, Object obj) {
        getRequestScopedVariables().put(key, obj);
    }

    @Override
    public void set(Map<String, Object> variables) {
        if(variables == null) {
            return;
        }
        getRequestScopedVariables().putAll(variables);
    }

    @Override
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(getRequestScopedVariables());
    }

    protected Map<String, Object> getRequestScopedVariables() {
        return this.requestScopedVariables;
    }

    @Override
    public Object get(String key) {
        return getRequestScopedVariables().get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T asT = (T)get(key);
        return asT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Key<T> typeKey) {
        return get(key, (Class<T>)typeKey.getTypeLiteral().getRawType());
    }

    @Override
    public JsonObject getAsJsonObject(String key) {
        Object obj = getRequestScopedVariables().get(key);
        if(obj == null) {
            return null;
        }
        return (JsonObject)obj;
    }

    @Override
    public String getAsString(String key) {
        Object obj = getRequestScopedVariables().get(key);
        if(obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public void remove(String key) {
        getRequestScopedVariables().remove(key);
    }

}

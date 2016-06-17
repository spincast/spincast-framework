package org.spincast.core.guice;

import java.lang.reflect.Type;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.util.Types;

/**
 * Base class for a Spincast Guice module.
 */
public abstract class SpincastGuiceModuleBase extends AbstractModule {

    /**
     * Get a version of the specified class parameterized with the
     * request context type.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithRequestContext(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getRequestContextType()));
    }

    /**
     * Get a version of the specified class parameterized with the
     * Websocket context type.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithWebsocketContext(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getWebsocketContextType()));
    }

    /**
     * Get a version of the specified class parameterized with the
     * request and Websocket context types.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithContextInterfaces(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getRequestContextType(), getWebsocketContextType()));
    }

    /**
     * The type of the request context objects.
     */
    protected abstract Type getRequestContextType();

    /**
     * The type of the Websocket context objects.
     */
    protected abstract Type getWebsocketContextType();

}

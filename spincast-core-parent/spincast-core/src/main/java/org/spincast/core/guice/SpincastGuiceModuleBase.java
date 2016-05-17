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
    protected <T> Key<T> parameterizeWithRequestContextInterface(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getRequestContextType()));
    }

    /**
     * The type of the request context objects.
     */
    protected abstract Type getRequestContextType();

}

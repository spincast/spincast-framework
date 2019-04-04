package org.spincast.core.guice;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

/**
 * Guice scope for a <code>request</code>.
 * 
 * <p>
 * Modified from: <a href="https://github.com/google/guice/wiki/CustomScopes">https://github.com/google/guice/wiki/CustomScopes</a>:
 * </p>
 * <p>
 * Scopes a single execution of a block of code. Apply this scope with a
 * try/finally block: <pre>
 *
 *   scope.enter();
 *   try {
 *     // explicitly seed some seed objects...
 *     scope.seed(Key.get(SomeObject.class), someObject);
 *     // create and access scoped objects
 *   } finally {
 *     scope.exit();
 *   }
 * </pre>
 *
 * The scope can be initialized with one or more seed values by calling
 * <code>seed(key, value)</code> before the injector will be called upon to
 * provide for this key. A typical use is for a servlet filter to enter/exit the
 * scope, representing a Request Scope, and seed HttpServletRequest and
 * HttpServletResponse.  For each key inserted with seed(), you must include a
 * corresponding binding:
 *  <pre>
 *   bind(key)
 *       .toProvider(SimpleScope.&lt;KeyClass&gt;seededKeyProvider())
 *       .in(ScopeAnnotation.class);
 * </pre>
 * </p>
 *
 * @author Jesse Wilson
 * @author Fedor Karpelevitch
 */
public class SpincastRequestScope implements Scope {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastRequestScope.class);

    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

    public void enter() {
        checkState(this.values.get() == null,
                   "A scoping block is already in progress");
        this.values.set(Maps.<Key<?>, Object>newHashMap());
    }

    public void exit() {
        checkState(this.values.get() != null,
                   "No scoping block in progress");
        this.values.remove();
    }

    public <T> void seed(Key<T> key, T value) {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

        if(scopedObjects.containsKey(key)) {
            logger.warn("Replacing key '" + key + "' in our custom " + SpincastRequestScope.class.getSimpleName() + "!");
        }

        scopedObjects.put(key, value);
    }

    public <T> void seed(Class<T> clazz, T value) {
        seed(Key.get(clazz), value);
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {

            @Override
            public T get() {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T)scopedObjects.get(key);
                if(current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();

                    // don't remember proxies; these exist only to serve
                    // circular dependencies
                    if(Scopes.isCircularProxy(current)) {
                        return current;
                    }

                    scopedObjects.put(key, current);
                }
                return current;
            }
        };
    }

    protected <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = this.values.get();
        if(scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }

    /**
     * Returns a provider that always throws exception complaining that the
     * object in question must be seeded before it can be injected.
     *
     * @return typed provider
     */
    @SuppressWarnings("unchecked")
    public static <T> Provider<T> getSeedErrorProvider(@SuppressWarnings("rawtypes") final Key key) {
        return (Provider<T>)new Provider<Object>() {

            @Override
            public Object get() {
                throw new IllegalStateException("If you got here then it means that" +
                                                " your code asked for scoped object " + key +
                                                " which should have been" +
                                                " explicitly seeded in this the " + SpincastRequestScope.class +
                                                " scope by calling" +
                                                " " + SpincastRequestScope.class + "#seed(), but was not.");
            }
        };
    }

}

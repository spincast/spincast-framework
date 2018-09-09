package org.spincast.plugins.hotswap.classeswatcher;

import java.util.Set;

/**
 * A listener for classes redefinitions.
 */
public interface HotSwapClassesRedefinitionsListener {

    /**
     * Should this listener be enabled?
     * <p>
     * In most cases, you only want to be able to auto
     * reload classes during development.
     * <p>
     * When this method return <code>false</code>, the
     * listener is not registered, at all.
     * <p>
     * A common pattern for this method is to use:
     * <pre>
     * return getSpincastConfig().isDevelopmentMode();
     * </pre>
     */
    public boolean isEnabled();

    /**
     * The class(es) to watch.
     */
    public Set<Class<?>> getClassesToWatch();

    /**
     * Called when a watched class is redefined.
     * <p>
     * Receives the class that was modified.
     * <p>
     * The method is called in a new Thread.
     */
    public void classRedefined(Class<?> redefinedClass);
}

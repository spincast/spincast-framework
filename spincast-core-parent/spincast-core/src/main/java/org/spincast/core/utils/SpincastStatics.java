package org.spincast.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some very few static methods.
 * 
 * You can still change the underlying instance though, in case
 * you need to change/fix something.
 */
public class SpincastStatics {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastStatics.class);

    private static SpincastStatics instance;

    protected static SpincastStatics getInstance() {
        if(instance == null) {
            instance = new SpincastStatics();
        }
        return instance;
    }

    public static void setInstance(SpincastStatics instance) {
        SpincastStatics.instance = instance;
    }

    public static RuntimeException runtimize(Exception ex) {
        return getInstance().runtimizePrivate(ex);
    }

    protected RuntimeException runtimizePrivate(Exception ex) {

        Objects.requireNonNull(ex, "NULL exception");

        // Use the wrapped exception.
        if(ex instanceof InvocationTargetException) {
            Throwable wrappedException = ((InvocationTargetException)ex).getTargetException();
            if(wrappedException != null && wrappedException instanceof Exception) {
                ex = (Exception)wrappedException;
            }
        }

        if(ex instanceof InterruptedException) {
            RuntimeException exceptionToReturn = manageInterruptedException(ex);
            if(exceptionToReturn != null) {
                return exceptionToReturn;
            }
        }

        if(ex instanceof RuntimeException) {
            return (RuntimeException)ex;
        }

        return new RuntimeException(ex.getMessage(), ex);
    }

    /**
     * Return <code>null</code> to continue the exception processing or
     * an exception to be returned immediatly.
     * 
     * @see <a href="http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html">http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html</a>
     */
    protected RuntimeException manageInterruptedException(Exception ex) {
        Thread.currentThread().interrupt();
        return new RuntimeException(ex.getMessage(), ex);
    }

    /**
     * Gets the stack trace of an Exception.
     */
    public static String getStackTrace(Throwable ex) {
        return getInstance().getStackTraceInstance(ex);
    }

    protected String getStackTraceInstance(Throwable ex) {

        if(ex == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Creates a map.
     */
    public static <K, V> Map<K, V> map(K key1, V value1) {
        return getInstance().mapInstance(key1, value1);
    }

    /**
     * Creates a Map&lt;String, Object&gt; map.
     */
    public static Map<String, Object> params(String key1, Object value1) {
        return map(key1, value1);
    }

    protected <K, V> Map<K, V> mapInstance(K key1, V value1) {

        Objects.requireNonNull(key1, "The keys can't be NULL");

        Map<K, V> map = new HashMap<K, V>();
        map.put(key1, value1);

        return map;
    }

    /**
     * Creates a map.
     */
    public static <K, V> Map<K, V> map(K key1, V value1,
                                       K key2, V value2) {
        return getInstance().mapInstance(key1,
                                         value1,
                                         key2,
                                         value2);
    }

    /**
     * Creates a Map&lt;String, Object&gt; map.
     */
    public static Map<String, Object> params(String key1, Object value1, String key2, Object value2) {
        return map(key1, value1, key2, value2);
    }

    protected <K, V> Map<K, V> mapInstance(K key1, V value1,
                                           K key2, V value2) {

        Objects.requireNonNull(key2, "The keys can't be NULL");

        Map<K, V> map = mapInstance(key1, value1);
        map.put(key2, value2);

        return map;
    }

    /**
     * Creates a map.
     */
    public static <K, V> Map<K, V> map(K key1, V value1,
                                       K key2, V value2,
                                       K key3, V value3) {
        return getInstance().mapInstance(key1,
                                         value1,
                                         key2,
                                         value2,
                                         key3,
                                         value3);
    }

    /**
     * Creates a Map&lt;String, Object&gt; map.
     */
    public static Map<String, Object> params(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return map(key1, value1, key2, value2, key3, value3);
    }

    protected <K, V> Map<K, V> mapInstance(K key1, V value1,
                                           K key2, V value2,
                                           K key3, V value3) {

        Objects.requireNonNull(key3, "The keys can't be NULL");

        Map<K, V> map = mapInstance(key1, value1, key2, value2);
        map.put(key3, value3);

        return map;
    }

    /**
     * Creates a map.
     */
    public static <K, V> Map<K, V> map(K key1, V value1,
                                       K key2, V value2,
                                       K key3, V value3,
                                       K key4, V value4) {
        return getInstance().mapInstance(key1,
                                         value1,
                                         key2,
                                         value2,
                                         key3,
                                         value3,
                                         key4,
                                         value4);
    }

    /**
     * Creates a Map&lt;String, Object&gt; map.
     */
    public static Map<String, Object> params(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                             String key4, Object value4) {
        return map(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    protected <K, V> Map<K, V> mapInstance(K key1, V value1,
                                           K key2, V value2,
                                           K key3, V value3,
                                           K key4, V value4) {

        Objects.requireNonNull(key4, "The keys can't be NULL");

        Map<K, V> map = mapInstance(key1, value1, key2, value2, key3, value3);
        map.put(key4, value4);

        return map;
    }

    /**
     * Creates a map.
     */
    public static <K, V> Map<K, V> map(K key1, V value1,
                                       K key2, V value2,
                                       K key3, V value3,
                                       K key4, V value4,
                                       K key5, V value5) {
        return getInstance().mapInstance(key1,
                                         value1,
                                         key2,
                                         value2,
                                         key3,
                                         value3,
                                         key4,
                                         value4,
                                         key5,
                                         value5);
    }

    /**
     * Creates a Map&lt;String, Object&gt; map.
     */
    public static Map<String, Object> params(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                             String key4, Object value4, String key5, Object value5) {
        return map(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5);
    }

    protected <K, V> Map<K, V> mapInstance(K key1, V value1,
                                           K key2, V value2,
                                           K key3, V value3,
                                           K key4, V value4,
                                           K key5, V value5) {

        Objects.requireNonNull(key5, "The keys can't be NULL");

        Map<K, V> map = mapInstance(key1, value1, key2, value2, key3, value3, key4, value4);
        map.put(key5, value5);

        return map;
    }

}

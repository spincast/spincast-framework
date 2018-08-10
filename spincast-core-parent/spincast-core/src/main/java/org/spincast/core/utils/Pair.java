package org.spincast.core.utils;

/**
 * A simple {@link String} to {@link Object} pair.
 * <p>
 * For a generic version, use
 * {@link org.spincast.shaded.org.apache.commons.lang3.tuple.Pair}.
 */
public class Pair {

    private final String key;
    private final Object value;

    private Pair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static Pair of(String key, Object value) {
        return new Pair(key, value);
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }
}

package org.spincast.core.exceptions;

/**
 * Exception thrown when an element from a JsonObject
 * or from a JsonArray can't be converted to the
 * requested type.
 */
public class CantConvertException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String actualType;
    private final String invalidTargetType;
    private final Object value;

    /**
     * Constructor
     */
    public CantConvertException(String actualType, String invalidTargetType, Object value) {
        super("Can't convert from : " + actualType + " to " + invalidTargetType + " : " + value);
        this.actualType = actualType;
        this.invalidTargetType = invalidTargetType;
        this.value = value;
    }

    public String getActualType() {
        return this.actualType;
    }

    public String getInvalidTargetType() {
        return this.invalidTargetType;
    }

    public Object getValue() {
        return this.value;
    }

};

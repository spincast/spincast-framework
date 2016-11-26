package org.spincast.core.exceptions;

/**
 * Exception thrown when an element from an JsonObject
 * or from an JsonArray can't be compared to another.
 */
public class CantCompareException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String actualType;
    private final String invalidTargetType;
    private final Object value;

    /**
     * Constructor
     */
    public CantCompareException(String actualType, String invalidTargetType, Object value) {
        super("Can't compare : " + actualType + " to " + invalidTargetType + " : " + value);
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

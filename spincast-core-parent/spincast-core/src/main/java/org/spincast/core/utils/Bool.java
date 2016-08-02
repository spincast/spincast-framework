package org.spincast.core.utils;

/**
 * Boolean object that can't be autoboxed. 
 * <p>
 * Java's Boolean objects are quite dangerous because of autoboxing. For
 * example, <code>if(myBooleanObject)</code> will throw an NullPointerException
 * if it's null.
 * </p>
 * <p>
 * This Bool class allows to carry a <code>null</code>, <code>true</code>
 * or <code>false</code> value, without the autoboxing dangers.
 * </p>
 */
public class Bool {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool from(boolean val) {
        return val ? TRUE : FALSE;
    }

    public boolean getBoolean() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(getBoolean());
    }
}

package org.spincast.core.dictionary;

/**
 * How to deal with a key that is not found in the dictionary,
 * at runtime?
 */
public enum DictionaryEntryNotFoundBehavior {

    /**
     * A {@link DictionaryKeyNotFoundException} exception
     * will be thrown.
     */
    EXCEPTION,

    /**
     * Returns the key itself.
     */
    RETURN_KEY,

    /**
     * Returns an empty string
     */
    RETURN_EMPTY_STRING


}

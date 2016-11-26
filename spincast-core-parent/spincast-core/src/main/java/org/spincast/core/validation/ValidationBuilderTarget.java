package org.spincast.core.validation;

import org.spincast.core.json.JsonArray;

public interface ValidationBuilderTarget {

    /**
     * The element to validate.
     */
    public ValidationBuilderCore element(Object element);

    /**
     * Validates all the elements of the specified 
     * <code>JsonArray</code>.
     * <p>
     * Each validation key will be the original validation key
     * followed by "[X]", where "X" is the position of the validated
     * element in the array.
     */
    public ValidationBuilderArray all(JsonArray array);

}

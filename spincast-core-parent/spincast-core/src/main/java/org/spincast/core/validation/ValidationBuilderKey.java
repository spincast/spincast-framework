package org.spincast.core.validation;

public interface ValidationBuilderKey {

    /**
     * The validation key. This should in general
     * be the <code>JsonPath</code> of the element when 
     * this element is part of a <code>JsonObject</code>! 
     * That way, we can associate the validation Messages 
     * to the validated elements.
     */
    public ValidationBuilderTarget key(String validationKey);

}

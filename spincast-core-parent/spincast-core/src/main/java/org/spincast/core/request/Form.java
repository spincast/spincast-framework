package org.spincast.core.request;

import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.ValidationSet;
import org.spincast.core.validation.Validators;

/**
 * Specialized {@link JsonObject} that is also a 
 * {@link ValidationSet} to store validations.
 */
public interface Form extends JsonObject, ValidationSet {

    /**
     * The name of the form.
     */
    public String getFormName();

    /**
     * Sets the {@link JsonObject} to use
     * to store the validation messages.
     * <p>
     * If there are existing validation messages on
     * the current validation object, they will be
     * merged to the new object!
     * <p>
     * Beware that changing this object may disconnect
     * the validation messages of this form from a
     * global validation element on the response's model!
     */
    public void setValidationObject(JsonObject validationObject);

    /**
     * Returns the {@link Validators}.
     */
    public Validators validators();


}

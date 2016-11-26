package org.spincast.core.validation;

import java.util.List;

import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectOrArray;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory to create Validation related components.
 */
public interface ValidationFactory {

    /**
     * Creates a new, empty, validation set.
     */
    public ValidationSet createValidationSet();

    /**
     * Creates a validation set and add the existing 
     * Validation Messages using the <code>validationKey</code>
     * key.
     */
    public ValidationSet createValidationSet(String validationKey, List<ValidationMessage> messages);

    /**
     * Creates a validation set and add the existing 
     * Validation Message using the <code>validationKey</code>
     * key.
     */
    public ValidationSet createValidationSet(String validationKey, ValidationMessage message);

    /**
     * Creates a validation set which is binded to the
     * specified <code>JsonObject</code>. It is then possible to
     * use a <code>JsonPath</code> to specified an element to validate
     * on that object.
     */
    public JsonObjectValidationSet createJsonObjectValidationSet(JsonObject objectToValidate);

    /**
     * Creates a validation set which is binded to the
     * specified <code>JsonArray</code>. It is then possible to
     * use a <code>JsonPath</code> to specified an element to validate
     * on that array.
     */
    public JsonArrayValidationSet createJsonArrayValidationSet(JsonArray arrayToValidate);

    /**
     * Creates a validation message. You can then manually
     * add this message to a validation set.
     */
    public ValidationMessage createMessage(@Assisted("validationLevel") ValidationLevel validationLevel,
                                           @Assisted("code") String code,
                                           @Assisted("text") String text);

    /**
     * Creates a <code>ValidationBuilderKey</code>.
     */
    public ValidationBuilderKey createValidationBuilderKey(ValidationSet validationSet, SimpleValidator validator);

    /**
     * Creates a <code>JsonObjectValidationBuilderKey</code>.
     */
    public JsonObjectValidationBuilderKey createJsonObjectValidationBuilderKey(ValidationSet validationSet,
                                                                               JsonObjectOrArray validatedObject,
                                                                               SimpleValidator validator);

    /**
     * Creates a <code>JsonArrayValidationBuilderKey</code>.
     */
    public JsonArrayValidationBuilderKey createJsonArrayValidationBuilderKey(ValidationSet validationSet,
                                                                             JsonArray validatedObject,
                                                                             SimpleValidator validator);

    /**
     * Creates a <code>ValidationBuilderTarget</code>.
     */
    public ValidationBuilderTarget createValidationBuilderTarget(ValidationSet validationSet,
                                                                 SimpleValidator validator,
                                                                 String validationKey);

    /**
     * Creates a <code>ValidationBuilderCore</code>.
     */
    public ValidationBuilderCore createValidationBuilderCore(ValidationSet validationSet,
                                                             SimpleValidator validator,
                                                             String validationKey,
                                                             Object elementToValidate);

    /**
     * Creates a <code>ValidationBuilderArray</code>.
     */
    public ValidationBuilderArray createValidationBuilderArray(ValidationSet validationSet,
                                                               SimpleValidator validator,
                                                               String validationKey,
                                                               JsonArray array);

    /**
     * Creates a <code>ValidationBuilderArray</code>.
     */
    public ValidationBuilderArray createValidationBuilderArray(ValidationSet validationSet,
                                                               SimpleValidator validator,
                                                               String validationKey,
                                                               JsonArray array,
                                                               boolean elementWasNotAnArray);

}

package org.spincast.core.validation;

/**
 * Validation parameters specific to an <em>JsonArray</em>
 * validation.
 */
public interface ValidationBuilderArray extends ValidationBuilderCoreBase<ValidationBuilderArray> {

    /**
     * If all its elements are successfully validated, add a Success Validation Message
     * on the array itself.
     * <p>
     * The validation key is going to be the <code>JsonPath</code> to the array.
     */
    public ValidationBuilderArray arrayItselfAddSuccessMessage();

    /**
     * If all its elements are successfully validated, add a Success Validation Message
     * on the array itself.
     * 
     * @param customSuccessMessageText the Success message text to use for the
     * array itself. If <code>null</code>, the default message will be used.
     */
    public ValidationBuilderArray arrayItselfAddSuccessMessage(String customSuccessMessageText);

    /**
     * If all its elements are successfully validated, add a Success Validation Message
     * on the array itself.
     * 
     * @param validationKey The validation key to which the <code>Validation Message</code>
     * will be added.
     * 
     * @param customSuccessMessageText the Success message text to use for the
     * array itself. If <code>null</code>, the default message will be used.
     */
    public ValidationBuilderArray arrayItselfAddSuccessMessage(String validationKey, String customSuccessMessageText);

    /**
     * If the validation of at least one element failed, add a Fail Validation Message
     * on the array itself.
     * 
     * The validation key is going to be the <code>JsonPath</code> to the array.
     */
    public ValidationBuilderArray arrayItselfAddFailMessage();

    /**
     * If the validation of at least one element failed, add a Fail Validation Message
     * on the array itself.
     * 
     * @param customSuccessMessageText the Fail message text to use for the
     * array itself. If <code>null</code>, the default message will be used.
     */
    public ValidationBuilderArray arrayItselfAddFailMessage(String customSuccessMessageText);

    /**
     * If the validation of at least one element failed, add a Fail Validation Message
     * on the array itself.
     * 
     * @param validationKey The validation key to which the <code>Validation Message</code>
     * will be added.
     * 
     * @param customFailMessageText the Fail message text to use for the
     * array itself.
     */
    public ValidationBuilderArray arrayItselfAddFailMessage(String validationKey, String customFailMessageText);

}

package org.spincast.core.validation;

/**
 * The core of the builder used to create a Validation Set.
 */
public interface ValidationBuilderCoreBase<T extends ValidationBuilderCoreBase<?>> {

    /**
     * Modifies the default validation code.
     */
    public T code(String code);

    /**
     * If the validation is successful, the validator should add
     * a Success Message. The default behavior is to only
     * add Error and Warning Messages.
     */
    public T addMessageOnSuccess();

    /**
     * If the validation is successful, the validator should add
     * a Success Message. The default behavior is to only
     * add Error and Warning Messages.
     * 
     * @param customSuccessMessageText The text to use for 
     * the Message instead of the default one.
     */
    public T addMessageOnSuccess(String customSuccessMessageText);

    /**
     * If this is called, a validation failure will result in
     * a <em>Warning</em> level Message instead of an Error level Message.
     * This can be useful to indicate to a user that some
     * data is valid, but with a warning...
     * <p>
     * Please remember that an object with Warnings validation messages
     * and no Error Messages is considered as <em>valid</em>!
     */
    public T treatErrorAsWarning();

    /**
     * Instead of the default one, use the specified text 
     * for the validation Message in case of a failure.
     * <p>
     * Note that this text will be used for Errors but also 
     * for <em>Warning</em> Message text is {@link #treatErrorAsWarning()} 
     * has been called.
     */
    public T failMessageText(String customFailMessageText);

    /**
     * Performs the validation, saves the result into the
     * validation set and returns it.
     */
    public ValidationSet validate();

    /**
     * Performs the validation, saves the result into the
     * validation set and returns it.
     * 
     * @param onlyIfNoMessageYet if <code>true</code>, the validation
     * will be performs only if no Message already exists
     * for the validation key. This is a synonym to calling
     * <code>validate(ValidationLevel.SUCCESS)</code>.
     */
    public ValidationSet validate(boolean onlyIfNoMessageYet);

    /**
     * Performs the validation, saves the result into the
     * validation set and returns it.
     * 
     * @param onlyIfNoMessageYet The validation
     * will be performs only if no Message at the specified 
     * <code>ValidationLevel</code> or higher already exists
     * for the validation key. 
     * <p>
     * <ul>
     * <li>
     * <code>validate(ValidationLevel.SUCCESS)</code> : The validation will
     * be perform only if no messages at all already exist for that
     * validation key.
     * </li>
     * <li>
     * <code>validate(ValidationLevel.WARNING)</code> : The validation will
     * be perform only if no WARNING or ERROR messages 
     * already exist for that validation key.
     * </li>
     * <li>
     * <code>validate(ValidationLevel.ERROR)</code> : The validation will
     * be perform only if no ERROR messages already exist for 
     * that validation key.
     * </li>
     * </ul>
     */
    public ValidationSet validate(ValidationLevel onlyIfNoMessageAtThisLevelOrHigherYet);

}

package org.spincast.core.validation;

import java.util.List;
import java.util.Map;

import org.spincast.core.json.JsonObject;

public interface ValidationSet {

    /**
     * Adds a new validation Message to this set, using the
     * specified validation key.
     */
    public void addMessage(String validationKey,
                           ValidationMessage validationMessage);

    /**
     * Creates and adds a new validation Message.
     */
    public void addMessage(String validationKey,
                           ValidationLevel messageLevel,
                           String code,
                           String text);

    /**
     * Creates and adds a new Error validation Message.
     * 
     */
    public void addError(String validationKey, String code, String text);

    /**
     * Creates and adds a new Warning validation Message.)
     */
    public void addWarning(String validationKey, String code, String text);

    /**
     * Creates and adds a new Success validation Message.
     */
    public void addSuccess(String validationKey, String code, String text);


    /**
     * Does this validation set contain any validation
     * Messages?
     */
    public boolean hasMessages();

    /**
     * Does the the element at the specified key
     * contains validation Messages?
     */
    public boolean hasMessages(String validationKey);

    /**
     * Returns <code>true</code> if the validation set 
     * contains at least one <em>WARNING</em> message, but no
     * <em>ERROR</em> messages.
     */
    public boolean isWarning();

    /**
     * Returns <code>true</code> if the validation set 
     * for the element at the specified key contains at 
     * least one <em>WARNING</em> message, but no
     * <em>ERROR</em> messages.
     */
    public boolean isWarning(String validationKey);

    /**
     * Returns <code>true</code> if the validation set
     * only contains <em>SUCCESS</em> messages
     * (or contains no messages at all).
     */
    public boolean isSuccess();

    /**
     * Returns <code>true</code> if the validation set
     * for the element at the specified key only contains 
     * <em>SUCCESS</em> messages
     * (or contains no messages at all).
     */
    public boolean isSuccess(String validationKey);

    /**
     * Returns <code>true</code> if the validation set 
     * contains at least one <em>ERROR</em> message.
     */
    public boolean isError();

    /**
     * Returns <code>true</code> if the validation set 
     * for the element at the specified key contains
     * at least one <em>ERROR</em> message.
     */
    public boolean isError(String validationKey);

    /**
     * Returns <code>true</code> if the validation set 
     * does not contain <em>ERROR</em> messages.
     * The set <em>may</em> contain 
     * <em>WARNING</em> messages.
     * <p>
     * This is a synonym of <code>!isError()</code>.
     */
    public boolean isValid();

    /**
     * Returns <code>true</code> if the validation set 
     * for the elements at the specified keys
     * dont not contain <em>ERROR</em> messages.
     * <p>
     * The elements <em>may</em> contains
     * <em>WARNING</em> messages.
     * <p>
     * If no keys are passed, then <em>all keys</em>
     * must be valid (synonym of {@link #isValid()}.
     */
    public boolean isValid(String... validationKey);

    /**
     * Gets the validation keys and their 
     * associated messages.
     * <p>
     * The Map and the lists are immutable.
     */
    public Map<String, List<ValidationMessage>> getMessages();

    /**
     * Gets the validation messages for the specified key.
     * <p>
     * The list is immutable.
     */
    public List<ValidationMessage> getMessages(String validationKey);

    /**
     * Quick way to get a formatted version of the validation
     * messages for the specified key.
     * 
     * @param key The key to get messages for.
     * @param formatType The type of output for the messages (Text, HTML, Json or XML).
     * 
     * @return the formatted messages or <code>null</code> if
     * there are no validation messages.
     */
    public String getMessagesFormatted(String validationKey, ValidationMessageFormatType formatType);

    /**
     * Quick way to get a formatted version of all validation
     * messages.
     * 
     * @param formatType The type of output for the messages (Text, HTML, Json or XML).
     * 
     * @return the formatted messages or <code>null</code> if
     * there are no validation messages.
     */
    public String getMessagesFormatted(ValidationMessageFormatType formatType);

    /**
     * Creates a new {@link ValidationSet}.
     */
    public ValidationSet createNewValidationSet();

    /**
     * Merges another {@link ValidationSet}.
     * 
     * @return this, fluent-style.
     */
    public ValidationSet mergeValidationSet(ValidationSet validationSet);

    /**
     * Merges another {@link ValidationSet} and prefixes all the
     * validation keys of this set using the
     * <code>validationKeyPrefix</code>.
     * 
     * @return this, fluent-style.
     */
    public ValidationSet mergeValidationSet(String validationKeyPrefix, ValidationSet validationSet);

    /**
     * The number of validation messages in this set.
     */
    public int size();

    /**
     * The {@link JsonObject} in which the validation messages
     * are actually stored.
     * <p>
     * You can use this object to return the validation result
     * as json, in a response.
     */
    public JsonObject getValidationResultAsJsonObject();


}

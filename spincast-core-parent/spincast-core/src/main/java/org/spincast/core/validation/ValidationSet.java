package org.spincast.core.validation;

import java.util.List;
import java.util.Map;

import org.spincast.core.exceptions.CantCompareException;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.ToJsonObjectConvertible;
import org.spincast.core.utils.ObjectConverter;

/**
 * A validation set is a container to store one or multiple
 * validation messages. It represents the result of a validation
 * of a particular element, or of a group of elements.
 * <p>
 * You create a validation set then you add some <code>validation Messages</code> to
 * it, manually using the {@link #addMessage(ValidationLevel, String, String, String) addMessage}
 * similar methods, or by using some provided validators, such as
 * {@link #validationNotBlank()}.
 * <p>
 * Multiple validation sets can be grouped together using the
 * {@link #mergeValidationSet(ValidationSet)} related methods. This
 * means you can validate some elements independently, then group
 * the results together to form a final validation set.
 */
public interface ValidationSet extends ToJsonObjectConvertible {

    /**
     * Codes for some provided validations :
     */
    public static final String VALIDATION_CODE_WHOLE_ARRAY_VALIDATION_RESULT_TYPE =
            "VALIDATION_TYPE_WHOLE_ARRAY_VALIDATION_RESULT_TYPE";
    public static final String VALIDATION_CODE_NOT_AN_ARRAY =
            "VALIDATION_TYPE_NOT_AN_ARRAY";
    public static final String VALIDATION_CODE_NOT_NULL = "VALIDATION_TYPE_NOT_NULL";
    public static final String VALIDATION_CODE_NULL = "VALIDATION_TYPE_NULL";
    public static final String VALIDATION_CODE_NOT_BLANK = "VALIDATION_TYPE_NOT_BLANK";
    public static final String VALIDATION_CODE_BLANK = "VALIDATION_TYPE_BLANK";
    public static final String VALIDATION_CODE_EQUIVALENT = "VALIDATION_TYPE_EQUALS";
    public static final String VALIDATION_CODE_NOT_EQUIVALENT = "VALIDATION_TYPE_NOT_EQUALS";
    public static final String VALIDATION_CODE_EQUIVALENT_OR_GREATER = "VALIDATION_TYPE_EQUALS_OR_GREATER";
    public static final String VALIDATION_CODE_GREATER = "VALIDATION_TYPE_GREATER";
    public static final String VALIDATION_CODE_EQUIVALENT_OR_LESS = "VALIDATION_TYPE_EQUALS_OR_LESS";
    public static final String VALIDATION_CODE_LESS = "VALIDATION_TYPE_LESS";
    public static final String VALIDATION_CODE_LENGTH = "VALIDATION_TYPE_LENGTH";
    public static final String VALIDATION_CODE_MIN_LENGTH = "VALIDATION_TYPE_MIN_LENGTH";
    public static final String VALIDATION_CODE_MAX_LENGTH = "VALIDATION_TYPE_MAX_LENGTH";
    public static final String VALIDATION_CODE_PATTERN = "VALIDATION_TYPE_PATTERN";
    public static final String VALIDATION_CODE_NOT_PATTERN = "VALIDATION_TYPE_NOT_PATTERN";
    public static final String VALIDATION_CODE_EMAIL = "VALIDATION_TYPE_EMAIL";
    public static final String VALIDATION_CODE_SIZE = "VALIDATION_TYPE_SIZE";
    public static final String VALIDATION_CODE_MIN_SIZE = "VALIDATION_TYPE_MIN_SIZE";
    public static final String VALIDATION_CODE_MAX_SIZE = "VALIDATION_TYPE_MAX_SIZE";
    public static final String VALIDATION_CODE_CAN_BE_CONVERTED_TO = "VALIDATION_TYPE_CAN_BE_CONVERTED_TO";
    public static final String VALIDATION_CODE_IS_OF_TYPE = "VALIDATION_TYPE_IS_OF_TYPE";

    /**
     * Creates a new, empty, validation set.
     */
    public ValidationSet createValidationSet();

    /**
     * Adds a new validation Message to this set, using the
     * specified validation key.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet addMessage(String validationKey,
                                    ValidationMessage validationMessage);

    /**
     * Creates and adds a new validation Message.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet addMessage(String validationKey,
                                    ValidationLevel messageLevel,
                                    String code,
                                    String text);

    /**
     * Creates and adds a new Error validation Message.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet addError(String validationKey, String code, String text);

    /**
     * Creates and adds a new Warning validation Message.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet addWarning(String validationKey, String code, String text);

    /**
     * Creates and adds a new Success validation Message.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet addSuccess(String validationKey, String code, String text);

    /**
     * Merges an existing validation set.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet mergeValidationSet(ValidationSet set);

    /**
     * Adds an existing validation set and prefix all the
     * validation keys of this Set using the
     * <code>validationKeyPrefix</code>.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet mergeValidationSet(String validationKeyPrefix, ValidationSet result);

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
     * for the element at the specified key
     * does not contain <em>ERROR</em> messages.
     * The element <em>may</em> contains
     * <em>WARNING</em> messages.
     * <p>
     * This is a synonym of <code>!isError(key)</code>.
     */
    public boolean isValid(String validationKey);

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
     * Converts the set to a <code>JsonObject</code> object.
     * <p>
     * The resulting <code>JsonObject</code> object 
     * is <em>immutable</em>.
     */
    @Override
    public JsonObject convertToJsonObject();

    /**
     * Starts a validation using a
     * custom <code>SimpleValidator</code>.
     * <p>
     * This is useful when you have a validator that can be 
     * reuse in different contexts (as the ones provided by Spincast).
     * <p>
     * If you need <em>full</em> control over a validation, the number of 
     * generated Messages for example, then you can instead run your 
     * validations as you wish
     * and use {@link #addMessage(ValidationMessage)} and other
     * similar methods to add the resulting validation Messages to the
     * set.
     */
    public ValidationBuilderKey validation(SimpleValidator validator);

    /**
     * Starts the creation of a "not blank" validation. 
     */
    public ValidationBuilderKey validationNotBlank();

    /**
     * Starts the creation of a "must be blank" validation. 
     */
    public ValidationBuilderKey validationBlank();

    /**
     * Starts the creation of a "email" validation. 
     */
    public ValidationBuilderKey validationEmail();

    /**
     * Starts the creation of a "not null" validation. 
     */
    public ValidationBuilderKey validationNotNull();

    /**
     * Starts the creation of a "must be null" validation. 
     */
    public ValidationBuilderKey validationNull();

    /**
     * Starts the creation of a "pattern must match" validation. 
     */
    public ValidationBuilderKey validationPattern(String pattern);

    /**
     * Starts the creation of a "pattern must not match" validation. 
     */
    public ValidationBuilderKey validationNotPattern(String pattern);

    /**
     * Starts the creation of a "size" validation. 
     * 
     * @param ignoreNullValues If <code>true</code>, <code>null</code> values will
     * be ignored in the total count. For example, if the size validation
     * is done on a <code>JsonArray</code> which is <code>[null, null "abc"]</code>,
     * the size would be <em>3</em> if <code>ignoreNullValues</code> is <code>false</code>
     * and <em>1</em> if it's <code>true</code>.
     */
    public ValidationBuilderKey validationSize(int size, boolean ignoreNullValues);

    /**
     * Starts the creation of a "minimum size" validation. 
     * 
     * @param ignoreNullValues If <code>true</code>, <code>null</code> values will
     * be ignored in the total count. For example, if the size validation
     * is done on a <code>JsonArray</code> which is <code>[null, null "abc"]</code>,
     * the size would be <em>3</em> if <code>ignoreNullValues</code> is <code>false</code>
     * and <em>1</em> if it's <code>true</code>.
     */
    public ValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues);

    /**
     * Starts the creation of a "maximum size" validation. 
     * 
     * @param ignoreNullValues If <code>true</code>, <code>null</code> values will
     * be ignored in the total count. For example, if the size validation
     * is done on a <code>JsonArray</code> which is <code>[null, null "abc"]</code>,
     * the size would be <em>3</em> if <code>ignoreNullValues</code> is <code>false</code>
     * and <em>1</em> if it's <code>true</code>.
     */
    public ValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues);

    /**
     * Starts the creation of a "length" validation. 
     */
    public ValidationBuilderKey validationLength(int length);

    /**
     * Starts the creation of a "minimum length" validation. 
     * <p>
     * A <code>null</code> value returns <code>false</code>, otherwise
     * <code>toString()</code> is called on the other object to
     * check its "length".
     */
    public ValidationBuilderKey validationMinLength(int minLength);

    /**
     * Starts the creation of a "maximum length" validation. 
     * <p>
     * A <code>null</code> value returns <code>true</code>, otherwise
     * <code>toString()</code> is called on the other object to
     * check its "length".
     */
    public ValidationBuilderKey validationMaxLength(int maxLength);

    /**
     * Starts the creation of a "equivalent" validation. 
     * <p>
     * To validate if an element is <em>equivalent</em> to another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they are not equivalent. When both
     * elements are of the same types, the {@link #equals(Object)}
     * method is used to compre them.
     * <p>
     * The exact logic to determine if two elements are equivalent
     * can be found in {@link ObjectConverter#isEquivalent(Object, Object)}.
     */
    public ValidationBuilderKey validationEquivalent(Object reference);

    /**
     * Starts the creation of a "not equivalent" validation. 
     * <p>
     * To validate if an element is <em>equivalent</em> to another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they are not equivalent. When both
     * elements are of the same types, the {@link #equals(Object)}
     * method is used to compre them.
     * <p>
     * The exact logic to determine if two elements are equivalent
     * can be found in {@link ObjectConverter#isEquivalent(Object, Object)}.
     */
    public ValidationBuilderKey validationNotEquivalent(Object reference);

    /**
     * Starts the creation of a "less than" validation. 
     * <p>
     * To validate if an element is <em>less</em> than another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they can't be compared.
     * The resulting type must implement the {@link Comparable} interface,
     * so the {@link Comparable#compareTo(Object)} method is
     * used to compare the two elements.
     * <p>
     * <code>null</code> is always less than a non-null element.
     * 
     * @throws CantCompareException if the two elements can't be compared
     * together.
     */
    public ValidationBuilderKey validationLess(Object reference) throws CantCompareException;

    /**
     * Starts the creation of a "greater than" validation. 
     * <p>
     * To validate if an element is <em>greater</em> than another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they can't be compared.
     * The resulting type must implement the {@link Comparable} interface,
     * so the {@link Comparable#compareTo(Object)} method is
     * used to compare the two elements.
     * <p>
     * <code>null</code> is always less than a non-null element.
     * 
     * @throws CantCompareException if the two elements can't be compared
     * together.
     */
    public ValidationBuilderKey validationGreater(Object reference) throws CantCompareException;

    /**
     * Starts the creation of a "equivalent or less" validation. 
     * <p>
     * To validate if an element is <em>equivalent or less</em> than another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they can't be compared.
     * The resulting type must implement the {@link Comparable} interface,
     * so the {@link Comparable#compareTo(Object)} method is
     * used to compare the two elements.
     * <p>
     * <code>null</code> is always less than a non-null element.
     * 
     * @throws CantCompareException if the two elements can't be compared
     * together.
     */
    public ValidationBuilderKey validationEquivalentOrLess(Object reference) throws CantCompareException;

    /**
     * Starts the creation of a "equivalent or greater" validation. 
     * <p>
     * To validate if an element is <em>equivalent or greater</em> than another,
     * one is converted to the type of the other if this is required. 
     * If this conversion fails, they can't be compared.
     * The resulting type must implement the {@link Comparable} interface,
     * so the {@link Comparable#compareTo(Object)} method is
     * used to compare the two elements.
     * <p>
     * <code>null</code> is always less than a non-null element.
     * 
     * @throws CantCompareException if the two elements can't be compared
     * together.
     */
    public ValidationBuilderKey validationEquivalentOrGreater(Object reference) throws CantCompareException;

    /**
     * Prefix all the currently existing validation keys in this Set
     * with the specified prefix.
     * 
     * @return itself (fluent style)
     */
    public ValidationSet prefixValidationKeys(String prefix);

}

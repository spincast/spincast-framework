package org.spincast.core.validation;

public interface JsonObjectValidationSet extends ValidationSet {

    @Override
    public JsonObjectValidationBuilderKey validation(SimpleValidator validator);

    @Override
    public JsonObjectValidationBuilderKey validationNotBlank();

    @Override
    public JsonObjectValidationBuilderKey validationBlank();

    @Override
    public JsonObjectValidationBuilderKey validationEmail();

    @Override
    public JsonObjectValidationBuilderKey validationNull();

    @Override
    public JsonObjectValidationBuilderKey validationNotNull();

    @Override
    public JsonObjectValidationBuilderKey validationPattern(String pattern);

    @Override
    public JsonObjectValidationBuilderKey validationNotPattern(String pattern);

    @Override
    public JsonObjectValidationBuilderKey validationSize(int size, boolean ignoreNullValues);

    @Override
    public JsonObjectValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues);

    @Override
    public JsonObjectValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues);

    @Override
    public JsonObjectValidationBuilderKey validationLength(int length);

    @Override
    public JsonObjectValidationBuilderKey validationMinLength(int minLength);

    @Override
    public JsonObjectValidationBuilderKey validationMaxLength(int maxLength);

    @Override
    public JsonObjectValidationBuilderKey validationEquivalent(Object reference);

    @Override
    public JsonObjectValidationBuilderKey validationNotEquivalent(Object reference);

    @Override
    public JsonObjectValidationBuilderKey validationLess(Object reference);

    @Override
    public JsonObjectValidationBuilderKey validationGreater(Object reference);

    @Override
    public JsonObjectValidationBuilderKey validationEquivalentOrLess(Object reference);

    @Override
    public JsonObjectValidationBuilderKey validationEquivalentOrGreater(Object reference);

    /**
     * Starts the creation of a "is of type or null" validation. 
     * 
     * @param referenceType the type to validate agains.
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeOrNull(Class<?> referenceType);

    /**
     * Starts the creation of a "is of type String or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeStringOrNull();

    /**
     * Starts the creation of a "is of type Integer or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeIntegerOrNull();

    /**
     * Starts the creation of a "is of type Long or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeLongOrNull();

    /**
     * Starts the creation of a "is of type Float or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeFloatOrNull();

    /**
     * Starts the creation of a "is of type Double or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeDoubleOrNull();

    /**
     * Starts the creation of a "is of type Boolean or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeBooleanOrNull();

    /**
     * Starts the creation of a "is of type BigDecimal or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeBigDecimalOrNull();

    /**
     * Starts the creation of a "is of type byte[] or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeByteArrayOrNull(boolean acceptBase64StringToo);

    /**
     * Starts the creation of a "is of type Date or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeDateOrNull();

    /**
     * Starts the creation of a "is of type JsonObject or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeJsonObjectOrNull();

    /**
     * Starts the creation of a "is of type JsonArray or null" validation. 
     */
    public JsonObjectValidationBuilderKey validationIsOfTypeJsonArrayOrNull();

    /**
     * Starts the creation of a "can be converted to a String" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToString();

    /**
     * Starts the creation of a "can be converted to an Integer" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToInteger();

    /**
     * Starts the creation of a "can be converted to a Long" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToLong();

    /**
     * Starts the creation of a "can be converted to a Float" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToFloat();

    /**
     * Starts the creation of a "can be converted to a Boolean" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToBoolean();

    /**
     * Starts the creation of a "can be converted to a Date" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToDate();

    /**
     * Starts the creation of a "can be converted to a Double" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToDouble();

    /**
     * Starts the creation of a "can be converted to a BigDecimal" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToBigDecimal();

    /**
     * Starts the creation of a "can be converted to a byte[]" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToByteArray();

    /**
     * Starts the creation of a "can be converted to a JsonObject" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToJsonObject();

    /**
     * Starts the creation of a "can be converted to a JsonArray" validation. 
     */
    public JsonObjectValidationBuilderKey validationCanBeConvertedToJsonArray();

}

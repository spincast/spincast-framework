package org.spincast.core.validation;

public interface JsonArrayValidationSet extends JsonObjectValidationSet {

    @Override
    public JsonArrayValidationBuilderKey validation(SimpleValidator validator);

    @Override
    public JsonArrayValidationBuilderKey validationNotBlank();

    @Override
    public JsonArrayValidationBuilderKey validationBlank();

    @Override
    public JsonArrayValidationBuilderKey validationEmail();

    @Override
    public JsonArrayValidationBuilderKey validationNull();

    @Override
    public JsonArrayValidationBuilderKey validationNotNull();

    @Override
    public JsonArrayValidationBuilderKey validationPattern(String pattern);

    @Override
    public JsonArrayValidationBuilderKey validationNotPattern(String pattern);

    @Override
    public JsonArrayValidationBuilderKey validationSize(int size, boolean ignoreNullValues);

    @Override
    public JsonArrayValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues);

    @Override
    public JsonArrayValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues);

    @Override
    public JsonArrayValidationBuilderKey validationLength(int length);

    @Override
    public JsonArrayValidationBuilderKey validationMinLength(int minLength);

    @Override
    public JsonArrayValidationBuilderKey validationMaxLength(int maxLength);

    @Override
    public JsonArrayValidationBuilderKey validationEquivalent(Object reference);

    @Override
    public JsonArrayValidationBuilderKey validationNotEquivalent(Object reference);

    @Override
    public JsonArrayValidationBuilderKey validationLess(Object reference);

    @Override
    public JsonArrayValidationBuilderKey validationGreater(Object reference);

    @Override
    public JsonArrayValidationBuilderKey validationEquivalentOrLess(Object reference);

    @Override
    public JsonArrayValidationBuilderKey validationEquivalentOrGreater(Object reference);

    /**
     * Starts the creation of a "is of type or null" validation. 
     * 
     * @param referenceType the type to validate agains.
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeOrNull(Class<?> referenceType);

    /**
     * Starts the creation of a "is of type String or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeStringOrNull();

    /**
     * Starts the creation of a "is of type Integer or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeIntegerOrNull();

    /**
     * Starts the creation of a "is of type Long or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeLongOrNull();

    /**
     * Starts the creation of a "is of type Float or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeFloatOrNull();

    /**
     * Starts the creation of a "is of type Double or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeDoubleOrNull();

    /**
     * Starts the creation of a "is of type Boolean or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeBooleanOrNull();

    /**
     * Starts the creation of a "is of type BigDecimal or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeBigDecimalOrNull();

    /**
     * Starts the creation of a "is of type byte[] or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeByteArrayOrNull(boolean acceptBase64StringToo);

    /**
     * Starts the creation of a "is of type Date or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeDateOrNull();

    /**
     * Starts the creation of a "is of type JsonObject or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeJsonObjectOrNull();

    /**
     * Starts the creation of a "is of type JsonArray or null" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeJsonArrayOrNull();

    /**
     * Starts the creation of a "can be converted to a String" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToString();

    /**
     * Starts the creation of a "can be converted to an Integer" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToInteger();

    /**
     * Starts the creation of a "can be converted to a Long" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToLong();

    /**
     * Starts the creation of a "can be converted to a Float" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToFloat();

    /**
     * Starts the creation of a "can be converted to a Boolean" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToBoolean();

    /**
     * Starts the creation of a "can be converted to a Date" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToDate();

    /**
     * Starts the creation of a "can be converted to a Double" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToDouble();

    /**
     * Starts the creation of a "can be converted to a BigDecimal" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToBigDecimal();

    /**
     * Starts the creation of a "can be converted to a byte[]" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToByteArray();

    /**
     * Starts the creation of a "can be converted to a JsonObject" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToJsonObject();

    /**
     * Starts the creation of a "can be converted to a JsonArray" validation. 
     */
    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToJsonArray();

}

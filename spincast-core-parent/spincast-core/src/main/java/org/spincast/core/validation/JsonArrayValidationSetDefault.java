package org.spincast.core.validation;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.xml.XmlManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class JsonArrayValidationSetDefault extends JsonObjectValidationSetDefault implements JsonArrayValidationSet {

    private Map<String, SimpleValidator> isOfTypeValidators;
    private Map<String, SimpleValidator> canBeConvertedToValidators;

    @AssistedInject
    public JsonArrayValidationSetDefault(@Assisted JsonArray validatedObject,
                                         ValidationFactory validationFactory,
                                         SpincastDictionary spincastDictionary,
                                         JsonManager jsonManager,
                                         XmlManager xmlManager,
                                         ObjectConverter objectConverter) {
        super(validatedObject,
              validationFactory,
              spincastDictionary,
              jsonManager,
              xmlManager,
              objectConverter);
    }

    @Override
    protected JsonArray getValidatedObject() {
        return (JsonArray)super.getValidatedObject();
    }

    @Override
    protected Map<String, SimpleValidator> getIsOfTypeValidators() {

        if(this.isOfTypeValidators == null) {
            this.isOfTypeValidators = new HashMap<String, SimpleValidator>();
        }

        return this.isOfTypeValidators;
    }

    @Override
    protected Map<String, SimpleValidator> getCanBeConvertedToValidators() {

        if(this.canBeConvertedToValidators == null) {
            this.canBeConvertedToValidators = new HashMap<String, SimpleValidator>();
        }

        return this.canBeConvertedToValidators;
    }

    @Override
    public JsonArrayValidationBuilderKey validation(SimpleValidator validator) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          validator);
    }

    @Override
    public JsonArrayValidationBuilderKey validationNotBlank() {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getNotBlankValidatorInfo());
    }

    @Override
    public JsonArrayValidationBuilderKey validationBlank() {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getBlankValidatorInfo());
    }

    @Override
    public JsonArrayValidationBuilderKey validationEmail() {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getEmailValidatorInfo());
    }

    @Override
    public JsonArrayValidationBuilderKey validationNull() {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getNullValidatorInfo());
    }

    @Override
    public JsonArrayValidationBuilderKey validationNotNull() {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getNotNullValidatorInfo());
    }

    @Override
    public JsonArrayValidationBuilderKey validationPattern(String pattern) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getPatternValidator(pattern));
    }

    @Override
    public JsonArrayValidationBuilderKey validationNotPattern(String pattern) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getNotPatternValidator(pattern));
    }

    @Override
    public JsonArrayValidationBuilderKey validationSize(int size, boolean ignoreNullValues) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getSizeValidator(size, ignoreNullValues));
    }

    @Override
    public JsonArrayValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getMinSizeValidator(minSize, ignoreNullValues));
    }

    @Override
    public JsonArrayValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getMaxSizeValidator(maxSize, ignoreNullValues));
    }

    @Override
    public JsonArrayValidationBuilderKey validationLength(int length) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getLengthValidator(length));
    }

    @Override
    public JsonArrayValidationBuilderKey validationMinLength(int minLength) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getMinLengthValidator(minLength));
    }

    @Override
    public JsonArrayValidationBuilderKey validationMaxLength(int maxLength) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getMaxLengthValidator(maxLength));
    }

    @Override
    public JsonArrayValidationBuilderKey validationEquivalent(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getEquivalentValidator(reference));
    }

    @Override
    public JsonArrayValidationBuilderKey validationNotEquivalent(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getNotEquivalentValidator(reference));
    }

    @Override
    public JsonArrayValidationBuilderKey validationLess(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getLessValidator(reference));
    }

    @Override
    public JsonArrayValidationBuilderKey validationGreater(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getGreaterValidator(reference));
    };

    @Override
    public JsonArrayValidationBuilderKey validationEquivalentOrLess(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getEquivalentOrLessValidator(reference));
    }

    @Override
    public JsonArrayValidationBuilderKey validationEquivalentOrGreater(Object reference) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getEquivalentOrGreaterValidator(reference));
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeOrNull(Class<?> referenceType) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getIsOfTypeValidator(referenceType));
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeStringOrNull() {
        return validationIsOfTypeOrNull(String.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeIntegerOrNull() {
        return validationIsOfTypeOrNull(Integer.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeLongOrNull() {
        return validationIsOfTypeOrNull(Long.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeFloatOrNull() {
        return validationIsOfTypeOrNull(Float.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeDoubleOrNull() {
        return validationIsOfTypeOrNull(Double.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeBooleanOrNull() {
        return validationIsOfTypeOrNull(Boolean.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeBigDecimalOrNull() {
        return validationIsOfTypeOrNull(BigDecimal.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeByteArrayOrNull(boolean acceptBase64StringToo) {
        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getIsOfTypeByteArrayValidator(acceptBase64StringToo));
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeDateOrNull() {
        return validationIsOfTypeOrNull(Date.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeJsonObjectOrNull() {
        return validationIsOfTypeOrNull(JsonObject.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationIsOfTypeJsonArrayOrNull() {
        return validationIsOfTypeOrNull(JsonArray.class);
    }

    @Override
    protected JsonArrayValidationBuilderKey validationCanBeConvertedTo(Class<?> referenceType) {

        return getValidationFactory().createJsonArrayValidationBuilderKey(this,
                                                                          getValidatedObject(),
                                                                          getCanBeConvertedToValidator(referenceType));
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToString() {
        return validationCanBeConvertedTo(String.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToInteger() {
        return validationCanBeConvertedTo(Integer.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToLong() {
        return validationCanBeConvertedTo(Long.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToFloat() {
        return validationCanBeConvertedTo(Float.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToBoolean() {
        return validationCanBeConvertedTo(Boolean.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToDate() {
        return validationCanBeConvertedTo(Date.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToDouble() {
        return validationCanBeConvertedTo(Double.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToBigDecimal() {
        return validationCanBeConvertedTo(BigDecimal.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToByteArray() {
        return validationCanBeConvertedTo(byte[].class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToJsonObject() {
        return validationCanBeConvertedTo(JsonObject.class);
    }

    @Override
    public JsonArrayValidationBuilderKey validationCanBeConvertedToJsonArray() {
        return validationCanBeConvertedTo(JsonArray.class);
    }

}

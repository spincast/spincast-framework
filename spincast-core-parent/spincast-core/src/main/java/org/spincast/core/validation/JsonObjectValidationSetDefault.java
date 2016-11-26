package org.spincast.core.validation;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectOrArray;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.xml.XmlManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class JsonObjectValidationSetDefault extends ValidationSetDefault implements JsonObjectValidationSet {

    private final JsonObjectOrArray validatedObject;

    private Map<String, SimpleValidator> isOfTypeValidators;
    private Map<String, SimpleValidator> canBeConvertedToValidators;

    @AssistedInject
    public JsonObjectValidationSetDefault(@Assisted JsonObject validatedObject,
                                          ValidationFactory validationFactory,
                                          SpincastDictionary spincastDictionary,
                                          JsonManager jsonManager,
                                          XmlManager xmlManager,
                                          ObjectConverter objectConverter) {
        this((JsonObjectOrArray)validatedObject,
             validationFactory,
             spincastDictionary,
             jsonManager,
             xmlManager,
             objectConverter);
    }

    public JsonObjectValidationSetDefault(JsonObjectOrArray validatedObject,
                                          ValidationFactory validationFactory,
                                          SpincastDictionary spincastDictionary,
                                          JsonManager jsonManager,
                                          XmlManager xmlManager,
                                          ObjectConverter objectConverter) {
        super(validationFactory,
              spincastDictionary,
              jsonManager,
              xmlManager,
              objectConverter);
        this.validatedObject = validatedObject;
    }

    protected JsonObjectOrArray getValidatedObject() {
        return this.validatedObject;
    }

    protected Map<String, SimpleValidator> getIsOfTypeValidators() {

        if(this.isOfTypeValidators == null) {
            this.isOfTypeValidators = new HashMap<String, SimpleValidator>();
        }

        return this.isOfTypeValidators;
    }

    protected Map<String, SimpleValidator> getCanBeConvertedToValidators() {

        if(this.canBeConvertedToValidators == null) {
            this.canBeConvertedToValidators = new HashMap<String, SimpleValidator>();
        }

        return this.canBeConvertedToValidators;
    }

    @Override
    public JsonObjectValidationBuilderKey validation(SimpleValidator validator) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           validator);
    }

    @Override
    public JsonObjectValidationBuilderKey validationNotBlank() {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getNotBlankValidatorInfo());
    }

    @Override
    public JsonObjectValidationBuilderKey validationBlank() {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getBlankValidatorInfo());
    }

    @Override
    public JsonObjectValidationBuilderKey validationEmail() {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getEmailValidatorInfo());
    }

    @Override
    public JsonObjectValidationBuilderKey validationNull() {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getNullValidatorInfo());
    }

    @Override
    public JsonObjectValidationBuilderKey validationNotNull() {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getNotNullValidatorInfo());
    }

    @Override
    public JsonObjectValidationBuilderKey validationPattern(String pattern) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getPatternValidator(pattern));
    }

    @Override
    public JsonObjectValidationBuilderKey validationNotPattern(String pattern) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getNotPatternValidator(pattern));
    }

    @Override
    public JsonObjectValidationBuilderKey validationSize(int size, boolean ignoreNullValues) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getSizeValidator(size, ignoreNullValues));
    }

    @Override
    public JsonObjectValidationBuilderKey validationMinSize(int minSize, boolean ignoreNullValues) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getMinSizeValidator(minSize, ignoreNullValues));
    }

    @Override
    public JsonObjectValidationBuilderKey validationMaxSize(int maxSize, boolean ignoreNullValues) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getMaxSizeValidator(maxSize, ignoreNullValues));
    }

    @Override
    public JsonObjectValidationBuilderKey validationLength(int length) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getLengthValidator(length));
    }

    @Override
    public JsonObjectValidationBuilderKey validationMinLength(int minLength) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getMinLengthValidator(minLength));
    }

    @Override
    public JsonObjectValidationBuilderKey validationMaxLength(int maxLength) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getMaxLengthValidator(maxLength));
    }

    @Override
    public JsonObjectValidationBuilderKey validationEquivalent(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getEquivalentValidator(reference));
    }

    @Override
    public JsonObjectValidationBuilderKey validationNotEquivalent(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getNotEquivalentValidator(reference));
    }

    @Override
    public JsonObjectValidationBuilderKey validationLess(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getLessValidator(reference));
    }

    @Override
    public JsonObjectValidationBuilderKey validationGreater(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getGreaterValidator(reference));
    };

    @Override
    public JsonObjectValidationBuilderKey validationEquivalentOrLess(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getEquivalentOrLessValidator(reference));
    }

    @Override
    public JsonObjectValidationBuilderKey validationEquivalentOrGreater(Object reference) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getEquivalentOrGreaterValidator(reference));
    }

    protected SimpleValidator getIsOfTypeValidator(final Class<?> referenceType) {

        Objects.requireNonNull(referenceType, "The reference type can't be NULL");

        SimpleValidator validator = getIsOfTypeValidators().get(referenceType);
        if(validator == null) {
            validator = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = elementToValidate == null || referenceType.isAssignableFrom(elementToValidate.getClass());
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_IS_OF_TYPE;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_is_of_type_default_text(referenceType.getSimpleName());
                }
            };
            getIsOfTypeValidators().put(referenceType.getName(), validator);
        }

        return validator;
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeOrNull(Class<?> referenceType) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getIsOfTypeValidator(referenceType));
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeStringOrNull() {
        return validationIsOfTypeOrNull(String.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeIntegerOrNull() {
        return validationIsOfTypeOrNull(Integer.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeLongOrNull() {
        return validationIsOfTypeOrNull(Long.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeFloatOrNull() {
        return validationIsOfTypeOrNull(Float.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeDoubleOrNull() {
        return validationIsOfTypeOrNull(Double.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeBooleanOrNull() {
        return validationIsOfTypeOrNull(Boolean.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeBigDecimalOrNull() {
        return validationIsOfTypeOrNull(BigDecimal.class);
    }

    protected SimpleValidator getIsOfTypeByteArrayValidator(final boolean acceptBase64StringToo) {

        String validatorKey = "ByteArray" + String.valueOf(acceptBase64StringToo);

        SimpleValidator validator = getIsOfTypeValidators().get(validatorKey);
        if(validator == null) {
            validator = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {

                    boolean valid = elementToValidate == null || elementToValidate instanceof byte[] ||
                                    (acceptBase64StringToo && getObjectConverter().isBase64StringOrNull(elementToValidate));
                    return valid;
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_IS_OF_TYPE;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_is_of_type_default_text("byte[]");
                }
            };
            getIsOfTypeValidators().put(validatorKey, validator);
        }

        return validator;
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeByteArrayOrNull(boolean acceptBase64StringToo) {
        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getIsOfTypeByteArrayValidator(acceptBase64StringToo));
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeDateOrNull() {
        return validationIsOfTypeOrNull(Date.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeJsonObjectOrNull() {
        return validationIsOfTypeOrNull(JsonObject.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationIsOfTypeJsonArrayOrNull() {
        return validationIsOfTypeOrNull(JsonArray.class);
    }

    protected SimpleValidator getCanBeConvertedToValidator(final Class<?> referenceType) {

        SimpleValidator validator = getCanBeConvertedToValidators().get(referenceType);
        if(validator == null) {
            validator = new SimpleValidator() {

                @Override
                public boolean validate(Object elementToValidate) {
                    return getObjectConverter().isCanBeConvertedTo(referenceType, elementToValidate);
                }

                @Override
                public String getCode() {
                    return VALIDATION_CODE_CAN_BE_CONVERTED_TO;
                }

                @Override
                public String getSuccessMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_success_message_default_text();
                }

                @Override
                public String getFailMessage(Object elementToValidate) {
                    return getSpincastDictionary().validation_can_be_converted_to_default_text(referenceType.getSimpleName());
                }
            };
            getCanBeConvertedToValidators().put((referenceType != null ? referenceType.getName() : null), validator);
        }

        return validator;
    }

    protected JsonObjectValidationBuilderKey validationCanBeConvertedTo(Class<?> referenceType) {

        return getValidationFactory().createJsonObjectValidationBuilderKey(this,
                                                                           getValidatedObject(),
                                                                           getCanBeConvertedToValidator(referenceType));
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToString() {
        return validationCanBeConvertedTo(String.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToInteger() {
        return validationCanBeConvertedTo(Integer.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToLong() {
        return validationCanBeConvertedTo(Long.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToFloat() {
        return validationCanBeConvertedTo(Float.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToBoolean() {
        return validationCanBeConvertedTo(Boolean.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToDate() {
        return validationCanBeConvertedTo(Date.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToDouble() {
        return validationCanBeConvertedTo(Double.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToBigDecimal() {
        return validationCanBeConvertedTo(BigDecimal.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToByteArray() {
        return validationCanBeConvertedTo(byte[].class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToJsonObject() {
        return validationCanBeConvertedTo(JsonObject.class);
    }

    @Override
    public JsonObjectValidationBuilderKey validationCanBeConvertedToJsonArray() {
        return validationCanBeConvertedTo(JsonArray.class);
    }

}

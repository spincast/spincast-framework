package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class EquivalentValidationTest extends ValidationTestBase {

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentNullValue() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5L)
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentNullreference() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(null)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentNullValueAndNullreference() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent((String)null)
                                                  .key("key1")
                                                  .element((String)null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentLess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentEqual() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .key("key1")
                                                  .element(5)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentGreater() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .key("key1")
                                                  .element(6)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectWithConversionValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "5");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOnJsonObjectWithConversionInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "6");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectCantConvert() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    /**
     * A null (not found) value is less than a non null value.
     */
    @Test
    public void validateEquivalentOnJsonObjectNotFound() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("nope", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    /**
     * A null value is less than a non null value.
     */
    @Test
    public void validateEquivalentOnJsonObjectNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectNullAndreferenceoNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOnJsonObjectreferenceNullCantBeConverted() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOnJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentBigDecimalValid() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(new BigDecimal("5"))
                                                  .key("key1")
                                                  .element(new BigDecimal("5"))
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentBigDecimalInvalid() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(new BigDecimal("5"))
                                                  .key("key1")
                                                  .element(new BigDecimal("6"))
                                                  .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentOnJsonObjectBigDecimalEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOnJsonObjectBigDecimalGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectDateStrEquals() throws Exception {

        Date reference = SpincastTestUtils.getTestDateNoTime();
        Date dateToCompare = reference;

        JsonObject obj = getJsonManager().create();
        obj.put("key1", getObjectConverter().convertToJsonDateFormat(dateToCompare));

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(reference).jsonPath("key1").validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());

        validation = validationFinal.validationLess(reference).jsonPath("key1").validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectDateStrLess() throws Exception {

        Date reference = SpincastTestUtils.getTestDateNoTime();
        Date dateToCompare = DateUtils.addMinutes(reference, -1);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", getObjectConverter().convertToJsonDateFormat(dateToCompare));

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        ValidationSet validation = builder.validationLess(reference).jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());

        validation = builder.validationEquivalent(reference).jsonPath("key1").validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());

        validation = builder.validationGreater(reference).jsonPath("key1").validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateEquivalentByteArrayEqual() throws Exception {

        byte[] toCompare = new byte[]{1, 2, 3};
        byte[] reference = new byte[]{1, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentByteArrayNotEqual() throws Exception {

        byte[] toCompare = new byte[]{1, 2, 3};
        byte[] reference = new byte[]{1, 2, 4};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentByteArrayAndBase64StrEqual() throws Exception {

        byte[] reference = new byte[]{1, 2, 3};
        byte[] toCompare = new byte[]{1, 2, 3};

        String toCompareStr = getObjectConverter().convertByteArrayToBase64String(toCompare);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", toCompareStr);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentByteArrayAndBase64StrNotEqual() throws Exception {

        byte[] reference = new byte[]{1, 2, 3};
        byte[] toCompare = new byte[]{1, 2, 4};

        String toCompareStr = getObjectConverter().convertByteArrayToBase64String(toCompare);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", toCompareStr);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentByteArrayAndBase64StrInvalidStr() throws Exception {

        byte[] reference = new byte[]{1, 2, 3};
        byte[] toCompare = new byte[]{1, 2, 3};

        String toCompareStr = getObjectConverter().convertByteArrayToBase64String(toCompare) + "nope";

        JsonObject obj = getJsonManager().create();
        obj.put("key1", toCompareStr);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentJsonObjectEqual() throws Exception {

        JsonObject toCompareInner = getJsonManager().create();
        toCompareInner.put("k1", 123);
        toCompareInner.put("k2", new BigDecimal("33"));

        JsonObject toCompare = getJsonManager().create();
        toCompare.put("k1", "v1");
        toCompare.put("k2", toCompareInner);

        JsonObject referenceInner = getJsonManager().create();
        referenceInner.put("k1", "123");
        referenceInner.put("k2", 33L);

        JsonObject reference = getJsonManager().create();
        reference.put("k1", "v1");
        reference.put("k2", referenceInner);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentJsonObjectNotEqual() throws Exception {

        JsonObject toCompareInner = getJsonManager().create();
        toCompareInner.put("k1", 123);
        toCompareInner.put("k2", new BigDecimal("34")); // 34, not 33!

        JsonObject toCompare = getJsonManager().create();
        toCompare.put("k1", "v1");
        toCompare.put("k2", toCompareInner);

        JsonObject referenceInner = getJsonManager().create();
        referenceInner.put("k1", "123");
        referenceInner.put("k2", 33L);

        JsonObject reference = getJsonManager().create();
        reference.put("k1", "v1");
        reference.put("k2", referenceInner);

        ValidationSet builder = getValidationFactory().createValidationSet();
        assertNotNull(builder);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentJsonArrayEqual() throws Exception {

        JsonArray toCompareInner = getJsonManager().createArray();
        toCompareInner.add(123);
        toCompareInner.add(new BigDecimal("33"));

        JsonArray toCompare = getJsonManager().createArray();
        toCompare.add("v1");
        toCompare.add(toCompareInner);

        JsonArray referenceInner = getJsonManager().createArray();
        referenceInner.add("123");
        referenceInner.add(33L);

        JsonArray reference = getJsonManager().createArray();
        reference.add("v1");
        reference.add(referenceInner);

        ValidationSet builder = getValidationFactory().createValidationSet();
        assertNotNull(builder);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentJsonArrayNotEqual() throws Exception {

        JsonArray toCompareInner = getJsonManager().createArray();
        toCompareInner.add(124); // not 123
        toCompareInner.add(new BigDecimal("33"));

        JsonArray toCompare = getJsonManager().createArray();
        toCompare.add("v1");
        toCompare.add(toCompareInner);

        JsonArray referenceInner = getJsonManager().createArray();
        referenceInner.add("123");
        referenceInner.add(33L);

        JsonArray reference = getJsonManager().createArray();
        reference.add("v1");
        reference.add(referenceInner);

        ValidationSet builder = getValidationFactory().createValidationSet();
        assertNotNull(builder);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void validateEquivalentJsonObjectAndJsonArrayEqual() throws Exception {

        JsonObject objInner = getJsonManager().create();
        objInner.put("k1", "123");
        objInner.put("k2", 33L);

        JsonArray toCompareInner = getJsonManager().createArray();
        toCompareInner.add(123);
        toCompareInner.add(objInner);

        JsonArray toCompare = getJsonManager().createArray();
        toCompare.add("v1");
        toCompare.add(toCompareInner);

        JsonObject obj2Inner = getJsonManager().create();
        obj2Inner.put("k1", 123);
        obj2Inner.put("k2", new BigDecimal("33"));

        JsonArray referenceInner = getJsonManager().createArray();
        referenceInner.add("123");
        referenceInner.add(obj2Inner);

        JsonArray reference = getJsonManager().createArray();
        reference.add("v1");
        reference.add(referenceInner);

        ValidationSet builder = getValidationFactory().createValidationSet();
        assertNotNull(builder);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentJsonObjectAndJsonArrayNotEqual() throws Exception {

        JsonObject objInner = getJsonManager().create();
        objInner.put("k1", "124"); // not 123
        objInner.put("k2", 33L);

        JsonArray toCompareInner = getJsonManager().createArray();
        toCompareInner.add(123);
        toCompareInner.add(objInner);

        JsonArray toCompare = getJsonManager().createArray();
        toCompare.add("v1");
        toCompare.add(toCompareInner);

        JsonObject obj2Inner = getJsonManager().create();
        obj2Inner.put("k1", 123);
        obj2Inner.put("k2", new BigDecimal("33"));

        JsonArray referenceInner = getJsonManager().createArray();
        referenceInner.add("123");
        referenceInner.add(obj2Inner);

        JsonArray reference = getJsonManager().createArray();
        reference.add("v1");
        reference.add(referenceInner);

        ValidationSet builder = getValidationFactory().createValidationSet();
        assertNotNull(builder);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalent(reference)
                                                  .key("key1")
                                                  .element(toCompare)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
    }

    @Test
    public void equalsOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(new BigDecimal("123"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(123L)
                                                  .jsonPathAll("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void equalsOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(123L)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validation.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void notAnArray() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalent(123L)
                                                  .jsonPathAll("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_AN_ARRAY, message.getCode());
        assertNotNull(message.getText());
    }

}
